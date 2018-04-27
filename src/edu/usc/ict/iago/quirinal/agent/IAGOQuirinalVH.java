package edu.usc.ict.iago.quirinal.agent;

import javax.websocket.Session;

import edu.usc.ict.iago.agent.IAGOCoreVH;
import edu.usc.ict.iago.utils.GameSpec;

public class IAGOQuirinalVH extends IAGOCoreVH {
		
	public IAGOQuirinalVH(String name, GameSpec game, Session session)
	{
		super("Pinocchio", game, session, new IAGOQuirinalBehavior(), new IAGOQuirinalExpression(), 
				new IAGOQuirinalMessage());
		
	}

	@Override
	public String getArtName() {
		return "Quirinal";
	}
	
	@Override
	public String agentDescription() {
		return "<h1>Laura</h1><p>She is excited to begin negotiating!</p>";
	}
	

}