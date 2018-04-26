package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.Map;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;
import edu.usc.ict.iago.utils.ServletUtils;



public class IAGONiceFreeMessage extends IAGOCoreMessage implements MessagePolicy {
	protected final String[] proposal = {"I think this deal is good for the both of us.", 
						 			   "I think you'll find this offer to be satisfactory.", 
						 			   "I think this arrangement is fair.", 
						 			   "I think this deal will interest you.",
									   "Please consider this deal?"};
	protected final String[] acceptResponse = {
											 "Great!",
											 "Wonderful!",
											 "I'm glad we could come to an agreement!",
											 "Sounds good!"};
	protected final String[] rejectResponse = {
											 "Oh that's too bad.",
											 "Ah well, perhaps another time.",
											 "Ok, maybe something different next time.",
											 "Alright."};
	
	protected final String[] vhReject = {
			 "I'm sorry, but I don't think that's fair to me.",
			 "Apologies, but that won't work for me.",
			 "Perhaps we should spend more time finding a solution that's good for us both...",
			 "I won't be able to accept that.  So sorry. :("
			 };
	
	protected final String[] vhAccept = {
			 "Your offer is good!",
			 "That seems like a good deal.",
			 "That will work for me.",
			 "Yes.  This deal will work."
			 };
	
	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;
	}
	
	private AgentUtilsExtension utils;
	private ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
	
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
			return "I'm glad you're happy with how things are going!";
		else if(e.getMessage().equals("surprised"))
			return "Have I said something unexpected?";
		return "I don't know what face you just made!";
	}
	
	protected String getEndOfTimeResponse() {
		return "We're almost out of time!  Accept this quickly!";
	}
	
	protected String getSemiFairResponse() {
		return "Unfortunately, I cannot accept.  But that's getting close to being fair.";
	}
	
	protected String getContradictionResponse(String drop) {
		return "I'm sorry.  I must be misunderstanding.  Earlier, you said: " + drop + " Was that not correct?";
	}

	public String getMessageResponse(History history, GameSpec game) {
		Event ePrime = history.getPlayerHistory().getLast();
		if (ePrime.getType() == Event.EventClass.SEND_EXPRESSION)
			return getEmotionResponse(history, game);
		
		if (ePrime.getType() == Event.EventClass.TIME)
			return "Can I provide more information to help us reach consensus?";
		
		Preference p = ePrime.getPreference();
		if (p != null) //a preference was expressed
		{
			Relation myRelation;
			if (p.getRelation() == Relation.BEST)
			{
				return "I like " + this.findVHItem(1, game) + " the best!";
			}
			else if (p.getRelation() == Relation.WORST)
			{
				return "I like " + this.findVHItem(game.getNumIssues(), game) + " the least.";
			}
			else
			{
				if(p.getIssue1() == -1 || p.getIssue2() == -1)
					return "Can you be a little more specific? Saying \"something\" is a little confusing.";
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
		
		int best = findBest();
		int worst = findWorst(game);
		String resp = "";
		
		int playerOfferCount = 0;
		for(Event e: history.getPlayerHistory())
			if(e.getType() == Event.EventClass.SEND_OFFER)
				playerOfferCount++;
		
		int offerCount = 0;
		for(Event e: history.getHistory())
			if(e.getType() == Event.EventClass.SEND_OFFER)
				offerCount++;
		
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
			case 2:
			case 3:
			case 9:
				if(best < 0)//we do not have any guess to their favorite
					resp = "I agree!  What is your favorite item?";
				else
					resp = "I agree!  Why don't we make sure you get your favorite item , and I get mine?  Yours is " + game.getIssuePluralNames()[best] + ", right?";
				break;
			case 1:				
				if(playerOfferCount <= 1)
					resp = "Don't worry about giving ground!  We're just starting.";
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
							resp = "Thank you!  I think you want " + game.getIssuePluralNames()[best] + ", is that right?";
						else
							resp = "Thank you!  What item would you most like in return?";
					}
					else
						resp = "I appreciate the effort, but I like the last offer better!";
					
				}
				break;
			case 4:
				resp = "I'm sorry, have I done something wrong?  I'm just trying to make sure we both get the things that make us the most happy.";

				if(!isFull)
					resp += "  Besides, what about the rest of the undecided items?";
				
				break;
			case 6:
				if (offerCount > 0)
				{
					int avgPlayerValue = (Math.abs(utils.opponentValueMax(lastOffer.getOffer()) - utils.opponentValueMin(lastOffer.getOffer())))/2;
					if (Math.abs(utils.myActualOfferValue(lastOffer.getOffer()) - avgPlayerValue) > game.getNumIssues() * 2)//fair is defined as within one of the most valuable items away from each other
					{
						resp = "Ok, I understand.  I do wish we could come up with something that is a more even split though.";
						if (best >= 0 && worst >= 0)
							resp += "  Isn't it true that you like " + game.getIssuePluralNames()[best] + " best and " + game.getIssuePluralNames()[worst] + " least?";
					}
					else
						resp = "Ok, I understand.  This seems like a fairly even split.";
					
				}
				else
					resp = "What offer?  I don't think I've gotten any offers yet...";
				
				if(!isFull)
					resp += "  Also, what about the rest of the undecided items?";
				break;
			case 7:
				resp = "Oh dear.  That certainly wasn't my intention!  Perhaps I misunderstood which items are most important to you.  Would you mind telling me again?";
				break;
			case 5:
			case 8:
				int suggest = best >= 0 ? best : (int)(Math.random() * game.getNumIssues());
				String myBest = findVHItem(1, game);
				String yourBest = game.getIssuePluralNames()[suggest];
				if (myBest.equals(yourBest))
					suggest = (suggest + 1) % game.getNumIssues();
				resp = "Ok, I understand you have your own requirements.  If I give you the " + game.getIssuePluralNames()[suggest] + ", can you give me the " + myBest + "?";
				if(!isFull)
					resp += "  Also, what about the rest of the undecided items?";
				break;
			case 10:
				int time = 0;
				int index = history.getHistory().size() - 1;
				index = index < 1 ? 1 : index;
				if (history.getHistory().size() > 1)
				{
					Event lastTime = history.getHistory().get(index);
					while (lastTime.getType() != Event.EventClass.TIME && index > 0)
					{
						index--;
						lastTime = history.getHistory().get(index);
					}
					if(lastTime == null || lastTime.getMessage() == null || lastTime.getMessage().equals(""))
						break;
					time = (int)Double.parseDouble(lastTime.getMessage());
					time = game.getTotalTime() - time;
					
					int min = time / 60;
					int sec = time % 60;
					
					resp = "There is currently " + min + " minute" + (min == 1 ? "" : "s") + " and " + sec + " seconds remaining.";
					
					if (min > 0)
						resp += "  Don't worry.  We've still got a bit more time to negotiate.";
					else
					{
						int secondBest = findSecondBest();
						int suggest2 = best >= 0 ? best : (int)(Math.random() * game.getNumIssues());
						int suggest3 = secondBest >= 0 ? secondBest : (int)(Math.random() * game.getNumIssues());
						if (suggest3 == suggest2)
							suggest3  = (suggest3 + 1) % game.getNumIssues();
						resp += "  AHH!  You're right!  Let's just split it like this: you get all the " + game.getIssuePluralNames()[suggest2] + " and the " + game.getIssuePluralNames()[suggest3] +
								" and I get the remainder.";
					}
				}
				break;
			case 11:
				resp = "Alright, what do you think of this?";
				break;
			case 100:
				resp = this.getRejectLang(history, game);
				break;
			case 101:
				resp = this.getAcceptLang(history, game);
				break;
			default:
				resp = "I'm sorry... I find myself at a curious loss for words!";
				break;
				
		}
		return resp;
		
	}
	
	private int findBest()
	{
		for (ArrayList<Integer> order: orderings)
		{
			for (int i = 0; i < order.size(); i++)
			{
				if(order.get(i) == 1)
					return i;
			}
		}
		return -1;
	}
	
	private int findSecondBest()
	{
		for (ArrayList<Integer> order: orderings)
		{
			for (int i = 0; i < order.size(); i++)
			{
				if(order.get(i) == 2)
					return i;
			}
		}
		return -1;
	}

	
	private int findWorst(GameSpec game)
	{
		for (ArrayList<Integer> order: orderings)
		{
			for (int i = 0; i < order.size(); i++)
			{
				if(order.get(i) == game.getNumIssues())
					return i;
			}
		}
		return -1;
	}
	
	private String findVHItem(int order, GameSpec game)
	{
		if(order <= 0 || order > game.getNumIssues())
			throw new IndexOutOfBoundsException("Index out bounds on VH Preference!");
		Map<String, Integer> pref =  MathUtils.sortByValue(game.getSimpleVHPoints());
		int count = 0;
		for (Map.Entry<String, Integer> s: pref.entrySet())
		{
			count++;
			if(count == order)
				return s.getKey();
		}
		return null;
	}
}