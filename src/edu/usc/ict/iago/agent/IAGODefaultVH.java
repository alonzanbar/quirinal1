package edu.usc.ict.iago.agent;

import java.util.LinkedList;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.ExpressionPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Offer;

public class IAGODefaultVH extends GeneralVH {
	private BehaviorPolicy behavior;
	private ExpressionPolicy expression;
	private MessagePolicy messages;

	public IAGODefaultVH(String name, GameSpec game, Session session)
	{
		super("vh_default", game, session);
		expression = new IAGODefaultExpression();
		messages = new IAGODefaultMessage();
		behavior = new IAGODefaultBehavior();
	}
	
	@Override
	public LinkedList<Event> updateHistory(Event e)
	{
		
		LinkedList<Event> resp = new LinkedList<Event>();
		//this agent essentially mirrors you, it sends messages when you do, sends offers when you do, and sends expressions when you do
		//however, it cycles through all expressions in order, which makes it nice for testing purposes
		if(e.getType().equals(Event.EventClass.SEND_EXPRESSION))
		{
			String expr = getExpression();
			Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_EXPRESSION, expr, 1000, 0);
			//history.updateHistory(e1);
			resp.add(e1);
			return resp;
		}
		
		//slightly more complex; also sends a message with its offer, and rejects your previous
		if(e.getType().equals(Event.EventClass.SEND_OFFER))
		{
			Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, getVHRejectResponse(), 0);
			//history.updateHistory(e0);
			resp.add(e0);
			Event e1 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
			//history.updateHistory(e1);
			resp.add(e1);
			Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, getProposal(), 3000);
			//history.updateHistory(e2);
			resp.add(e2);
			Event e3 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, getNextChoice(), 700);
			//history.updateHistory(e3);
			resp.add(e3);
			return resp;
		}
		
		if(e.getType().equals(Event.EventClass.SEND_MESSAGE))
		{
			Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, getMessageResponse(), 2000);
			//history.updateHistory(e1);
			resp.add(e1);
			return resp;
		}
		
		return null;
	}
	
	
	private Offer getNextChoice(){
		return behavior.getNextOffer(getHistory());
	}
	
	
	private String getExpression() {
		return expression.getExpression(getHistory());
	}
	
	
	private String getProposal(){
		return messages.getProposalLang(getHistory(), game);
	}
	
	private String getMessageResponse(){
		return messages.getMessageResponse(getHistory(), game);
	}
	
	@SuppressWarnings("unused")
	private String getAcceptResponse(){
		return messages.getAcceptLang(getHistory(), game);
	}
	
	@SuppressWarnings("unused")
	private String getRejectResponse(){
		return messages.getRejectLang(getHistory(), game);
	}
	
	@SuppressWarnings("unused")
	private String getVHAcceptResponse(){
		return messages.getVHAcceptLang(getHistory(), game);
	}
	
	private String getVHRejectResponse(){
		return messages.getVHRejectLang(getHistory(), game);
	}

	@Override
	public String getArtName() {
		return "Brad";
	}
	
	@Override
	public String agentDescription() {
		return "<h1>Brad</h1><p>He is excited to begin negotiating!</p>";
	}
	
}