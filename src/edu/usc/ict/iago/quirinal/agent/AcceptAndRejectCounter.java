package edu.usc.ict.iago.quirinal.agent;

import edu.usc.ict.iago.utils.Event;


public class AcceptAndRejectCounter {
	
	private int accepted;
	private int rejected;
	
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
	
	public AcceptAndRejectCounter update(Event event) {
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
	
	private AcceptAndRejectCounter hasAccepted() {
		accepted++;
		rejected = 0;
		return this;
	}
	
	private AcceptAndRejectCounter hasRejected() {
		rejected++;
		accepted = 0; // reset
		return this;
	}
	
	public Boolean IfExaggerateRejected() {
		return rejected > 1;
	}
	
	public void resetRejected() {
		rejected = 0;
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
