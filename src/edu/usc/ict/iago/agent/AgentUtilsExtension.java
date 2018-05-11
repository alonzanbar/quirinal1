package edu.usc.ict.iago.agent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.Preference.Relation;

public class AgentUtilsExtension 
{
	protected GameSpec game;
	protected ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
	protected int[][] permutations;
	protected LinkedList<Preference> preferences = new LinkedList<Preference>();
	protected ArrayList<ArrayList<Integer>> offers = new ArrayList<ArrayList<Integer>>();
	
	/**
	 * Configures initial parameters for the given game.
	 * @param game the game being played.
	 */
	protected void configureGame(GameSpec game)
	{
		this.game = game;
		permutations = MathUtils.getPermutations(game.getNumIssues(), 1);//offset by 1, so we will be 1-indexed
		orderings = new ArrayList<ArrayList<Integer>>();
		preferences = new LinkedList<Preference>();
	}
	
	public GameSpec getSpec()
	{
		return game;
	}
	
	
	/**
	 * Adds the given preference to the list of preferences.
	 * @param p the preference to add
	 */
	protected void addPref (Preference p)
	{
		preferences.add(p);
	}
	
	
	
	/**
	 * Removes the 0th element in the preferences queue.
	 * @return the preference removed, or thows IndexOutOfBoundException 
	 */
	protected Preference dequeuePref()
	{
		return preferences.remove(0);
	}
	
	/**
	 * Returns the VH value of an offer.
	 * @param o the offer
	 * @return the total value
	 */
	public int myActualOfferValue(Offer o) {
		int ans = 0;
		for (int num = 0; num < game.getNumIssues(); num++)
			ans += o.getItem(num)[0] * game.getSimpleVHPoints().get(game.getIssuePluralNames()[num]);
		return ans;
	}
	
	/**
	 * Returns the VH value of an ordering.
	 * @param o the ordering
	 * @return the total value
	 */
	public int myActualOrderValue(ArrayList<Integer> o) {
		int ans = 0;
		for (int num = 0; num < game.getNumIssues(); num++)
			ans += o.get(num) * game.getSimpleVHPoints().get(game.getIssuePluralNames()[num]);
		return ans;
	}
	
	
	
	/**
	 * Check to see if the offer is a full offer.
	 * @param o the offer
	 * @return is full offer
	 */
	protected boolean isFullOffer(Offer o)
	{
		boolean ans = true;
		for (int num = 0; num < game.getNumIssues(); num++)
			if(o.getItem(num)[1] > 0)
				ans = false;
		return ans;
	}

	/**
	 * Returns the normalized ordering of VH preferences (e.g., a point value of {3, 7, 2} would return {2, 1, 3}), with 1 being the highest
	 * @return an arraylist of preferences
	 */
	public ArrayList<Integer> getVHOrdering() 
	{
		int rating = 1;
		ArrayList<Integer> ans = new ArrayList<Integer>(game.getNumIssues());
		ArrayList<Integer> sortedIndices = new ArrayList<Integer>(game.getNumIssues());
		for(int init = 0; init < game.getNumIssues(); init++)
			ans.add(0);
		
		for (int i = 0; i < game.getNumIssues(); i++)
		{
			int max = 0;
			int value = 0;
			int index = 0;
			for (int j = 0; j < game.getNumIssues(); j++)
			{
				value = game.getSimpleVHPoints().get(game.getIssuePluralNames()[j]);
				if (value > max && !sortedIndices.contains(j))
				{
					max = value;
					index = j;
				}
			}
			sortedIndices.add(index);
			ans.set(index, rating);
			rating++;
		}
		return ans;
	}
	/**
	 * Returns the expected opponent value on an offer for a given ordering of preferences.
	 * @param o the offer
	 * @param ordering the ordering
	 * @return the total value
	 */
	protected int opponentValue(Offer o, ArrayList<Integer> ordering) {
		int ans = 0;
		for (int num = 0; num < game.getNumIssues(); num++)
			ans += o.getItem(num)[2] * (game.getNumIssues() - ordering.get(num) + 1);
		return ans;
	}
	
	/**
	 * Returns the maximum opponent value on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	public int opponentValueMax(Offer o)
	{
		int max = 0;
		for (ArrayList<Integer> order : orderings)
			max = Math.max(max,  opponentValue(o, order));
		return max;
	}
	
	/**
	 * Returns the minimum opponent value on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	public int opponentValueMin(Offer o)
	{
		int min = 0;
		for (ArrayList<Integer> order : orderings)
			min = Math.min(min,  opponentValue(o, order));
		return min;
	}
	
	/**
	 * Returns the last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event lastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		for (int i = history.size() - 1; i > 0; i--)
		{
			if(history.get(i).getType() == type)
				return history.get(i);
		}
		return null;
	}
	
	/**
	 * Returns the second to last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event secondLastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		boolean foundFirst = false;
		for (int i = history.size() - 1; i > 0; i--)
		{
			if(history.get(i).getType() == type && !foundFirst)
			{
				foundFirst = true;
				continue;
			}
			else if (history.get(i).getType() == type && foundFirst)
				return history.get(i);
		}
		return null;
	}
	
	/**
	 * Eliminates invalid orderings by looking at preferences, the oldest ones first.
	 * @return true if there are no valid orderings, false otherwise
	 */
	protected boolean reconcileContradictions()
	{
		orderings = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < permutations.length; i++)
		{
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < permutations[i].length; j++)
			{
				a.add(permutations[i][j]);
			}
			orderings.add(a);
		}
		
		for (Preference pref: preferences)
		{
			ServletUtils.log(pref.toString(), ServletUtils.DebugLevels.DEBUG);
			Relation r = pref.getRelation();
			ArrayList<ArrayList<Integer>> toRemove = new ArrayList<ArrayList<Integer>>();
			if(r == Relation.BEST)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) != 1)//if the ordering does not have the item as number 1 
						toRemove.add(o);
				}
			}
			else if(r == Relation.WORST)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) != game.getNumIssues())//if the ordering does not have the item as the last place 
						toRemove.add(o);
				}
			}
			else if(r == Relation.GREATER_THAN)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) > o.get(pref.getIssue2()))//if the ordering does not have the item greater
						toRemove.add(o);
				}
			}
			else if(r == Relation.LESS_THAN)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(o.get(pref.getIssue1()) < o.get(pref.getIssue2()))//if the ordering does not have the item lesser 
						toRemove.add(o);
				}
			}
			else if(r == Relation.EQUAL)
			{
				//kludge when vague information is supplied
				if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
					continue;
				for (int x = 0; x < orderings.size(); x++)
				{
					ArrayList<Integer> o = orderings.get(x);
					if(Math.abs(o.get(pref.getIssue1()) - o.get(pref.getIssue2())) == 1)//if the ordering does not have the items adjacent 
						toRemove.add(o);
				}
			}
			
			for(ArrayList<Integer> al : toRemove)
				orderings.remove(al);
			
			ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		}
		ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		
		if(orderings.size() == 0)
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * Finds the ordering among possible orderings that is most different than the VH's ordering, since that means the greatest chance of integrative potential.
	 * @return the chosen ordering.
	 */
	public ArrayList<Integer> getMinimaxOrdering()
	{
		int valueHeuristic = 0;
		int max = -1;
		ArrayList<Integer> ans = null;
		//just in case this hasn't been run yet
		reconcileContradictions();
		for(ArrayList<Integer> order: orderings)
		{
			for (int i = 0; i < order.size(); i++)
			{
				valueHeuristic += Math.abs(order.get(i) - getVHOrdering().get(i));
			}
			if (valueHeuristic > max)
			{
				max = valueHeuristic;
				ans = order;
			}
		}
		return ans;
	}
	
	
}
