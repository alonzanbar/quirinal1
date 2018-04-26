package edu.usc.ict.iago.views;

import java.util.HashMap;
import java.util.Map;

import edu.usc.ict.iago.utils.GameSpec;

class AuctionWarsGameSpec extends GameSpec{
	
	
	
	
	/**
	 * This is an example constructor that uses privileged calls to the superclass to set its variables.  It is package private,
	 * which prohibits inheritance, and thus should prevent users in agent packages from maliciously accessing privileged information.
	 */
	AuctionWarsGameSpec(boolean privileged) 
	{
		//VH preferences
		Map<String, Integer> simpleVHPoints = new HashMap<String, Integer>();
		Map<String, Integer> simplePlayerPoints = new HashMap<String, Integer>();
		
		simpleVHPoints.put(getIssuePluralNames()[0], 20);
		simpleVHPoints.put(getIssuePluralNames()[1], 10);
		simpleVHPoints.put(getIssuePluralNames()[2], 5);
		
		simplePlayerPoints.put(getIssuePluralNames()[0], 10);
		simplePlayerPoints.put(getIssuePluralNames()[1], 30);
		simplePlayerPoints.put(getIssuePluralNames()[2], 0);
		
		//Privileged calls -- cannot be called outside package.
		setSimpleVHPoints(simpleVHPoints);
		setSimplePlayerPoints(simplePlayerPoints);	
		setPlayerBATNA(30);
		setVHBATNA(25);
		setIndexMenu(super.buildMenu());
		if(privileged)
			enablePrivilege();
	}

	@Override
	public int getNumIssues() {
		return 3;
	}

	@Override
	public int[] getIssueQuants() {
		return new int[] {3,2,1};
	}

	@Override
	public String[] getIssuePluralNames() {
		return new String[] {"crates of records", "lamps", "paintings"};
	}

	@Override
	public String[] getIssueNames() {
		return new String[] {"crate of records", "lamp", "painting"};
	}

	@Override
	public boolean isAdvancedPoints() {
		return false;
	}
	
	@Override
	public int getTotalTime() {
		return 300;
	}

	@Override
	public boolean showAgentScore() {
		return false;
	}
	
	@Override
	public String getTargetEmail()
	{
		return "mell@ict.usc.edu";
	}

	@Override
	public String getEndgameMessage() {
		return "<p>Thank you for participating.</p>";
	}

	@Override
	public boolean showOpponentScoreOnEnd() {
		return true;
	}

	@Override
	public boolean showNegotiationTimer() {
		return true;
	}


	@Override
	public String getStudyName() {
		return "AuctionWars";
	}

}
