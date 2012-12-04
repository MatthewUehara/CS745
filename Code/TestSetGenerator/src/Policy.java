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

	
}
