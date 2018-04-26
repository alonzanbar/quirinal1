package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;

public class IAGOBuildingBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
		
	private AgentUtilsExtension utils;
	private GameSpec game;	
	private Offer allocated;
		
	@Override
	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;
		
		this.game = this.utils.getSpec();
		allocated = new Offer(game.getNumIssues());
		for(int i = 0; i < game.getNumIssues(); i++)
		{
			int[] init = {0, game.getIssueQuants()[i], 0};
			allocated.setItem(i, init);
		}
	}
	
	@Override
	protected void updateAllocated (Offer update)
	{
		allocated = update;
	}
	
	@Override
	protected Offer getAllocated ()
	{
		return allocated;
	}
	
	@Override
	protected Offer getConceded ()
	{
		return allocated;
	}
	
	@Override
	protected Offer getFinalOffer(History history)
	{
		Offer propose = new Offer(game.getNumIssues());
		int totalFree = 0;
		do 
		{
			totalFree = 0;
			for(int issue = 0; issue < game.getNumIssues(); issue++)
			{
				totalFree += allocated.getItem(issue)[1];
			}
			propose = getNextOffer(history);
			updateAllocated(propose);
		} while(totalFree > 0);
		return propose;
	}

	@Override
	public Offer getNextOffer(History history) 
	{	
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumIssues());
		for(int issue = 0; issue < game.getNumIssues(); issue++)
			propose.setItem(issue,  allocated.getItem(issue));

		ArrayList<Integer> playerPref = utils.getMinimaxOrdering();
		ArrayList<Integer> vhPref = utils.getVHOrdering();
		int[] free = new int[game.getNumIssues()];
		
		for(int issue = 0; issue < game.getNumIssues(); issue++)
		{
			free[issue] = allocated.getItem(issue)[1];
		}
		
		//find top deals
		int topPlay = -1;
		int topVH = -1;
		int max = game.getNumIssues() + 1;
		for(int i  = 0; i < game.getNumIssues(); i++)
			if(free[i] > 0 && playerPref.get(i) < max)
			{
				topPlay = i;
				max = playerPref.get(i);
			}
		max = game.getNumIssues() + 1;
		for(int i  = 0; i < game.getNumIssues(); i++)
			if(free[i] > 0 && vhPref.get(i) < max)
			{
				topVH = i;
				max = vhPref.get(i);
			}
		

		if (topPlay == -1 && topVH == -1) //we're already at a full offer, but need to try something different
		{
			//just repeat and keep allocated
		}			
		else if(topPlay == topVH)//we're wanting the same thing			
		{
			if(free[topPlay] >= 2)
				propose.setItem(topPlay, new int[] {allocated.getItem(topPlay)[0] + 1, free[topPlay] - 2, allocated.getItem(topPlay)[2] + 1});//split evenly
			else
				propose.setItem(topPlay, new int[] {allocated.getItem(topPlay)[0], free[topPlay] - 1, allocated.getItem(topPlay)[2] + 1});//give give
		}
		else
		{
			propose.setItem(topPlay, new int[] {allocated.getItem(topPlay)[0], free[topPlay] - 1, allocated.getItem(topPlay)[2] + 1});
			propose.setItem(topVH, new int[] {allocated.getItem(topVH)[0] + 1, free[topVH] - 1, allocated.getItem(topVH)[2]});
		}
		
		
		return propose;
	}

	@Override
	protected Offer getTimingOffer(History history) {
		return null;
	}

	@Override
	protected Offer getAcceptOfferFollowup(History history) {
		return null;
	}
	
	@Override
	protected Offer getFirstOffer(History history) {
		return null;
	}

	@Override
	protected int getAcceptMargin() {
		return game.getNumIssues();
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
		return null;
	}
	

}
