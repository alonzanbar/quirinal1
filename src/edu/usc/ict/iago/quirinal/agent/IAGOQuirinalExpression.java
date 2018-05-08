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
		//our basic version is ignorant of history
		//it simply cycles through all expressions
		count++;
		if (count >= expressions.length)
			count = 0;
		return expressions[count];
		
		//this version mirrors the player instead
//		if(history.getPlayerHistory().getLast().getType().equals(Event.EventClass.SEND_EXPRESSION)){
//			if(history.getPlayerHistory().getLast().getMessage().equals("sad"))
//				return "sad";
//			if(history.getPlayerHistory().getLast().getMessage().equals("happy"))
//				return "happy";
//			if(history.getPlayerHistory().getLast().getMessage().equals("surprised"))
//				return "surprised";
//			if(history.getPlayerHistory().getLast().getMessage().equals("angry"))
//				return "angry";
//		} 
//		return "";
		
	}
	protected String getSemiFairEmotion()
	{
		return "neutral";
	}
	
	protected String getFairEmotion()
	{
		return "happy";
	}
	
	protected String getUnfairEmotion()
	{
		return "sad";
	}

}
