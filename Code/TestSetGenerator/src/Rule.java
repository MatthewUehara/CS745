import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Rule {
	public Set<String> subjects;
	public Set<String> actions;
	public Set<String> resources;
	public String effect;
	
	public Rule()
	{
		subjects = new HashSet<String>();
		actions = new HashSet<String>();
		resources = new HashSet<String>();		
	}
}
