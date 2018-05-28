package edu.usc.ict.iago.quirinal.agent;

import static edu.usc.ict.iago.utils.MathUtils.sortByValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;

public class HeuristicOpponentModel implements OpponentModel{

	private static final double CONST_PREF_W = 0.9;

	private Map<Ordering, Double> orderings;
	
	private final RelationDistribution distribution;
	
	private final int numIssues;
	
	public HeuristicOpponentModel(GameSpec game) {
		this.numIssues = game.getNumIssues();
		orderings = initializeOrderings(numIssues);
		distribution = new RelationDistribution(numIssues);	
		orderings = calcLikelihoodAllOrdering(orderings);
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
	public Ordering getTopOrderings(int topK) {
		if (topK >= orderings.size()) {
			topK = orderings.size() - 1;
		}
		
		LinkedList<Entry<Ordering, Double>> sorted = new LinkedList<>(
				sortByValue(this.orderings).entrySet());
		return sorted.get(topK).getKey();
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
		if(event.getPreference() != null && !event.getPreference().isQuery()) // preference accepted
		{
			return updatePrefReceived(event.getPreference());
		}
		return this;
	}

	private OpponentModel updatePrefReceived(Preference preference) {
		Map<Preference, Double> newScores = getScoresFromPref(preference);			
		return update(newScores);
	}

	private Map<Preference, Double> getScoresFromPref(Preference preference) {
		Preference augmented = new Preference(preference);
		augmented.setIssue1(augmented.getIssue1() + 1);
		augmented.setIssue2(augmented.getIssue2() + 1);
		
		Map<Preference, Double> newScores = new HashMap<Preference, Double>();

		int issue1 = augmented.getIssue1();
		int issue2 = augmented.getIssue2();
		int sign = 1;
		int temp = 0;
		switch (augmented.getRelation()) {
		case LESS_THAN:
		case GREATER_THAN:
			if(augmented.getRelation() == Relation.GREATER_THAN) {
				// swap issues to get the less than relation
				temp = issue2; 
				issue2 = issue1;
				issue1 = temp;
			}
			// check if you get the complementary relation
			if (issue2 < issue1) {
				// got the complementary relation. change score sign to negative
				// and swap issues
				sign = -1;
				temp = issue2; 
				issue2 = issue1;
				issue1 = temp;
			}			
			newScores.put(createPref(issue1, issue2), sign*CONST_PREF_W);
			break;
		case BEST:
			int bestIssue = augmented.getIssue1();
			for(int i = 1; i < this.numIssues + 1; i++) {
				if (i == bestIssue) {
					continue;
				}
				newScores.put(createPref(i, bestIssue), CONST_PREF_W);
			}
			break;
		case WORST:
			int worstIssue = augmented.getIssue1();
			for(int i = 1; i < this.numIssues + 1; i++) {
				if (i == worstIssue) {
					continue;
				}
				newScores.put(createPref(worstIssue, i), CONST_PREF_W);
			}
			break;
		default:
			// Do nothing
		}
		return newScores;
	}
	
	private OpponentModel update(Map<Preference, Double> newScores) {
		distribution.updateAll(newScores);
		orderings = calcLikelihoodAllOrdering(orderings);
		return this;
	}

	private OpponentModel updateOfferFormallyAccepted(Event event) {

		return this; // TODO
	}

	private OpponentModel updateOfferReceived(Event event) {
		Offer offer = event.getOffer();
		Map<Integer, Double> issueAllocations = calcIssueAllocations(offer);		
		Map<Preference, Double> newScores = scoresFromAllocations(issueAllocations);
		return update(newScores);		
	}

	private Map<Ordering, Double> calcLikelihoodAllOrdering(Map<Ordering, Double> orderProbs) {
		Map<Ordering, Double> newProbs = new HashMap<>();
		for (Ordering ord : orderProbs.keySet()) {			
			double likelihood = distribution.calcOrderLikelihood(ord);
			newProbs.put(ord, likelihood);
		}		
		return newProbs;
	}
	
	private static Preference createPref(int issue1, int issue2) {
		return new Preference(issue1, issue2, Relation.LESS_THAN, false);
	}

	private Map<Preference, Double> scoresFromAllocations(
			Map<Integer, Double> issueAllocations) {
		Map<Preference, Double> newScores = new HashMap<>();
		for (int issue1 = 1; issue1 < numIssues; issue1++) {
			for (int issue2 = issue1 + 1; issue2 < numIssues + 1; issue2++) {
				boolean has1 = issueAllocations.containsKey(issue1);
				boolean has2 = issueAllocations.containsKey(issue2);
				if(!has1 || !has2) {
					continue;
				}				
				double alloc1 = issueAllocations.get(issue1);
				double alloc2 = issueAllocations.get(issue2);
				double score = calcScore(alloc1, alloc2);

				newScores.put(createPref(issue1, issue2), score);				
			}
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
