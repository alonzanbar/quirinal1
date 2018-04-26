package edu.usc.ict.iago.agent;



import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;

public class IAGODefaultBehavior implements BehaviorPolicy {
	
	private Offer[] offers = new Offer[2];
	public IAGODefaultBehavior()
	{
		Offer o = new Offer(4);
		o.setItem(0, new int[] {5, 0, 0});
		o.setItem(1, new int[] {0, 5, 0});
		o.setItem(2, new int[] {0, 2, 3});
		o.setItem(3, new int[] {0, 5, 0});
		offers[0] = o;
		Offer o2 = new Offer(4);
		o2.setItem(0, new int[] {5, 0, 0});
		o2.setItem(1, new int[] {5, 0, 0});
		o2.setItem(2, new int[] {3, 0, 2});
		o2.setItem(3, new int[] {0, 0, 5});
		offers[1] = o2;
	}

	@Override
	public Offer getNextOffer(History history) {
		return offers[(int)(Math.random()*2)];
	}

}
