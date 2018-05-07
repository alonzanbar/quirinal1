package edu.usc.ict.iago.quirinal.agent;

import edu.usc.ict.iago.agent.IAGOCoreExpression;
import edu.usc.ict.iago.agent.IAGOCoreVH;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.ExpressionPolicy;
import edu.usc.ict.iago.utils.History;

@SuppressWarnings("unused")
public class IAGOQuirinalExpression extends IAGOCoreExpression implements ExpressionPolicy {

	private String [] expressions = {"angry", "disgusted", "afraid", "neutral", "sad", "insincereSmile", "happy", "surprised"};
	private int count = 0;
	@Override
	public String getExpression(History history) 
	{	
		Event lastEvent = history.getPlayerHistory().getLast();
		//Well behaving agent
		if(lastEvent.getType().equals(Event.EventClass.SEND_EXPRESSION)){
			if(history.getPlayerHistory().getLast().getMessage().equals("sad"))
				return "sad";
			if(history.getPlayerHistory().getLast().getMessage().equals("happy"))
				return "happy";
			if(history.getPlayerHistory().getLast().getMessage().equals("surprised"))
				return "insincereSmile";
			if(history.getPlayerHistory().getLast().getMessage().equals("angry"))
				return "afraid";
		} 
		
		/*
		// Nasty behavior
		if(history.getPlayerHistory().getLast().getType().equals(Event.EventClass.SEND_EXPRESSION)){
			if(history.getPlayerHistory().getLast().getMessage().equals("sad"))
				return "angry";
			if(history.getPlayerHistory().getLast().getMessage().equals("happy"))
				return "happy";
			if(history.getPlayerHistory().getLast().getMessage().equals("surprised"))
				return "happy";
			if(history.getPlayerHistory().getLast().getMessage().equals("angry"))
				return "netural";
		} 
		*/
		else if (lastEvent.getType().equals(Event.EventClass.SEND_MESSAGE)){
			if(last.getMessageCode() > -1) {
				switch(lastEvent.getMessageCode()) {
					case 0://important both happy
					case 3://get most valuable item
					case 9://benefits both
						return "happy";
					case 1://I gave, you give
					case 11://make an offer
					case 2://split evenly
					case 10: //no time!
					case 6://best offer possible
					case 5: //last offer
						return "netural"; // section is redundant (can be using default),leaving for further changes.
					case 8://can't go lower
						return "surprised"
					case 4: //accept or consequences
					case 7: //offer sucks
						return "disgusted";
					default:
						return "neutral";
				}
			}
		}
		return "netural";
		
	}
	@Override
	protected String getSemiFairEmotion() {
		// TODO Auto-generated method stub
		return "netural";
	}
	@Override
	protected String getFairEmotion() {
		// TODO Auto-generated method stub
		return "happy";
	}
	@Override
	protected String getUnfairEmotion() {
		// TODO Auto-generated method stub
		return "netural";
	}

}
