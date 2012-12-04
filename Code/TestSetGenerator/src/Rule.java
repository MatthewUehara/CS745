import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Rule {
	public Set<String> subjects;
	public Set<String> actions;
	public Set<String> resources;
	public String effect;
	public String title;
	
	public Rule()
	{
		subjects = new HashSet<String>();
		actions = new HashSet<String>();
		resources = new HashSet<String>();		
	}
	
	public static String combineTitles(Set<Rule> rules)
	{
		HashSet<String> titles = new HashSet<String>();
		for (Rule r: rules)
		{
			titles.add(r.title);
		}
		
		return Utilities.combineSet(titles, " + ", "");
	}

	public String getTarget()
	{
		return this.title.replace("Rule_", "Target_");
	}
}
