import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main {

	/**
	 * @param args
	 */
	
	public static Set<String> allActions = new HashSet<String>();
	public static Set<String> allSubjects = new HashSet<String>();
	public static Set<String> allResources = new HashSet<String>();		
	public static ArrayList<Policy> allPolicies = new ArrayList<Policy>();
	public static String policyCombiningAlgo = "";
	public static Set<String> failingRequests = new HashSet<String>();
	public static Set<String> conflictingRules = new HashSet<String>();
	
	
	
	
	public static void main(String[] args) {
		
		String fileName = "";
		
		if (args.length >= 1)
		{
			String str = args[0];			
			String ext = str.substring(str.lastIndexOf('.'));
			if (ext.equals(".csv")) // we have CSV file indeed
				fileName = args[0];
		}
		
		if (fileName == "")
		{
			System.out.println("No File is specified");
		}
		
		try {
			String result;
			result = convertFile(fileName, "metamodel.als", "predicate.als", false);
			System.out.println(result);
			Utilities.writeStringToFile(fileName.replaceAll(".csv", ".als"), result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String convertFile(String fileName, String metaModelFileName,
			String predicateFileName, boolean allowCrossReferencingRules) throws IOException {

		ArrayList<String> lines = Utilities.readFileAsStringArray(fileName);
		
		int policyCount = 0;
		String currentHeader = "";
		
		for(int i = 0; i < lines.size(); i++)
		{
			String current = lines.get(i).trim();
			String[] components = current.split(",", -1);
			policyCount = components.length - 1;
			
			if (policyCount < 1)
			{
				System.out.println("Policy count < 1");
				return "";
			}
			
			if (allPolicies.size() == 0)
			{
				for (int j = 0; j < policyCount; j++)
				{
					allPolicies.add(new Policy());
				}
			}
			
			if (!components[0].equals(""))
				currentHeader = components[0];

			for (int j = 0; j < components.length - 1; j++)
			{
				String component = components[j + 1].trim().replaceAll("\\s","");
				if (component.length() == 0)
					continue;
				
				if (currentHeader.equals("Policies"))
				{
					allPolicies.get(j).name = component;
					continue;
				}
				
				if (currentHeader.equals("Rule Combining Algorithm"))
				{
					allPolicies.get(j).combiningAlgo = component;
					continue;
				}
				
				if (currentHeader.equals("Rules"))
				{
					Rule rule = new Rule(); 
					String[] ruleParts = component.split("_", -1); // split the rule					
					if (ruleParts.length < 5)
					{
						System.out.println("Problem with rule parts: " + ruleParts.length);
						continue;
					}
					
					rule.title = component;
					rule.subjects = Utilities.createSet(Utilities.splitByCaps(ruleParts[1]));
					allSubjects.addAll(rule.subjects);

					rule.actions = Utilities.createSet(Utilities.splitByCaps(ruleParts[2]));
					allActions.addAll(rule.actions);
					
					rule.resources = Utilities.createSet(Utilities.splitByCaps(ruleParts[3]));
					allResources.addAll(rule.resources);

					rule.effect = ruleParts[4];

					allPolicies.get(j).rules.add(rule);
					continue;
				}

				if (j > 1)
					break;
				
				if (currentHeader.equals("Policy Combining Algorithm"))
				{
					policyCombiningAlgo = "P_" + component;
				}

				if (currentHeader.equals("Failing Requests"))
				{
					failingRequests.add(component);
				}
				
				if (currentHeader.equals("Conflicting Rules"))
				{
					conflictingRules.add(component);
				}
			}
		}
		
		System.out.println("Extraction done. Now conveting...");

		String result = getAlsModel(allowCrossReferencingRules);
		
		String metamodel = Utilities.getFileContentsAsString(metaModelFileName);
		String predicate = Utilities.getFileContentsAsString(predicateFileName);
		
		result = metamodel + result + predicate;
		
		return result;
	}

	private static String getAlsModel(boolean crossReferencingRules) {

		String result = "\n\n";
		
		String subjects = Utilities.combineSet(allSubjects, ", ", "");
		String resources = Utilities.combineSet(allResources, ", ", "");
		String actions = Utilities.combineSet(allActions, ", ", "");
		
		result += String.format("one sig %s extends Value {}\n", subjects);
		result += String.format("one sig %s extends Value {}\n", resources);
		result += String.format("one sig %s extends Value {}\n", actions);

		result += "fact{ \n values = \n";
		
		HashSet<String> tuples = new HashSet<String>();
		

		// general sigs
		
		for (String s: allActions)
		{
			tuples.add(String.format("(%s -> %s)", "ActionName", s));
		}
		
		for (String s: allSubjects)
		{
			tuples.add(String.format("(%s -> %s)", "Role", s));
		}

		for (String s: allResources)
		{
			tuples.add(String.format("(%s -> %s)", "ResourceName", s));
		}
		
		result += Utilities.combineSet(tuples, "\n +", "") + "}\n\n";

		// actions

		for (String s: allSubjects)
		{
			result += String.format("one sig S%s extends Subject{}{\n attributes = Role -> %s \n}\n\n", s, s);
		}
		
		for (String s: allResources)
		{
			result += String.format("one sig R%s extends Resource{}{\n attributes = ResourceName -> %s \n}\n\n", s, s);
		}

		for (String s: allActions)
		{
			result += String.format("one sig A%s extends Action{}{\n attributes = ActionName -> %s \n}\n\n", s, s);
		}
		
		result += String.format("one sig T0 extends Target {}{\n subjects = %s \n resources = %s \n actions = %s }\n\n", 

				Utilities.combineSet(allSubjects, " + ", "S"), Utilities.combineSet(allResources, " + ", "R"), Utilities.combineSet(allActions, " + ", "A")
		);

		HashSet<Rule> allRules = new HashSet<Rule>();
		HashSet<String> allRulesTitles = new HashSet<String>();
		
		String prefix = "";
		
		for (Policy p: allPolicies)
		{
			if (!crossReferencingRules)
			{
				prefix = p.name + "_";
			}
			
			result += String.format("one sig %s extends Policy {}{\n" + 
				  "policyTarget = T0\n" +
				  "rules = %s\n" +
				  "combiningAlgo = %s\n}\n\n", p.name, Rule.combineTitles(p.rules, prefix), p.combiningAlgo);
			
			if (crossReferencingRules)
			{			
				for (Rule r: p.rules)
				{
					if (!allRulesTitles.contains(r.title))
					{
						allRules.add(r);
						allRulesTitles.add(r.title);
					}
				}
			}
			else
			{
				for (Rule r: p.rules)
				{		
					result += String.format("one sig %s extends Rule {}{\n" + 
					"ruleTarget = %s\n" +
					"ruleEffect = %s\n}\n\n", prefix + r.title, r.getTarget(prefix), r.effect);
					
					
					result += String.format("one sig %s extends Target {}{\n" + 
					"subjects = %s\n" +
					"resources = %s\n" +
					"actions = %s\n}\n\n", r.getTarget(prefix), Utilities.combineSet(r.subjects, " + ", "S"), Utilities.combineSet(r.resources, " + ", "R"), Utilities.combineSet(r.actions, " + ", "A"));
		
				}		
				
			}
		}
		
		if (crossReferencingRules)
		{
			for (Rule r: allRules)
			{		
				result += String.format("one sig %s extends Rule {}{\n" + 
				"ruleTarget = %s\n" +
				"ruleEffect = %s\n}\n\n", r.title, r.getTarget(""), r.effect);
				
				
				result += String.format("one sig %s extends Target {}{\n" + 
				"subjects = %s\n" +
				"resources = %s\n" +
				"actions = %s\n}\n\n", r.getTarget(""), Utilities.combineSet(r.subjects, " + ", "S"), Utilities.combineSet(r.resources, " + ", "R"), Utilities.combineSet(r.actions, " + ", "A"));
	
			}		
		}

		// policy set
		result += String.format(
		"one sig PS extends PolicySet{}{\n" +
		  "policySetTarget = T0\n" +
		  "combiningAlgo = %s\n" +
		  "policies = %s\n}\n\n", policyCombiningAlgo, Policy.combineTitles(allPolicies));
		
		return result;
	}
	
}
