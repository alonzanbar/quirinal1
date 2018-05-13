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
				return "surprised";
			if(history.getPlayerHistory().getLast().getMessage().equals("angry"))
				return "sad";
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
						return "happy";
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
		return "netural";
	}
	@Override
	protected String getUnfairEmotion() {
		// TODO Auto-generated method stub
		return "netural";

	}

}
