package edu.usc.ict.iago.agent;

import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.MessagePolicy;
import edu.usc.ict.iago.utils.Preference;

public abstract class IAGOCoreMessage implements MessagePolicy
{
	protected abstract String getEndOfTimeResponse();
	
	protected abstract String getSemiFairResponse();
	
	protected abstract String getContradictionResponse(String drop);
	
	protected abstract void setUtils(AgentUtilsExtension utils);
	
	protected static String prefToEnglish(Preference preference, GameSpec game)
	{
		String ans = "";
		if (preference.isQuery())
			ans += "Do you like ";
		else
			ans += "I like ";
		
		if (preference.getIssue1() >= 0)
			ans += game.getIssuePluralNames()[preference.getIssue1()] + " ";
		else
			ans += "something ";
		switch (preference.getRelation())
		{
			case GREATER_THAN:
				ans += "more than ";
				if (preference.getIssue2() >= 0)
					ans += game.getIssuePluralNames()[preference.getIssue2()];
				else
					ans += "something else.";
				break;
			case LESS_THAN:
				ans += "less than ";
				if (preference.getIssue2() >= 0)
					ans += game.getIssuePluralNames()[preference.getIssue2()];
				else
					ans += "something else.";
				break;
			case BEST:
				ans += "the best";
				break;
			case WORST:
				ans += "the least";
				break;
			case EQUAL:
				ans += "the same as ";
				ans += game.getIssuePluralNames()[preference.getIssue2()];
				break;
		}
		ans += preference.isQuery() ? "?" : ".";
		
		return ans;
	}
}
