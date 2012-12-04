import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main {

	/**
	 * @param args
	 */
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
			convertFile(fileName, "metamodel.als", "predicate.als");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void convertFile(String fileName, String metaModelFileName,
			String predicateFileName) throws IOException {

		Set<String> allActions = new HashSet<String>();
		Set<String> allSubjects = new HashSet<String>();
		Set<String> allResources = new HashSet<String>();		
		ArrayList<Policy> allPolicies = new ArrayList<Policy>();
		String policyCombiningAlgo = "";
		Set<String> failingRequests = new HashSet<String>();
		Set<String> conflictingRules = new HashSet<String>();
		
		ArrayList<String> lines = Utilities.readFileAsStringArray(fileName);
		
		int policyCount = 0;
		String currentHeader = "";
		
		for(int i = 0; i < lines.size(); i++)
		{
			String current = lines.get(i).trim();
			String[] components = current.split(",", -1);
			policyCount = components.length - 1;
			
			if (policyCount < 1)
				return;
			
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
		
		System.out.println("done");
		
	}
	
}
