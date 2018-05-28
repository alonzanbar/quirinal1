package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.usc.ict.iago.agent.AgentUtilsExtension;
import edu.usc.ict.iago.agent.IAGOCoreBehavior;
import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;

public class IAGOQuirinalBehavior extends IAGOCoreBehavior implements BehaviorPolicy {
	
	private AgentUtilsExtension utils;
	private OpponentModel opponentModel;
	private AcceptCounter acceptCounter;
	private GameSpec game;	
	private Offer allocated;
	private long time;
	
	private static final int MINE = 0;
	
	private static final int OPPONENT = 2;
	
	private static final int UNALLOCATED = 1;
	
	private double PROPABILITY_RANDOM_OFFER = 0.1;
		
	@Override
	protected void setUtils(AgentUtilsExtension utils)
	{
		this.utils = utils;		
		this.game = this.utils.getSpec();
		time = System.currentTimeMillis();
		allocated = new Offer(game.getNumIssues());
		for(int i = 0; i < game.getNumIssues(); i++)
		{
			int[] init = {0, game.getIssueQuants()[i], 0};
			allocated.setItem(i, init);
		}
		opponentModel = new HeuristicOpponentModel(game);
		acceptCounter = new AcceptCounter();
		((AgentQuirinaUtilsExtension) utils).setOpponentModel(opponentModel);
	}

	public OpponentModel getOpponentModel() {
		return opponentModel;
	}
	
	private boolean shouldRandomizeOffer(History history) {
		Random r = new Random();		
		boolean shouldRandomizeOffer = r.nextDouble() < PROPABILITY_RANDOM_OFFER;
		return shouldRandomizeOffer;
	}
	
	@Override
	public Offer getNextOffer(History history) 
	{	
		if (shouldRandomizeOffer(history)) {
			return GetRandomOffer();
		}
		
		List<Integer> playerPref = getOpponentOrder();
		List<Integer> vhPref = utils.getVHOrdering();
		// one for me, one for you..
		int nextQty = acceptCounter.getNextQuantity();
		return GetOfferFromCompareOrders(vhPref, playerPref, nextQty);
	}
	
	
	@Override
	public List<Integer> getOpponentOrder() {
		return opponentModel.getTopPreferences();
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
	protected Offer getAcceptOfferFollowup(History history) {
		return getNextOffer(history);
	}
	
	private static <T extends Comparable<T>> int argmin(List<T> items) {
		int index = 0;
		T bestItem = null;
		int bestIndex = index;
		if (items.isEmpty()) {
			return -1;
		}
		for (T item : items) {
			if (bestItem == null || item.compareTo(bestItem) == -1) {
				bestItem = item;
				bestIndex = index;
			}
			index++;
		}
		return bestIndex;
	}

	/**
	 * https://github.com/deeplearning4j/nd4j
	 * @param sortable
	 * @param ascending
	 * @return
	 */
	public static <T extends Comparable<T>> int[] argsort(
			final List<T> sortable, 
			final boolean ascending) {
		
	    Integer[] indexes = new Integer[sortable.size()];
	    for (int i = 0; i < indexes.length; i++) {
	        indexes[i] = i;
	    }
	    Arrays.sort(indexes, new Comparator<Integer>() {
	        @Override
	        public int compare(final Integer i1, final Integer i2) {
	            return (ascending ? 1 : -1) * sortable.get(i1).compareTo(sortable.get(i2));
	        }
	    });
	
	    int[] ret = new int[indexes.length];
	    for(int i = 0; i  < ret.length; i++)
	        ret[i] = indexes[i];
	
	    return ret;
	}


	@Override
	protected Offer getFirstOffer(History history) {
		int qty = acceptCounter.getNextQuantity();
		Offer propose = GetOfferFromCompareOrders(
				utils.getVHOrdering(), 
				opponentModel.getTopPreferences(), qty);
		return propose;
	}

	@Override
	protected int getAcceptMargin() {
		int totalSeconds = this.game.getTotalTime();
		long currentTime = System.currentTimeMillis();
		int elapsedSeconds = (int)Math.ceil((currentTime - time) / 1000.0);
		
		if (elapsedSeconds < 0 || elapsedSeconds > totalSeconds) {
			// sanity..
			return 0;
		}
		int remaining = totalSeconds - elapsedSeconds;
		if (remaining > 180) {
			return 0; // no compromise yet
		} else if (remaining > 90) {
			return 2;
		} else if (remaining > 60) {
			return 5;
		} else if (remaining > 30) {
			return 7;
		} else {
			return 10;
		}
	}

	@Override
	protected Offer getRejectOfferFollowup(History history) {
		
		Random r = new Random();
		int topK = 3;
		int random = r.nextInt(topK);
		// we have been rejected :-(
		// the next proposal should come from a different 
		// opponent model. We associate a score with each 
		// opponent model, choose uniformly from the top K 
		// opponent models and propose according to the sampled one.		
		List<Integer> playerPref = opponentModel.getTopOrderings(random).toPrefs();
		ArrayList<Integer> vhPref = utils.getVHOrdering();	
		int items = acceptCounter.getNextQuantity();
		return GetOfferFromCompareOrders(vhPref, playerPref, items);
	}


	@Override
	protected Offer getFinalOffer(History history) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Offer getTimingOffer(History history) {
		// when opponent is stuck, randomize offers
		return GetRandomOffer();
	}


	@Override
	public void update(Event event) {
		acceptCounter.update(event);
		opponentModel.update(event);
		
	}	
	
	private static List<Integer> toList(int[] ints) {
		ArrayList<Integer> ls = new ArrayList<>(ints.length);		
		for(int i: ints) {
			ls.add(i);
		}
		return ls;
	}
	
	private Offer GetRandomOffer() {	
		List<Integer> perm1 = toList(MathUtils.getRandomPermutation(game.getNumIssues()));
		List<Integer> perm2 = toList(MathUtils.getRandomPermutation(game.getNumIssues()));
		// will try to reassign 4 items, 2 to each
		// first from unallocated pile
		// if all items are allocated, will toggle items between players 
		int switchItems = 4; 
		Offer propose  = GetOfferFromCompareOrders(perm1, perm2,  switchItems);		
		return propose;
	}
	
	private Offer increment(Offer base, int player, List<Integer> prefs) {
		Offer propose = copyOf(base);
		int otherPlayer = player == MINE ? OPPONENT : MINE;
		int[] newItem = new int[3];
		int issue = findUnallocatedIssue(propose, prefs, UNALLOCATED);
		if (issue == -1) {
			return propose;
		}
		
		int[] item = propose.getItem(issue);
		newItem[player] = item[player] + 1;
		newItem[UNALLOCATED] = item[UNALLOCATED] - 1;
		newItem[otherPlayer] = item[otherPlayer];
		propose.setItem(issue, newItem);
		
		return propose;
	}

	private int findUnallocatedIssue(Offer offer, List<Integer> prefs, int playerWithAlloc) {
		// issue with preference 1 is the most wanted		
		// issue with preference 4 is the least wanted
		// return a list where first item is the most wanted issue
		// by argument sorting the preference array.
		int[] sortedIssues = argsort(prefs, true);
		int unallocIssue = -1;
		for (int issue : sortedIssues ) {
			int[] item = offer.getItem(issue);
			if (item[playerWithAlloc] > 0) {
				unallocIssue = issue;
				break;
			}
		}
		return unallocIssue;
	}
	
	private Offer toggle(Offer base, int player, List<Integer> prefs) {
		Offer propose = copyOf(base);
		int otherPlayer = player == MINE ? OPPONENT : MINE;
		// find with my preferences, an issue that is allocated to the
		// other player, and switch between us.
		int issue = findUnallocatedIssue(propose, prefs, otherPlayer);
		if (issue == -1) {
			return propose;
		}
		int[] item = propose.getItem(issue);
		int[] newItem = new int[3];
		newItem[player] = item[player] + 1;
		newItem[UNALLOCATED] = item[UNALLOCATED];
		newItem[otherPlayer] = item[otherPlayer] - 1;
		propose.setItem(issue, newItem);
		return propose;
	}
	
	private static Offer copyOf(Offer source) {
		Offer copy = new Offer(source.getIssueCount());
		for(int issue = 0; issue < source.getIssueCount(); issue++) {
			int[] srcItem = source.getItem(issue);
			int[] cpyItem = Arrays.copyOf(srcItem , srcItem.length);
			copy.setItem(issue,  cpyItem);
		}
		return copy;
	}
	
	private static boolean equals(Offer o1, Offer o2) {
		if (o1.getIssueCount() != o2.getIssueCount()) {
			return false;
		}
		for (int i = 0; i < o1.getIssueCount(); i++) {
			int[] alloc1 = o1.getItem(i);
			int[] alloc2 = o2.getItem(i);
			// ALWAYS 3 way split: mine, not allocated, opponent's
			for (int j = 0; j < 3; j++) {
				if(alloc1[j] != alloc2[j]) {
					return false;
				}
			}				
		}
		return true;
	}

	
	private Offer GetOfferFromCompareOrders(List<Integer> vHpref, List<Integer> playerPref, int items) {
		// start with last known baseline, and try to allocate
		// all unallocated items according to the preferences.
		// If all items are allocated,  toggle items between players.
		// Every iteration handle one item in an interleaving fashion, 
		// one for me, one for you.
		Offer propose = copyOf(allocated);
		for(int i = 0; i < items; i++) {
			int player = ((i % 2) == 0)? MINE : OPPONENT;
			List<Integer> prefs = ((i % 2) == 0)? vHpref: playerPref;
			Offer next = increment(propose, player, prefs);
			if (equals(propose, next)) {
				// no more resources left unallocated, so toggle between resources
				next = toggle(propose, player, prefs);
			}
			propose = next;			
		}
		return propose;
	}



}
