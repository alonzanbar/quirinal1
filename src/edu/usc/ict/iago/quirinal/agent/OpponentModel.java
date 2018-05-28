package edu.usc.ict.iago.quirinal.agent;

import java.util.List;

import edu.usc.ict.iago.utils.Event;

public interface OpponentModel {
	
	/**
	 * Retrieves the k-th top ordering for the opponent.
	 * K starts at 0
	 * K == 0 means the most plausible opponent order.
	 * where order of [3, 1, 4, 2] ==> 3 < 1 < 4 < 2
	 * @param topK
	 * @return
	 */
	Ordering getTopOrderings(int topK);
	
	/**
	 * Preferences of [3, 4, 1, 2]
	 * correspond to:
	 * Issue 1: 3rd place
	 * Issue 2: 4th place
	 * Issue 3: 1st place
	 * Issue 4: 2nd place
	 * an ordering of: [2, 1, 4, 3]
	 * @return
	 */
	public default List<Integer> getTopPreferences() {
		return getTopOrderings(0).toPrefs();
	}
	
	OpponentModel update(Event event);

}
