package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;

public class Ordering {
	public final ArrayList<Integer> issues;
	
	public final int numIssues;
	
	public Ordering(ArrayList<Integer> issues) {
		this.issues = issues;
		this.numIssues = issues.size();
	}
	
	public int get(int index) {
		return issues.get(index);
	}
	
	@Override
	public String toString() {
		return issues.toString();
	}
}
