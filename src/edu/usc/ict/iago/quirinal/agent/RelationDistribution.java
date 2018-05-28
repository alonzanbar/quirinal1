package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;

public class RelationDistribution {
	
	private Map<String, Double> relationDist;
	
	private static final double EPSILON = 0.01;
	
	private int numIssues;
	
	private final Map<String , List<Double>> relationScores;
	
	public RelationDistribution(int numIssues) {
		this.numIssues = numIssues;
		this.relationScores = initializeRelations(numIssues);

		relationDist = recompute(relationScores);
	}	
	
	public void updateAll(Map<Preference, Double> newScores) {	
		if (newScores.isEmpty()) {
			// happens when an offer was made with a change to a single issue.
			// don't know how to score a relation based on a single issue
			return;
		}
		
		for(Entry<Preference, Double> entry : newScores.entrySet()) {
			Preference pref = entry.getKey();
			if (pref.getIssue1() >= pref.getIssue2() || pref.getRelation() != Relation.LESS_THAN) {
				throw new RuntimeException("Illegal preference argument: " + pref);
			}
					
			String pk1 = toKey(entry.getKey());			
			relationScores.get(pk1).add(entry.getValue());
		}
		
		relationDist = recompute(relationScores);

	}
	
	private static String toKey(int issue1, int issue2) {
		return String.valueOf(issue1) +" < " +String.valueOf(issue2); 
	}
	
	private static String toKey(Preference pref) {
		if (pref.getRelation() != Relation.LESS_THAN) {
			throw new RuntimeException("Relation must be less than on: "  + pref);
		}
		
		return toKey(pref.getIssue1(), pref.getIssue2());
	}
	
	private Map<String, Double> recompute(Map<String, List<Double>> allScores) {
		Map<String, Double> recomputedDist = new HashMap<>();		
		
		for (Integer issue1 = 1; issue1 < numIssues; issue1++) {
			for (Integer issue2 = issue1 + 1; issue2 < numIssues + 1; issue2++) {
				String pk = toKey(issue1, issue2);
				double avg = average(allScores.get(pk));
				double sigmoid = 1.0 / (1 + Math.exp(-avg));
				recomputedDist.put(pk, sigmoid);
			}
		}		
		return recomputedDist;
	}	
	
	private double getProb(int issue1, int issue2) {
		int i1 = issue1;
		int i2 = issue2;
		boolean isComplementary = issue1 > issue2;
		if (isComplementary) {
			i1 = issue2;
			i2 = issue1;			
		}
		double prob = this.relationDist.get(toKey(i1, i2));
		prob = isComplementary ? 1 - prob : prob;
		return prob;
	}
	
	public double calcOrderLikelihood(Ordering ord) {
		double sumLogProb = 0.0;
		// ordering: [3, 4, 1, 2]
		// interpreted as: (3 < 4) AND (3 < 1) AND (3 < 2) AND (4 < 1) AND (4 < 2) AND AND (1 < 2)   
		for (int i = 0; i < ord.numIssues - 1; i++) {
			for (int j = i + 1; j < ord.numIssues; j++) {
				
				int issue1 = ord.getIssue(i);
				int issue2 = ord.getIssue(j);	
				
				double prob = getProb(issue1, issue2);			
				sumLogProb += Math.log(prob);
			}
		}
		double prob = Math.exp(sumLogProb);
		return prob;
	}
	
	private static Map<String, List<Double>> initializeRelations(int numIssues) {
		Map<String, List<Double>> relationScores = new HashMap<>();
		for (Integer issue1 = 1; issue1 < numIssues; issue1++) {
			for (Integer issue2 = issue1 + 1; issue2 < numIssues + 1; issue2++) {
				String pk1 = toKey(issue1, issue2);
				relationScores.put(pk1, new ArrayList<>());
				// start with a slight bias toward 1 < 2 < 3 < 4 order
				relationScores.get(pk1).add(EPSILON);
			}
		}
		return relationScores;
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
