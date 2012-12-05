import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Policy {

	public Set<Rule> rules;
	public String combiningAlgo;
	public String name;
	
	public Policy()
	{
		rules = new HashSet<Rule>();
	}

	public static String combineTitles(ArrayList<Policy> policies)
	{
		HashSet<String> titles = new HashSet<String>();
		for (Policy p: policies)
		{
			titles.add(p.name);
		}
		
		return Utilities.combineSet(titles, " + ", "");
	}

	
}
