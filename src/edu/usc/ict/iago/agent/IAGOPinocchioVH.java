package edu.usc.ict.iago.agent;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.GameSpec;

public class IAGOPinocchioVH extends IAGOCoreVH {
		
	public IAGOPinocchioVH(String name, GameSpec game, Session session)
	{
		super("Pinocchio", game, session, new IAGOBuildingBehavior(), new IAGONiceExpression(), 
				new IAGONiceFreeMessage(),new AgentUtilsExtension());	
		
	}

	@Override
	public String getArtName() {
		return "Laura";
	}
	
	@Override
	public String agentDescription() {
		return "<h1>Laura</h1><p>She is excited to begin negotiating!</p>";
	}
	

}