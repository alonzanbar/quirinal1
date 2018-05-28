package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Represents an order over issues
 * where Ordering of [3, 4, 1, 2]  means 3 < 4 < 1 < 2
 * Position is importance (higher position is better)
 * And the number in the list is the issue index.
 * 
 * Specifically, issue 2 is the most important, more than issues 3, 4 and 1
 * issue 1 is more important than 3 and 4
 * issue 4 is more important than 3
 * issue 3 is the least important
 * 
 * The preferences would be: [2, 1, 4, 3], meaning:
 * issue 1: 2nd place
 * issue 2: 1st place
 * issue 3: 4th place
 * issue 4: 3rd place    
 * @author Paul
 *
 */
public class Ordering {
	
	private final ArrayList<Integer> issues;
	
	public final int numIssues;
	
	public Ordering(ArrayList<Integer> issues) {
		this.issues = issues;
		this.numIssues = issues.size();
	}
	
	/**
	 * 
	 * @return a list where each issue (index in the list)
	 * is assigned a preference number. 
	 * 1 is the most preferred issue, 4 is the least preferred
	 * preferences of 3, 1, 4, 2 ==> 
	 * issue 2: most important
	 * issue 4: 2nd most important
	 * issue 1: 3rd most important
	 * issue 3: 4th most important 
	 * 
	 */
	public List<Integer> toPrefs() {
		int pref = numIssues;
		Integer[] prefs = new Integer[numIssues];
		Arrays.fill(prefs, 0);
		// issues in ordering are sorted by importance.
		// ord: i1, i2, i3, i4 ==> i1 < i2 < i3 < i4
		// prefs[i1] = 4
		for (int issue : issues) {
			prefs[issue - 1] = pref;
			pref--;
		}
		return Arrays.asList(prefs);
	}
	
	@Override
	public String toString() {
		List<String> strIssues = new ArrayList<>();
		for (int issue: issues) {
			strIssues.add(String.valueOf(issue));
		}
		return String.join(" < ", strIssues);
	}

	public int getIssue(int i) {		
		return issues.get(i);
	}
}
