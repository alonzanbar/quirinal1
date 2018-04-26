package edu.usc.ict.iago.agent;

import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Preference;



public class IAGODefaultMessage implements MessagePolicy {
	protected final String[] proposal = {"I think this deal is acceptable.", 
						 			   "I think you'll find this offer to be satisfactory.", 
						 			   "I think this arrangement is fair.", 
						 			   "I think this deal will interest you.",
									   "I think this deal is acceptable."};
	protected final String[] acceptResponse = {
											 "Great!",
											 "Wonderful!",
											 "I'm glad we could come to an agreement!",
											 "Sounds good!"};
	protected final String[] rejectResponse = {
											 "Oh that's too bad.",
											 "Ah well, perhaps another time.",
											 "Ok, maybe something different next time.",
											 "Alright.",
											 ""};
	
	protected final String[] vhReject = {
			 "Are you insane?",
			 "That won't work for me.",
			 "You must be joking",
			 "I find your lack of negotiating skills... disturbing."
			 };
	
	protected final String[] vhAccept = {
			 "Your offer is acceptable.",
			 "Awesome sauce.",
			 "You betcha, cowboy.",
			 "Yes.  This deal will work."
			 };
	
	protected final String[] messageResponse = {"Hmmm", "Well...", "I'm not sure what to do with this information", "My responses are limited; you must ask the right questions."};
	
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

	public String getMessageResponse(History history, GameSpec game) 
	{
		return messageResponse[(int)(Math.random()*messageResponse.length)];
	}

	@SuppressWarnings("unused")
	private String prefToEnglish(Preference preference, GameSpec game)
	{
		String ans = "";
		if (preference.isQuery())
			ans += "Do you like ";
		else
			ans += "I like ";
		ans += game.getIssuePluralNames()[preference.getIssue1()] + " ";
		switch (preference.getRelation())
		{
			case GREATER_THAN:
				ans += "more than ";
				ans += game.getIssuePluralNames()[preference.getIssue2()];
				break;
			case LESS_THAN:
				ans += "less than ";
				ans += game.getIssuePluralNames()[preference.getIssue2()];
				break;
			case BEST:
				ans += "the best";
				break;
			case WORST:
				ans += "the least";
				break;
			case EQUAL:
				ans += "the same as ";
				ans += game.getIssuePluralNames()[preference.getIssue2()];
				break;
		}
		ans += preference.isQuery() ? "?" : ".";
		
		return ans;
	}
}