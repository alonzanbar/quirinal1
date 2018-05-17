package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.usc.ict.iago.agent.AgentUtilsExtension;
import edu.usc.ict.iago.agent.IAGOCoreBehavior;
import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;

public class IAGOQuirinalBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
	
	private AgentUtilsExtension utils;
	private OpponentModel opponentModel;
	private GameSpec game;	
	private Offer allocated;
	private long time = System.currentTimeMillis();
		
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
		opponentModel = new HeuristicOpponentModel(game);
	}

	

	@Override
	public Offer getNextOffer(History history) 
	{	
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumIssues());
		for(int issue = 0; issue < game.getNumIssues(); issue++)
			propose.setItem(issue,  allocated.getItem(issue));
		Ordering playerPref = opponentModel.getTopOrderings(1).get(0);
		
		ArrayList<Integer> vhPref = utils.getVHOrdering();
		int[] free = GetFree();
		
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
				//for even free[topPlay]
				if (((free[topPlay]/2)*2) == free[topPlay])
					propose.setItem(topPlay, new int[] {allocated.getItem(topPlay)[0] + free[topPlay]/2, 0, allocated.getItem(topPlay)[2] + free[topPlay]/2});//split evenly
				//for odd free[topPlay]
				else
					propose.setItem(topPlay, new int[] {allocated.getItem(topPlay)[0] + free[topPlay]/2, 1, allocated.getItem(topPlay)[2] + free[topPlay]/2});//split evenly
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
	public ArrayList<Integer> getOpponentOrder() {
		return opponentModel.getTopOrderings(1).get(0).issues;
	}	
	
//	/**
//	 * https://github.com/deeplearning4j/nd4j
//	 * @param a
//	 * @param ascending
//	 * @return
//	 */
//    public static int[] argsort(final double[] doubles, final boolean ascending) {
//        Integer[] indexes = new Integer[doubles.length];
//        for (int i = 0; i < indexes.length; i++) {
//            indexes[i] = i;
//        }
//        Arrays.sort(indexes, new Comparator<Integer>() {
//            @Override
//            public int compare(final Integer i1, final Integer i2) {
//                return (ascending ? 1 : -1) * Double.compare(doubles[i1], doubles[i2]);
//            }
//        });
//
//        int[] ret = new int[indexes.length];
//        for(int i = 0; i  < ret.length; i++)
//            ret[i] = indexes[i];
//
//        return ret;
//    }


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
	protected Offer getAcceptOfferFollowup(History history) {
		
		return null;
	}

	@Override
	protected Offer getFirstOffer(History history) {
		 
		//start from where we currently have accepted
		Offer propose = new Offer(game.getNumIssues());
		
		ArrayList<Integer> vhPref = utils.getVHOrdering();
		
		int indexWithHighVHPreference = 0;
		int highVHPreference = 0;
		for (int i = 0; i < vhPref.size(); i++) {
			if (vhPref.get(i) > highVHPreference) {
				highVHPreference = vhPref.get(i);
				indexWithHighVHPreference = i;
			}
		}
		
		int[] free = GetFree();
		
		for(int issue = 0; issue < game.getNumIssues(); issue++)
			propose.setItem(issue,  allocated.getItem(issue));
		propose.setItem(indexWithHighVHPreference, new int[] {allocated.getItem(indexWithHighVHPreference)[0] + 1, free[indexWithHighVHPreference] - 2, allocated.getItem(indexWithHighVHPreference)[2] + 1});
		return propose;
	}

	@Override
	protected int getAcceptMargin() {
		long currentTime = System.currentTimeMillis();
		double returnValue = (currentTime - time)/0.000033; 
		return (int)returnValue;
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Offer getFinalOffer(History history) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Offer getTimingOffer(History history) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void update(Event event) {
		opponentModel.update(event);		
	}

	private int[] GetFree() {
		int[] free = new int[game.getNumIssues()];
		
		for(int issue = 0; issue < game.getNumIssues(); issue++)
		{
			free[issue] = allocated.getItem(issue)[1];
		}
		return free;
	}

}
