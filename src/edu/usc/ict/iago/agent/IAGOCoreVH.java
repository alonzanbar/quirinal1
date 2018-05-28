package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.websocket.Session;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;

public abstract class IAGOCoreVH extends GeneralVH
{
	
	private Offer lastOfferReceived;
	private Offer lastOfferSent;
	protected IAGOCoreBehavior behavior;
	protected IAGOCoreExpression expression;
	protected IAGOCoreMessage messages;
	protected AgentUtilsExtension utils;
	private boolean timeFlag = false;
	private boolean firstFlag = false;
	private int noResponse = 0;
	private int noResponseLimit = 5;
	private boolean noResponseFlag = false;
	private boolean firstGame = true;
	
	public IAGOCoreVH(String name, GameSpec game, Session session, IAGOCoreBehavior behavior,
			IAGOCoreExpression expression, IAGOCoreMessage messages,AgentUtilsExtension aue )
	{
		super(name, game, session);
		
		aue.configureGame(game);
		
		this.utils = aue;
		this.expression = expression;
		this.messages = messages;
		this.behavior = behavior;
		
		this.behavior.setUtils(utils);
		this.messages.setUtils(utils);
	}

	@Override
	public LinkedList<Event> updateHistory(Event e)
	{
		LinkedList<Event> resp = new LinkedList<Event>();
		behavior.update(e);
		/**what to do when the game has changed -- this is only necessary because our AUE needs to be updated.
			Game, the current GameSpec from our superclass has been automagically changed!
			IMPORTANT: between GAME_END and GAME_START, the gameSpec stored in the superclass is undefined.
			Furthermore, attempting to access data that is decipherable with a previous gameSpec could lead to exceptions!
			For example, attempting to decipher an offer from Game 1 while in Game 2 could be a problem (what if Game 1 had 4 issues, but Game 2 only has 3?)
			You should always treat the current GameSpec as true (following a GAME_START) and store any useful metadata about past games yourself.
		**/
		if(e.getType().equals(Event.EventClass.GAME_START)) 
		{		
			ServletUtils.log("Game has changed... reconfiguring!", ServletUtils.DebugLevels.DEBUG);
			//AgentUtilsExtension aue = new AgentUtilsExtension();
			utils.configureGame(game);
			//this.utils = aue;
			
			// this has a side effect - utils.opponentModel 
			// is initialized after the behaviour setUtils
			this.behavior.setUtils(utils);	
			this.messages.setUtils(utils);
			
			//if we wanted, we could change our Policies between games
			
			//we should also reset some things
			timeFlag = false;
			firstFlag = false;
			noResponse = 0;
			noResponseLimit = 5;
			noResponseFlag = false;
			
			if(!firstGame)
			{
				//just for good measure, as a demo
				Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, "It's good to see you again!  Let's get ready to negotiate again.", 0);
				resp.add(e0);
				return resp;
			}
			firstGame = false;
		}
		
		
				
		//should we lead with an offer?
		if(!firstFlag)
		{
			firstFlag = true;
			Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getFirstOffer(getHistory()));
			if(e2.getOffer() != null)
			{
				Event e3 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
				resp.add(e3);
				Event e4 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 1000);
				resp.add(e4);
				resp.add(e2);
			}
		}
		
		//what to do when player sends an expression -- react to it with text and our own expression
		if(e.getType().equals(Event.EventClass.SEND_EXPRESSION))
		{
			String expr = expression.getExpression(getHistory());
			Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_EXPRESSION, expr, 2000, 0);
			resp.add(e1);
			
			Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getMessageResponse(getHistory(), game), 0);
			resp.add(e0);
			return resp;
		}
		
		//when to formally accept when player send an incoming formal acceptance
		if(e.getType().equals(Event.EventClass.FORMAL_ACCEPT))
		{
			Event lastOffer = utils.lastEvent(getHistory().getHistory(), Event.EventClass.SEND_OFFER);
			Event lastTime = utils.lastEvent(getHistory().getHistory(), Event.EventClass.TIME);
			
			int totalIssues = 0;
			for (int i = 0; i < game.getNumIssues(); i++)
				totalIssues += game.getIssueQuants()[i];
			if(lastOffer != null && lastTime != null)
			{
				//approximation based on distributive case
				int fairSplit = ((game.getNumIssues() + 1) * totalIssues / 4);
				//down to the wire, accept anything better than batna
				if(utils.myActualOfferValue(lastOffer.getOffer()) > game.getVHBATNA() && Integer.parseInt(lastTime.getMessage()) + 30 > game.getTotalTime()) 
				{
					Event e0 = new Event(History.VH_ID, Event.EventClass.FORMAL_ACCEPT, 0);
					resp.add(e0);
					return resp;
				}
				//accept anything better than fair minus margin
				if(utils.myActualOfferValue(lastOffer.getOffer()) > fairSplit - behavior.getAcceptMargin())
				{
					Event e0 = new Event(History.VH_ID, Event.EventClass.FORMAL_ACCEPT, 0);
					resp.add(e0);
					return resp;
				}
				else
				{
					Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getRejectLang(getHistory(), game), 0);
					resp.add(e1);
					return resp;					
				}
			}
		}
		
		//what to do with delays on the part of the other player
		if(e.getType().equals(Event.EventClass.TIME))
		{
			noResponse += 1;
			for(int i = getHistory().getHistory().size() - 1 ; i > 0 && i > getHistory().getHistory().size() - 4; i--)//if something from anyone for two time intervals
			{
				if(getHistory().getHistory().get(i).getType() != Event.EventClass.TIME) {
					noResponse = 0;
				}
				
			}
			
			// every time we reach a limit
			if(noResponse == noResponseLimit)
			{
				// next time we will back off a little and let the player more time to respond.
				noResponseLimit = (int)Math.ceil(noResponseLimit*1.5);
				Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getTimingOffer(getHistory()));
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 1000);
					resp.add(e4);
					resp.add(e2);
				}				
			}
			
			
			//times up
			if(!timeFlag && game.getTotalTime() - Integer.parseInt(e.getMessage()) < 30)
			{
				timeFlag = true;
				Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getEndOfTimeResponse(), 0);
				resp.add(e1);
				Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getFinalOffer(getHistory()));
				if(e2.getOffer() != null)
					resp.add(e2);
				
			}
			return resp;
		}
		
		//what to do when the player sends an offer
		if(e.getType().equals(Event.EventClass.SEND_OFFER))
		{
			ArrayList<Integer> playerOrder = new ArrayList<>(behavior.getOpponentOrder());	
			ServletUtils.log("Agent Normalized ordering: " + utils.getVHOrdering(), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Optimal ordering: " + playerOrder, ServletUtils.DebugLevels.DEBUG);
	
			Offer o = e.getOffer();//incoming offer
			this.lastOfferReceived = o;
			
			boolean localFair = false;
			boolean totalFair = false;
			
			Offer allocated = behavior.getAllocated();//what we've already agreed on
			Offer conceded = behavior.getConceded();//what the agent has agreed on internally
			int myOfferValue = utils.myActualOfferValue(o);
			int myAllocatedValue = utils.myActualOfferValue(allocated);			
			int opponentOfferValue = utils.opponentValue(o, playerOrder);
			int opponentAllocatedValue = utils.opponentValue(allocated, playerOrder);
			
			ServletUtils.log("Allocated Agent Value: " + myAllocatedValue, ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Conceded Agent Value: " + utils.myActualOfferValue(conceded), ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Offered Agent Value: " + myOfferValue, ServletUtils.DebugLevels.DEBUG);
			ServletUtils.log("Player Difference: " + (opponentOfferValue - opponentAllocatedValue), ServletUtils.DebugLevels.DEBUG);
			
			if(myOfferValue > myAllocatedValue)//net positive
				if(myOfferValue - myAllocatedValue + behavior.getAcceptMargin() > opponentOfferValue - opponentAllocatedValue)
					localFair = true;//offer improvement is within one max value item of the same for me and my opponent
			
				totalFair = true;//total offer still fair
			
			if (localFair && !totalFair)
			{
				Event eExpr = new Event(History.VH_ID, Event.EventClass.SEND_EXPRESSION, expression.getSemiFairEmotion(), 2000, 0);
				resp.add(eExpr);
				Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getSemiFairResponse(), 0);
				resp.add(e0);
				Event e3 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), 700);
				if(e3.getOffer() != null)
				{
					Event e1 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e1);
					Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 3000);
					resp.add(e2);
					this.lastOfferSent = e3.getOffer();
					resp.add(e3);
				}
			}
			else if(localFair && totalFair)
			{
				Event eExpr = new Event(History.VH_ID, Event.EventClass.SEND_EXPRESSION, expression.getFairEmotion(), 2000, 0);
				resp.add(eExpr);
				Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getVHAcceptLang(getHistory(), game), 0);
				resp.add(e0);
				behavior.updateAllocated(this.lastOfferReceived);
				
				Event eFinalize = new Event(History.VH_ID, Event.EventClass.FORMAL_ACCEPT, 0);
				if(utils.isFullOffer(o))
					resp.add(eFinalize);
			}
			else
			{
				Event eExpr = new Event(History.VH_ID, Event.EventClass.SEND_EXPRESSION, expression.getUnfairEmotion(), 2000, 0);
				resp.add(eExpr);
				Event e0 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getVHRejectLang(getHistory(), game), 0);
				resp.add(e0);	
				Event e3 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), 700);
				if(e3.getOffer() != null)
				{
					Event e1 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e1);
					Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 3000);
					resp.add(e2);
					this.lastOfferSent = e3.getOffer();
					resp.add(e3);
				}
			}
			
			return resp;
		}
		
		//what to do when the player sends a message (including offer acceptances and rejections)
		if(e.getType().equals(Event.EventClass.SEND_MESSAGE))
		{
			Preference p = e.getPreference() == null ? null : new Preference(e.getPreference().getIssue1(), e.getPreference().getIssue2(), e.getPreference().getRelation(), e.getPreference().isQuery());
			if (p != null && !p.isQuery()) //a preference was expressed
			{
				utils.addPref(p);
				if(utils.reconcileContradictions())
				{
					//we simply drop the oldest expressed preference until we are reconciled.  This is not the best method, as it may not be the the most efficient route.
					LinkedList<String> dropped = new LinkedList<String>();
					dropped.add(IAGOCoreMessage.prefToEnglish(utils.dequeuePref(), game));
					while(utils.reconcileContradictions())
						dropped.add(IAGOCoreMessage.prefToEnglish(utils.dequeuePref(), game));
					
					String drop = "";
					for (String s: dropped)
						drop += "\"" + s + "\", and ";
					
					drop = drop.substring(0, drop.length() - 6);//remove last 'and'
					
					Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, 
							messages.getContradictionResponse(drop), 2000);
					//history.updateHistory(e1);
					resp.add(e1);
				}
			}
			
			
			Event e1 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getMessageResponse(getHistory(), game), 3000);
			resp.add(e1);
			
			
			if(e.getMessageCode() == 11)//offer requested
			{
				Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getNextOffer(getHistory()), 3000);
				if(e2 != null)
				{
					Event e3 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 1000);
					resp.add(e4);
					this.lastOfferSent = e2.getOffer();
					resp.add(e2);		
				}
			}
			if(e.getMessageCode() == 101)//offer accepted
			{
				if(this.lastOfferSent != null)
					behavior.updateAllocated(this.lastOfferSent);
				
				Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getAcceptOfferFollowup(getHistory()), 3000);
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 1000);
					resp.add(e4);
					this.lastOfferSent = e2.getOffer();
					resp.add(e2);		
				}
			}
			
			if(e.getMessageCode() == 100)//offer rejected
			{			
				Event e2 = new Event(History.VH_ID, Event.EventClass.SEND_OFFER, behavior.getRejectOfferFollowup(getHistory()), 3000);
				if(e2.getOffer() != null)
				{
					Event e3 = new Event(History.VH_ID, Event.EventClass.OFFER_IN_PROGRESS, 0);
					resp.add(e3);
					Event e4 = new Event(History.VH_ID, Event.EventClass.SEND_MESSAGE, messages.getProposalLang(getHistory(), game), 1000);
					resp.add(e4);
					this.lastOfferSent = e2.getOffer();
					resp.add(e2);		
				}
			}
			return resp;
		}
		
		return null;
	}

	@Override
	public abstract String getArtName();
	
	@Override
	public abstract String agentDescription();
}
