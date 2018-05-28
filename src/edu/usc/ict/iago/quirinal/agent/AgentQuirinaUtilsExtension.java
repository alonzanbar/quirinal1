package edu.usc.ict.iago.quirinal.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Collections;

import edu.usc.ict.iago.agent.AgentUtilsExtension;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.ServletUtils;
import edu.usc.ict.iago.utils.Preference.Relation;

public class AgentQuirinaUtilsExtension extends AgentUtilsExtension {

	private LinkedHashMap<ArrayList<Integer>, Integer> orderMLE = new LinkedHashMap<ArrayList<Integer>, Integer>();

	private ArrayList<Integer> VHUtility = null;
	private ArrayList<Integer> playerUtility = null;

	private OpponentModel oppoModel;

	/**
	 * Configures initial parameters for the given game.
	 * 
	 * @param game
	 *            the game being played.
	 */
	@Override
	protected void configureGame(GameSpec game) {
		super.configureGame(game);

		orderMLE = new LinkedHashMap<ArrayList<Integer>, Integer>();
		for (int i = 0; i < permutations.length; i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int j = 0; j < permutations[i].length; j++) {
				a.add(permutations[i][j]);
			}
			orderings.add(a);
			orderMLE.put(a, 0);
		}

		VHUtility = new ArrayList<Integer>(game.getNumIssues());

		playerUtility = new ArrayList<Integer>(game.getNumIssues());
		for (int j = 0; j < game.getNumIssues(); j++) {
			playerUtility.add(0);
			VHUtility.add(j, game.getSimpleVHPoints().get(game.getIssuePluralNames()[j]));
		}
	}

	/**
	 * Adds the given preference to the list of preferences.
	 * 
	 * @param p
	 *            the preference to add
	 */
	@Override
	protected void addPref(Preference p) {
		if (!preferences.contains(p)) {
			preferences.add(p);
			adjustPlayerUtilityByPref(p);
		}
	}

	/**
	 * Returns the player value of an ordering. not available at run time
	 * 
	 * @param o
	 *            the ordering
	 * @return the total value
	 */
	protected ArrayList<Integer> getPlayerActualUtility() {
		ArrayList<Integer> playerActualUtility = new ArrayList<Integer>(game.getNumIssues());
		for (int j = 0; j < game.getNumIssues(); j++) {
			playerActualUtility.add(j, game.getSimplePlayerPoints().get(game.getIssuePluralNames()[j]));
		}
		return playerActualUtility;
	}

	protected List<ArrayList<Integer>> getTopOrderings() {
		orderMLE = MapUtil.sortByValue(orderMLE);
		ArrayList<ArrayList<Integer>> offers = new ArrayList<ArrayList<Integer>>();
		Set<Entry<ArrayList<Integer>, Integer>> entryset = orderMLE.entrySet();
		Integer topvalue = -1;
		Iterator<Entry<ArrayList<Integer>, Integer>> itr = entryset.iterator();
		while (itr.hasNext()) {
			Entry<ArrayList<Integer>, Integer> e = itr.next();
			if (e.getValue().equals(topvalue) || topvalue == -1) {
				offers.add(e.getKey());
				topvalue = e.getValue();
			} else {
				break;
			}

		}

		return offers;

	}

	/**
	 * Returns the normalized ordering of VH preferences (e.g., a point value of {3,
	 * 7, 2} would return {2, 1, 3}), with 1 being the highest
	 * 
	 * @return an arraylist of preferences
	 */
	public ArrayList<Integer> getVHOrdering() {
		return getOrder(VHUtility);
	}

	private ArrayList<Integer> getOrder(ArrayList<Integer> utility) {
		int rating = 1;
		ArrayList<Integer> ans = new ArrayList<Integer>(game.getNumIssues());
		ArrayList<Integer> sortedIndices = new ArrayList<Integer>(game.getNumIssues());
		for (int init = 0; init < game.getNumIssues(); init++)
			ans.add(0);

		for (int i = 0; i < game.getNumIssues(); i++) {
			int max = Integer.MIN_VALUE;
			int value = 0;
			int index = 0;
			for (int j = 0; j < game.getNumIssues(); j++) {
				value = utility.get(j);
				if (value > max && !sortedIndices.contains(j)) {
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
	 * Eliminates invalid orderings by looking at preferences, the oldest ones
	 * first.
	 * 
	 * @return true if there are no valid orderings, false otherwise
	 */
	protected boolean adjustPlayerUtilityByPref(Preference pref) {

		ServletUtils.log(pref.toString(), ServletUtils.DebugLevels.DEBUG);
		Relation r = pref.getRelation();
		if (r == Relation.BEST) {
			// kludge when vague information is supplied
			if (pref.getIssue1() == -1)// if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++) {
				ArrayList<Integer> o = orderings.get(x);
				if (o.get(pref.getIssue1()) == 1)// if the ordering does not have the item as number 1
					orderMLE.put(o, orderMLE.get(o) + 1);
			}
		} else if (r == Relation.WORST) {
			// kludge when vague information is supplied
			if (pref.getIssue1() == -1)// if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++) {
				ArrayList<Integer> o = orderings.get(x);
				if (o.get(pref.getIssue1()) == game.getNumIssues())// if the ordering does not have the item as the last
																	// place
					orderMLE.put(o, orderMLE.get(o) + 1);
			}
		} else if (r == Relation.GREATER_THAN) {
			// kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)// if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++) {
				ArrayList<Integer> o = orderings.get(x);
				if (o.get(pref.getIssue2()) > o.get(pref.getIssue1()))// if the ordering does not have the item greater
					orderMLE.put(o, orderMLE.get(o) + 1);
			}
		} else if (r == Relation.LESS_THAN) {
			// kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)// if information not filled in
				return false;
			for (int x = 0; x < orderings.size(); x++) {
				ArrayList<Integer> o = orderings.get(x);
				if (o.get(pref.getIssue2()) < o.get(pref.getIssue1()))// if the ordering does not have the item lesser
					orderMLE.put(o, orderMLE.get(o) + 1);
			}
		} else if (r == Relation.EQUAL) {
			// kludge when vague information is supplied
			if (pref.getIssue1() == -1 || pref.getIssue2() == -1)// if information not filled in

				for (int x = 0; x < orderings.size(); x++) {
					ArrayList<Integer> o = orderings.get(x);
					if (Math.abs(o.get(pref.getIssue1()) - o.get(pref.getIssue2())) == 0)// if the ordering does not
																							// have the items adjacent
						orderMLE.put(o, orderMLE.get(o) + 1);
				}
		}

		orderMLE = MapUtil.sortByValue(orderMLE);

		ServletUtils.log(orderings.toString(), ServletUtils.DebugLevels.DEBUG);
		return true;
	}

	/**
	 * Finds the ordering among possible orderings that is most different than the
	 * VH's ordering, since that means the greatest chance of integrative potential.
	 * 
	 * @return the chosen ordering.
	 */
	public ArrayList<Integer> getMinimaxOrdering() {
		int valueHeuristic = 0;
		int max = -1;
		ArrayList<Integer> ans = null;
		// just in case this hasn't been run yet
		for (ArrayList<Integer> order : getTopOrderings()) {
			for (int i = 0; i < order.size(); i++) {
				valueHeuristic += Math.abs(order.get(i) - getVHOrdering().get(i));
			}
			if (valueHeuristic > max) {
				max = valueHeuristic;
				ans = order;
			}
		}
		return ans;
	}

	public void setOpponentModel(OpponentModel opponentModel) {
		// TODO Auto-generated method stub
		this.oppoModel = opponentModel;
	}
	
	public OpponentModel getOpponentModel() {
		return oppoModel;
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
