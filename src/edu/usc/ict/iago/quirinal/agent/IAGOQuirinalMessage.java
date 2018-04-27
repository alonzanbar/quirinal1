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



	@Override
	public void updateOrderings(ArrayList<ArrayList<Integer>> orderings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getEndOfTimeResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSemiFairResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getContradictionResponse(String drop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setUtils(AgentUtilsExtension utils) {
		// TODO Auto-generated method stub
		
	}
}