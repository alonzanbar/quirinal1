package edu.usc.ict.iago.views;

import java.util.HashMap;
import java.util.Map;

import edu.usc.ict.iago.utils.GameSpec;

class ResourceDistributiveGameSpec extends GameSpec{

	ResourceDistributiveGameSpec(boolean privileged) 
	{

		//VH preferences
		
		HashMap<String, Integer> simpleVHPoints = new HashMap<String, Integer>();
		HashMap<String, Integer> simplePlayerPoints = new HashMap<String, Integer>();
		
		simpleVHPoints.put(getIssuePluralNames()[0], 4);
		simpleVHPoints.put(getIssuePluralNames()[1], 3);
		simpleVHPoints.put(getIssuePluralNames()[2], 2);
		simpleVHPoints.put(getIssuePluralNames()[3], 1);
		
		simplePlayerPoints.put(getIssuePluralNames()[0], 4);
		simplePlayerPoints.put(getIssuePluralNames()[1], 3);
		simplePlayerPoints.put(getIssuePluralNames()[2], 2);
		simplePlayerPoints.put(getIssuePluralNames()[3], 1);
		
		//Privileged calls -- cannot be called outside package.
		setSimpleVHPoints(simpleVHPoints);
		setSimplePlayerPoints(simplePlayerPoints);	
		setPlayerBATNA(4);
		setVHBATNA(4);
		setIndexMenu(this.buildMenu());
		if(privileged)
			enablePrivilege();
		
	}
	
	@Override
	public Map<String, Map<String, String>> buildMenu()
	{
		Map<String, Map<String, String>> menuIndex= new HashMap<String, Map<String, String>>();
		HashMap<String, String> menuRoot= new HashMap<String, String>();
		HashMap<String, String> menuYouLike= new HashMap<String, String>();
		HashMap<String, String> menuILike= new HashMap<String, String>();
		HashMap<String, String> menuBecause= new HashMap<String, String>();
		
		menuRoot.put("butYouLike", "Ask your opponent's preferences >");
		menuRoot.put("butILike", "Say your own preferences >");
		menuRoot.put("butCustom1", "Justify your actions >");
		
		
		menuYouLike.put("craftingMessageString", "");
		menuYouLike.put("butItemsDiv", "");
		for (int i = 0; i < getNumIssues(); i++)
			menuYouLike.put("butItem" + i, "");
		menuYouLike.put("butItemsComparison", "");
		menuYouLike.put("butItemFirst","");
		menuYouLike.put("butItemComparison","");
		menuYouLike.put("butItemSecond","");
		
		menuYouLike.put("butConfirm", "<Confirm>");
		menuYouLike.put("butBack", "<Back>");	
		menuILike.put("craftingMessageString", "");
		menuILike.put("butItemsDiv", "");
		for (int i = 0; i < getNumIssues(); i++)
			menuILike.put("butItem" + i, "");
		menuILike.put("butItemsComparison", "");
		menuILike.put("butItemFirst","");
		menuILike.put("butItemComparison","");
		menuILike.put("butItemSecond","");
		menuILike.put("butConfirm", "<Confirm>");
		menuILike.put("butBack", "<Back>");
		
		menuBecause.put("messageBuffer", "");
		menuBecause.put("butExpl0",  "It is important that we are both happy with an agreement.");
		menuBecause.put("butExpl1",  "I gave a little here; you give a little next time.");
		menuBecause.put("butExpl2",  "We should try to split things evenly.");
		menuBecause.put("butExpl3",  "We should each get our most valuable item.");
		menuBecause.put("butExpl4",  "Accept this or there will be consequences.");
		menuBecause.put("butExpl5",  "This is the last offer.  Take it or leave it.");
		menuBecause.put("butExpl6",  "This is the very best offer possible.");
		menuBecause.put("butExpl7",  "Your offer sucks.");
		menuBecause.put("butExpl8",  "I can't go any lower than this.");
		menuBecause.put("butExpl9",  "We should try harder to find a deal that benefits us both.");
		menuBecause.put("butExpl10", "There's hardly any time left to negotiate!");
		menuBecause.put("butExpl11", "Why don't you make an offer?");
		menuBecause.put("butBack", "<Back>");
		
		menuIndex.put("root", menuRoot);
		menuIndex.put("youLike", menuYouLike);
		menuIndex.put("iLike", menuILike);
		menuIndex.put("custom1", menuBecause);
		
		return menuIndex;
	}

	@Override
	public int getNumIssues() {
		return 4;
	}

	@Override
	public int[] getIssueQuants() {
		return new int[] {5,5,5,5};
	}

	@Override
	public String[] getIssuePluralNames() {
		return new String[] {"bars of iron", "bars of gold", "shipments of bananas", "shipments of spices"};
	}

	@Override
	public String[] getIssueNames() {
		return new String[] {"bar of iron", "bar of gold", "shipment of bananas", "shipment of spice"};
	}
	
	
	@Override
	public int getTotalTime() {
		return 120;
	}
	
	@Override
	public String getTargetEmail()
	{
		return "mell@ict.usc.edu";
	}

	@Override
	public String getEndgameMessage() {
		return "<p>Thank you for participating.  Prepare for a new game...</p>";
	}

	@Override
	public String getStudyName() {
		return "DistributiveResource";
	}

}
