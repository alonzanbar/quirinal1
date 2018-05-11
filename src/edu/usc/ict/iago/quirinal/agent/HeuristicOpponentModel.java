package edu.usc.ict.iago.quirinal.agent;

import static edu.usc.ict.iago.utils.MathUtils.sortByValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;

public class HeuristicOpponentModel implements OpponentModel{

	private final Map<Ordering, Double> orderings;
	
	private final RelationDistribution distribution;
	
	public HeuristicOpponentModel(GameSpec game) {
		orderings = initializeOrderings(game.getNumIssues());
		distribution = new RelationDistribution(game.getNumIssues());
	}

	private static Map<Ordering, Double> initializeOrderings(int numIssues) {
		Map<Ordering, Double> orderings = new HashMap<>();
		for (int[] permutation : MathUtils.getPermutations(numIssues, 1)) {
			ArrayList<Integer> issues = new ArrayList<>();
			for (int issue : permutation) {
				issues.add(issue);
			}
			orderings.put(new Ordering(issues), 0.0);
		}
		return orderings;		
	}

	@Override
	public List<Ordering> getTopOrderings(int topK) {
		List<Ordering> topOrders = new ArrayList<>();
		LinkedList<Entry<Ordering, Double>> sorted = new LinkedList<>(
				sortByValue(this.orderings).entrySet());
		Iterator<Entry<Ordering, Double>> iter = sorted.descendingIterator();
		// badly need java8 or guava library
		while(iter.hasNext() && topOrders.size() < topK) {
			Ordering ordering = iter.next().getKey();
			topOrders.add(ordering);
		}
		return topOrders;
	}

	@Override
	public OpponentModel update(Event event) {
		if (event.getOwner() == History.VH_ID) {
			return this;
		}
		switch(event.getType()) {
		case SEND_OFFER:
			return updateOfferReceived(event);		
		case FORMAL_ACCEPT:
			return updateOfferFormallyAccepted(event);
		case SEND_MESSAGE:
			return updateMessageReceived(event);
		default:
			return this;
		}
	}

	private OpponentModel updateMessageReceived(Event event) {
		
		/*
		 * Message codes are a way of quickly determining what message a 
		 * user has sent to the agent without doing a lot of natural 
		 * language processing or String comparisons. 
		 * Message codes 0 - 12 are reserved for the natural language 
		 * utterances specified in the menu. These are listed in the 
		 * example code in ResourceGameSpec.java. So, for example, 
		 * message code 4 corresponds to "Accept this or there will be consequences". 
		 * Message code 100 is reserved for an offer rejection, 
		 * and message code 101 is reserved for an offer acceptance.
		 */
		if( event.getMessageCode() == 101) {
			// TODO - offer accepted, get last proposed offer
		}
			
		return this;
	}

	private OpponentModel updateOfferFormallyAccepted(Event event) {

		return this; // TODO
	}

	private OpponentModel updateOfferReceived(Event event) {
		Offer offer = event.getOffer();
		Map<Integer, Double> issueAllocations = calcIssueAllocations(offer);
		Collection<Preference> prefs = distribution.getPreferences();
		
		Map<Preference, Double> newScores = scoresFromAllocations(issueAllocations, prefs);
		distribution.updateAll(newScores);
		
		for (Ordering ord : this.orderings.keySet()) {
			double likelihood = distribution.calcOrderLikelihood(ord);
			orderings.put(ord, likelihood);
		}
		
		return this;
		
	}

	private Map<Preference, Double> scoresFromAllocations(
			Map<Integer, Double> issueAllocations,
			Collection<Preference> prefs) {
		Map<Preference, Double> newScores = new HashMap<>();
		for (Preference pref : prefs) {
			boolean isContained = issueAllocations.containsKey(pref.getIssue1());
			isContained = isContained && issueAllocations.containsKey(pref.getIssue2());
			if (!isContained) {
				continue;
			}
			double alloc1 = issueAllocations.get(pref.getIssue1());
			double alloc2 = issueAllocations.get(pref.getIssue2());
			double score = calcScore(alloc1, alloc2);
			newScores.put(pref, score);
		}
		return newScores;
	}

	private Map<Integer, Double> calcIssueAllocations(Offer offer) {
		Map<Integer, Double> allocs = new HashMap<>();
		for (int issue = 0; issue < offer.getIssueCount(); issue++) {
			int[] allocation = offer.getItem(issue);
			int opponentValue = allocation[2];			
			int totalValue = allocation[0] + allocation[1] + allocation[2];
			if (totalValue == allocation[1]) {
				// Edge case where no quantity has been allocated.
				continue;
			}
			double allocationRate = opponentValue / (double) totalValue;
			int issueIndex = issue + 1; // The issues are 1-based
			allocs.put(issueIndex, allocationRate);
		}
		return allocs;
	}

	private double calcScore(double alloc1, double alloc2) {
		// Relation assumes that issue 1 < issue 2. 
		// Is that also reflected in their allocations?
		
		// allocations are real valued numbers between 0 and 1		
		return alloc2 - alloc1;
	}
	


}
