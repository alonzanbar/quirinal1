package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;

import edu.usc.ict.iago.agent.AgentUtilsExtension;
import edu.usc.ict.iago.agent.IAGOCoreMessage;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Preference;



public class IAGOQuirinalMessage extends IAGOCoreMessage implements MessagePolicy {
	protected final String[] proposal = {"I think this deal is acceptable.", 
						 			   "I think you'll find this offer to be satisfactory.", 
						 			   "Check out this arrangement. I see it fair.", 
						 			   "I think this deal will interest you.",
									   "I believe you will like this offer."};
	protected final String[] acceptResponse = {
											 "Great.",
											 "Awesome!",
											 "I'm glad we could come to an agreement!",
											 "Sounds good!"};
	protected final String[] rejectResponse = {
											 "Oh, that's too bad!",
											 "Ah well, perhaps another time.",
											 "Alright, maybe we try something different next time.",
											 "I think you should have reconsidered. It was a good deal."
										   	 "Although you rejected the offer, I still believe we can find one that suits us both."};
	
	protected final String[] vhReject = {
			 "Is that your offer? I know you can do better then that.",
			 "I'm sorry, but that won't work for me.",
			 "This offer is no good for me. Maybe change it a little bit.",
			 "You and I can do better than this."
			 };
	
	protected final String[] vhAccept = {
			 "Your offer is acceptable.",
			 "Cool! This is a nice arrangement.",
			 "This is what I'm talking about, a fine deal for both of us.",
			 "Yes.  This deal will work."
			 "Thanks. I knew we could reach an agreement."
			 "You know what you are doing. I'm in."};
	
	protected final String[] messageResponse = {"Hmmm", "Well...","Huh?" "I'm not quite sure how to treat this piece of information", "My responses are unambiguous. You just need to ask the right questions."};
	
	private AgentUtilsExtension utils;
	private ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
	
	@Override
	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;
	}
	
	@Override
	public void updateOrderings (ArrayList<ArrayList<Integer>> orderings)
	{
		this.orderings = orderings;
	}
	
	public String getProposalLang(History history, GameSpec game){
		return proposal[(int)(Math.random()*proposal.length)];
	}
	
	public String getAcceptLang(History history, GameSpec game){
		return acceptResponse[(int)(Math.random()*acceptResponse.length)];
	}
	
	public String getRejectLang(History history, GameSpec game){
		return rejectResponse[(int)(Math.random()*rejectResponse.length)];
	}
	
	public String getVHAcceptLang(History history, GameSpec game){
		return vhAccept[(int)(Math.random()*vhAccept.length)];
	}
	
	public String getVHRejectLang(History history, GameSpec game){
		return vhReject[(int)(Math.random()*vhReject.length)];
	}
	
	private String getEmotionResponse(History history, GameSpec game) {
		Event e = history.getPlayerHistory().getLast();
		
		if (e.getType() != Event.EventClass.SEND_EXPRESSION)
			throw new UnsupportedOperationException("The last event wasn't an expresion--this method is inappropriate.");
		
		if(e.getMessage().equals("sad") || e.getMessage().equals("angry"))
			return "I'm sorry, have I upset you?";
		else if(e.getMessage().equals("happy"))
			return "Great! I'm glad you're happy with how things are going.";
		else if(e.getMessage().equals("surprised"))
			return "Hmm. Have I said something unexpected?";
		return "I don't know what face you just made!";
	}
	public String getMessageResponse(History history, GameSpec game) 
	{
		int timeCounter = 0;
		Event ePrime = history.getPlayerHistory().getLast();
		if (ePrime.getType() == Event.EventClass.SEND_EXPRESSION)
			return getEmotionResponse(history, game);
		
		if (ePrime.getType() == Event.EventClass.TIME) {
			timeCounter++;
			if (4 == timeCounter && ) {
				timeCounter = 0;
				return "Let's make some progress. Do you have any questions or offers?";
			}
		}
		Preference p = ePrime.getPreference();
		if (p != null) //a preference was expressed
		{
			Relation myRelation;
			if (p.getRelation() == Relation.BEST)
			{
				return "Great. Let's see how we use this to reach an agreement."
				//return "I like " + this.findVHItem(2, game) + " the best!";
			}
			else if (p.getRelation() == Relation.WORST)
			{
				return "I see. I like " + this.findVHItem(game.getNumIssues(), game) + " the least.";
			}
			else
			{
				if(p.getIssue1() == -1 || p.getIssue2() == -1)
					return "Can you be a little more specific please? Saying \"something\" is a little confusing.";
				int value1 = game.getSimpleVHPoints().get(game.getIssuePluralNames()[p.getIssue1()]);
				int value2 = game.getSimpleVHPoints().get(game.getIssuePluralNames()[p.getIssue2()]);
				if(value1 > value2)
					myRelation = Relation.GREATER_THAN;
				else if (value2 > value1)
					myRelation = Relation.LESS_THAN;
				else
					myRelation = Relation.EQUAL;
				return prefToEnglish(new Preference(p.getIssue1(), p.getIssue2(), myRelation, false), game);

			}
		}
		ServletUtils.log("No preference detected in user message.", ServletUtils.DebugLevels.DEBUG);
		
		//details for each response
		int code = history.getPlayerHistory().getLast().getMessageCode();
		if (code == -1)
		{
			ServletUtils.log("MessageCode missing!", ServletUtils.DebugLevels.WARN);
		}
		
		//Best and worst offers
		int best = findBest();
		int worst = findWorst(game);
		String resp = "";
		
		//How many offers have the player and agent suggested.
		int playerOfferCount = 0;
		for(Event e: history.getPlayerHistory())
			if(e.getType() == Event.EventClass.SEND_OFFER)
				playerOfferCount++;
		
		int offerCount = 0;
		for(Event e: history.getHistory())
			if(e.getType() == Event.EventClass.SEND_OFFER)
				offerCount++;
		
		//Last offer and are all items splited
		boolean isFull = true;
		Event lastOffer = null;
		if (offerCount > 0)
		{
			int index = history.getHistory().size() - 1;
			lastOffer = history.getHistory().get(index);
			while (lastOffer.getType() != Event.EventClass.SEND_OFFER)
			{
				index--;
				lastOffer = history.getHistory().get(index);
			}
			Offer o = lastOffer.getOffer();
			for (int i = 0; i < o.getIssueCount(); i++)
			{
				if(o.getItem(i)[1] != 0)//some undecided items
					isFull = false;
			}
		}
		
		switch(code)
		{
			case 0:
				resp = "I couldn't agree more. We should be happy with our deal.";
				break;
			case 1:
				if(playerOfferCount <= 1)
					resp = "You still haven't made many offers. Don't be afraid to suggest some!";
				else
				{
					int index = history.getPlayerHistory().size() - 1;
					Event lastPlayerOffer = history.getPlayerHistory().get(index);
					while (lastPlayerOffer.getType() != Event.EventClass.SEND_OFFER)
					{
						index--;
						lastPlayerOffer = history.getPlayerHistory().get(index);
					}
					index--;
					Event prevPlayerOffer = history.getPlayerHistory().get(index);
					while (prevPlayerOffer.getType() != Event.EventClass.SEND_OFFER)
					{
						index--;
						prevPlayerOffer = history.getPlayerHistory().get(index);
					}
					
					if(utils.myActualOfferValue(lastPlayerOffer.getOffer()) > utils.myActualOfferValue(prevPlayerOffer.getOffer()))
					{
						if (best >= 0)
							resp = "Thank you for your offer. I believe you want " + game.getIssuePluralNames()[best] + ", am I correct?";
						else
							resp = "Thanks What item would you most like in return?";
					}
					else
						resp = "I truly appreciate the effort, but it wasn't the offer I was expecting.";
					
				}
				if (!isFull)
					resp += "  By the way, What about the rest of the items you left undecided?";
				break;
			case 2:
				resp = "You are right, but splitting all items evenly is not everything. We should find an evenly fair deal for both of us."
				break;
			case 3:
				if(best < 0)//we do not have any guess to their favorite
					resp = "I like your idea. What's your favorite item?";
				else
					resp = "True. Yours is " + game.getIssuePluralNames()[best] + ", right?";
				break;
			case 4:
				if(playerOfferCount <= 3)
					resp = "You really haven't made much offers. You can't threathen while you don't leave me any other options!";
				else
					resp = "I don't like that tone, but let me consider your offer.";
				if (!isFull)
					resp += " What about the rest of the items in the middle?";
				break;
			case 5:
				//player threat to shut down negotiations. need to know offer and playerbehavior policy to see if agent
				//should invoke a new offer, accept offer or break negotiations.
				break;
			case 6:
				//player says this is the best offer. need to know profit policy to accept offer or ask to change it.
				break;
			case 7:
				//might mean that player's profit is too low - can help telling favorite items of player
				resp = "If you think this offer sucks, why won't you suggest your own offer?";
				break;
			case 8:
				
			
				
				
				
				
			
		
		//return messageResponse[(int)(Math.random()*messageResponse.length)];
	}
	
	protected final String[] timeResponses = {
		"Our time is almost done! We need to hurry up.",
		"We are out of time, don't hesitate and let's reach consensus.",
		"There's no time left - we need to find a deal quickly."}
	
	@Override
	protected String getEndOfTimeResponse() {
		// TODO Auto-generated method stub
		return timeResponses[(int)(Math.random()*timeResponses.length)];
	}

	@Override
	protected String getSemiFairResponse() {
		// TODO Auto-generated method stub
		return "Your offer is not good enough; But we are getting there.";
	}

	@Override
	protected String getContradictionResponse(String drop) {
		// TODO Auto-generated method stub
		return "Wait a second. I remember you said earlier:" + drop + "Was that not true?";
	}

	
}
