package edu.usc.ict.iago.quirinal.agent;

import java.util.List;

import edu.usc.ict.iago.utils.Event;

public interface OpponentModel {
	
	List<Ordering> getTopOrderings(int topK);
	
	OpponentModel update(Event event);

}
