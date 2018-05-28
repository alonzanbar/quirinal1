package edu.usc.ict.iago.quirinal.agent;

import edu.usc.ict.iago.utils.Event;


public class AcceptCounter {
	
	private int accepted;
	
	/*
	 * Message codes are a way of quickly determining what message a 
	 * user has sent to the agent without doing a lot of natural 
	 * language processing or String comparisons. 
	 * Message codes 0 - 12 are reserved for the natural language 
	 * utterances specified in the menu. These are listed in the 
	 * example code in ResourceGameSpec.java. So, for example, 
	 * message code 4 corresponds to "Accept this or there will be consequences". 
	 * Message code 100 is reserved for an offer rejection, 
	 * and message code 101 is reserved for an offer acceptance.
	 */
	private final static int REJECTED = 100;
	private final static int ACCEPTED = 101;
	
	public AcceptCounter update(Event event) {
		switch (event.getType()) {
		case SEND_MESSAGE:
			int messageCode = event.getMessageCode();
			switch (messageCode) {
			case ACCEPTED:
				return hasAccepted();
			case REJECTED:
				return hasRejected();	
			default:
				break;
			}
		case FORMAL_ACCEPT:
			return hasAccepted();
		default:
			break;
		}
		return this;
	}
	
	private AcceptCounter hasAccepted() {
		accepted++;
		return this;
	}
	
	private AcceptCounter hasRejected() {
		accepted = 0; // reset
		return this;
	}
	
	public int getNextQuantity() {
		if (accepted > 3) {
			return 10;
		} else if (accepted > 1) {
			return 4;
		} else {
			return 2;
		}
	}
}
