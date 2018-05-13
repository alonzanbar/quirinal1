package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Collections;

import edu.usc.ict.iago.agent.AgentUtilsExtension;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.MathUtils;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.Preference.Relation;

public class AgentQuirinaUtilsExtension extends AgentUtilsExtension
{
	private GameSpec game;
	private ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
	private LinkedHashMap<ArrayList<Integer>,Integer> orderMLE = new LinkedHashMap<ArrayList<Integer>,Integer>();
	private int[][] permutations;
	private LinkedHashSet<Preference> preferences = new LinkedHashSet<Preference>();
	private ArrayList<Integer> HVuitility= null;
	private ArrayList<Integer> playeruitility= null;
	private ArrayList<ArrayList<Integer>> offers = new ArrayList<ArrayList<Integer>>();
	
	/**
	 * Configures initial parameters for the given game.
	 * @param game the game being played.
	 */
	protected void configureGame(GameSpec game)
	{
		this.game = game;
		permutations = MathUtils.getPermutations(game.getNumIssues(), 1);//offset by 1, so we will be 1-indexed
		orderings = new ArrayList<ArrayList<Integer>>();
		orderMLE = new LinkedHashMap<ArrayList<Integer>,Integer>();
		preferences = new LinkedHashSet<Preference>();
		for (int i = 0; i < permutations.length; i++)
		{
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < permutations[i].length; j++)
			{
				a.add(permutations[i][j]);
			}
			orderings.add(a);
			orderMLE.put(a, 0);
		}
		
		HVuitility = new ArrayList<Integer>(game.getNumIssues());
		
		playeruitility = new ArrayList<Integer>(game.getNumIssues());
		for (int j = 0; j < game.getNumIssues(); j++)
		{
			playeruitility.add(0);
			HVuitility.add(j, game.getSimpleVHPoints().get(game.getIssuePluralNames()[j])) ;
		}
		
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
		if (!preferences.contains(p))
		{
			preferences.add(p);
			adjustPlayerUtilityByPref(p);
		}
	}
	
	/**
	 * Removes the 0th element in the preferences queue.
	 * @return the preference removed, or thows IndexOutOfBoundException 
	 */
	protected Preference dequeuePref()
	{
		Preference p =  preferences.iterator().next();
		preferences.remove(p);
		return p;
	}
	
	/**
	 * Returns the VH value of an offer.
	 * @param o the offer
	 * @return the total value
	 */
	protected int myActualOfferValue(Offer o) {
		int ans = 0;
		for (int num = 0; num < game.getNumIssues(); num++)
			ans += o.getItem(num)[0] * game.getSimpleVHPoints().get(game.getIssuePluralNames()[num]);
		return ans;
	}
	
	/**
	 * Returns the player value of an ordering. not available at run time 
	 * @param o the ordering
	 * @return the total value
	 */
	protected ArrayList<Integer> getPlayerActualUtility() {
		ArrayList<Integer> playerActualUtility =  new ArrayList<Integer>(game.getNumIssues());
		for (int j = 0; j < game.getNumIssues(); j++)
		{
			playerActualUtility.add(j, game.getSimplePlayerPoints().get(game.getIssuePluralNames()[j])) ;
		}
		return playerActualUtility;
	}
	
	protected List<ArrayList<Integer>> getTopOrderings()
	{
		orderMLE = MapUtil.sortByValue(orderMLE);
		ArrayList<ArrayList<Integer>> offers = new ArrayList<ArrayList<Integer>>();
		Set<Entry<ArrayList<Integer>, Integer>> entryset = orderMLE.entrySet();
		Integer topvalue  = -1;
		Iterator<Entry<ArrayList<Integer>, Integer>>itr = entryset.iterator();
		while(itr.hasNext() ){
			Entry<ArrayList<Integer>,Integer> e= itr.next();
			if (e.getValue().equals(topvalue) || topvalue==-1) {
				offers.add(e.getKey());
				topvalue = e.getValue();
			}
			else {
				break;
			}
			
		}
		
		return offers;
		
	}
	
	
	/**
	 * Returns the VH value of an ordering.
	 * @param o the ordering
	 * @return the total value
	 */
	protected int myActualOrderValue(ArrayList<Integer> o) {
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
		return getOrder(HVuitility);
	}

	private ArrayList<Integer> getOrder(ArrayList<Integer> utility) {
		int rating = 1;
		ArrayList<Integer> ans = new ArrayList<Integer>(game.getNumIssues());
		ArrayList<Integer> sortedIndices = new ArrayList<Integer>(game.getNumIssues());
		for(int init = 0; init < game.getNumIssues(); init++)
			ans.add(0);
		
		for (int i = 0; i < game.getNumIssues(); i++)
		{
			int max = Integer.MIN_VALUE;
			int value = 0;
			int index = 0;
			for (int j = 0; j < game.getNumIssues(); j++)
			{
				value = utility.get(j);
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
	protected int opponentValueMax(Offer o)
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
	protected int opponentValueMin(Offer o)
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
	protected boolean adjustPlayerUtilityByPref(Preference pref)
	{
		
		ServletUtils.log(pref.toString(), ServletUtils.DebugLevels.DEBUG);
		Relation r = pref.getRelation();
		if(r == Relation.BEST)
		{
			//kludge when vague information is supplied
			if (pref.getIssue1() == -1)//if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++)
			{
				ArrayList<Integer> o = orderings.get(x);
				if(o.get(pref.getIssue1()) == 1)//if the ordering does not have the item as number 1 
					orderMLE.put(o, orderMLE.get(o)+1);
			}
		}
		else if(r == Relation.WORST)
		{
			//kludge when vague information is supplied
			if (pref.getIssue1() == -1)//if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++)
			{
				ArrayList<Integer> o = orderings.get(x);
				if(o.get(pref.getIssue1()) == game.getNumIssues())//if the ordering does not have the item as the last place 
					orderMLE.put(o, orderMLE.get(o)+1);
			}
		}
		else if(r == Relation.GREATER_THAN)
		{
			//kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++)
			{
				ArrayList<Integer> o = orderings.get(x);
				if(o.get(pref.getIssue2()) > o.get(pref.getIssue1()))//if the ordering does not have the item greater
					orderMLE.put(o, orderMLE.get(o)+1);
			}
		}
		else if(r == Relation.LESS_THAN)
		{
			//kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++)
			{
				ArrayList<Integer> o = orderings.get(x);
				if(o.get(pref.getIssue2()) < o.get(pref.getIssue1()))//if the ordering does not have the item lesser 
					orderMLE.put(o, orderMLE.get(o)+1);
			}
		}
		else if(r == Relation.EQUAL)
		{
			//kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)//if information not filled in
				
			for (int x = 0; x < orderings.size(); x++)
			{
				ArrayList<Integer> o = orderings.get(x);
				if(Math.abs(o.get(pref.getIssue1()) - o.get(pref.getIssue2())) == 0)//if the ordering does not have the items adjacent 
					orderMLE.put(o, orderMLE.get(o)+1);
			}
		}
		
		orderMLE = MapUtil.sortByValue(orderMLE);
		
		ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		return true;
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
		for(ArrayList<Integer> order: getTopOrderings())
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

class MapUtil {
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	
}


