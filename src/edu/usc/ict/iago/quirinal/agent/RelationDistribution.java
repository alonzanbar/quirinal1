package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;

public class RelationDistribution {
	
	private Map<Preference, Double> relationDist;
	
	private final Map<Preference, List<Double>> relationScores; 	
	
	public RelationDistribution(int numIssues) {
		relationScores = initializeRelations(numIssues);

		relationDist = recompute(relationScores);
	}	
	
	public void updateAll(Map<Preference, Double> newScores) {	
		if (newScores.isEmpty()) {
			// happens when an offer was made with a change to a single issue.
			// don't know how to score a relation based on a single issue
			return;
		}
		for(Entry<Preference, Double> entry : newScores.entrySet()) {
			// caveat if preference here does not match with preference
			// we hold! for example if the order is reversed.
			if (!relationScores.containsKey(entry.getKey())) {
				throw new RuntimeException("Relation not found: (equals? getHashCode?)" + entry.getKey());
			}
			
			relationScores.get(entry.getKey()).add(entry.getValue());
		}
		
		relationDist = recompute(relationScores);

	}
	
	private static Map<Preference, Double> recompute(Map<Preference, List<Double>> allScores) {
		Map<Preference, Double> recomputedDist = new HashMap<>();		
		for(Entry<Preference, List<Double>> entry : allScores.entrySet()) {
			List<Double> scores = entry.getValue();
			recomputedDist.put(entry.getKey(), average(scores));				
		}
		
		double sumExpScores = 0.0;
		for (double score: recomputedDist.values()) {
			sumExpScores += Math.exp(score);
		}
		
		for(Preference pref : recomputedDist.keySet()) {
			double score = recomputedDist.get(pref);
			double normScore = Math.exp(score) / sumExpScores;
			recomputedDist.put(pref, normScore);
		}
		return recomputedDist;
	}
	
	private double getProbability(int issue1, int issue2) {
		// Preference does not implement equals and getHashCode. 
		// Can't directly locate via a cloned object
		// Also - we create relations for issues in ascending order.
		if (issue1 > issue2) {
			int temp = issue2;
			issue2 = issue1; 
			issue1 = temp;
		}
		
		for(Preference pref : relationDist.keySet()) {
			
			boolean isMatch = (pref.getIssue1() == issue1 && pref.getIssue2() == issue2);					
			if (isMatch) {
				return relationDist.get(pref);
			}
		}
		
		throw new RuntimeException("Preference not found: " + issue1 + ", " + issue2);
	}
	
	public double calcOrderLikelihood(Ordering ord) {
		double sumLogProb = 0.0;
		
		for (int i = 0; i < ord.numIssues - 1; i++) {
			for (int j = i+1; j < ord.numIssues; j++) {
				int issue1 = ord.get(i);
				int issue2 = ord.get(j);
				double prob = getProbability(issue1, issue2);
				sumLogProb += Math.log(prob);
			}
		}
		double prob = Math.exp(sumLogProb);
		return prob;
	}
	
	private Map<Preference, List<Double>> initializeRelations(int numIssues) {
		Map<Preference, List<Double>> scores = new HashMap<>(); 
		for (int issue1 = 1; issue1 < numIssues; issue1++) {
			for (int issue2 = issue1 + 1; issue2 < numIssues + 1; issue2++) {
				Preference pref = new Preference(issue1, issue2, Relation.LESS_THAN, false);
				scores.put(pref, new ArrayList<>());
			}
		}		
		return scores;
	}

	public Collection<Preference> getPreferences() {
		return relationScores.keySet();
	}
	
	private static double average(List<Double> doubles) {
		if(doubles.isEmpty()) {
			return 0;
		}
		double s = 0.0;
		for(double d : doubles) {
			s+=d;
		}
		
		return s/doubles.size();
	}	
	
	@Override
	public String toString() {
		return relationDist.toString();
	}
}
