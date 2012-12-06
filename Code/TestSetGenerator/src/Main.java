import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
/*
	public static void main(String [] args) throws IOException{
		System.out.println("===========This is a test for string comparision============\n");
		long startTime = System.currentTimeMillis();		
		
		//Rule_Subject1Subject2_Action1Action2_Resource1Resource2
		String s1="Rule_Student_Read_Marks_Permit";
		String s2="Rule_ProfessorStudent_ReadWrite_Marks_Permit";
		String s3="Rule_Professor_Modify_Marks_Permit";

		HashSet<String> set = new HashSet<String>();
        set.add(s1);
        set.add(s2);
        set.add(s3);
        Iterator it = set.iterator();
        System.out.println("Original HashSet");
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("\n");
		
        RuleCompare a= new RuleCompare();
		int result=a.compare(s1,s2);
		
		switch (result){
		case 0: System.out.println("Recommendation: No deletion\n");
			break;
		case 1: System.out.println("Recommendation: Delete First argument\n");
			break;
		case 2: System.out.println("Recommendation: Delete Second Argument\n");
			break;
		default: System.out.println("Error: Rule component is inconcsistant, please check the rule format\n");
			break;
		}
        
		ChooseToCompare c= new ChooseToCompare();
		c.Choose();
		
		
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("\nTime Elapsed: "+elapsedTime+" miliseconds");
	}
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
			result = convertFile(fileName, "metamodel.als", "", false);
			System.out.println(result);
			Utilities.writeStringToFile(fileName.replaceAll(".csv", ".als"), result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String convertFile(String fileName, String prefixFileName,
			String postfixFileName, boolean allowCrossReferencingRules) throws IOException {

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
		
		System.out.println("Extraction done. Now converting...");
		
		

		String result = getAlsModel(allowCrossReferencingRules);
		
		String metamodel = Utilities.getFileContentsAsString(prefixFileName);
		
		
		String predicate = "";
		if (!postfixFileName.isEmpty())
			predicate = Utilities.getFileContentsAsString(postfixFileName);
		
		result = metamodel + result + predicate;
		
		return result;
	}

	private static String getAlsModel(boolean crossReferencingRules) throws IOException {

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
		
		//Interferencing the original structure for testing
		//!for (Policy p: allPolicies)
		FileWriter fstream = new FileWriter("OutPut.txt");
		BufferedWriter fout = new BufferedWriter(fstream);
		for (int i= 0; i<allPolicies.size();i++)
		{
			//!
			Policy p= allPolicies.get(i);
			//
			
			if (!crossReferencingRules)
			{
				prefix = p.name + "_";
			}
			
//////
			
			//===start of comparision
			
	        //System.out.println("Original HashSet");
	        
	        ArrayList<String> myRuleTitles = new ArrayList<String>();
	        ArrayList<String> redundantRule = new ArrayList<String>();
	        Iterator<Rule>it = p.rules.iterator();
	        while (it.hasNext()) {
	        	myRuleTitles.add(it.next().title);
	            //System.out.println(it.next().title);
	        }
	        
	        Iterator it1 = myRuleTitles.iterator();
	        //System.out.println("===Stored Rule HashSet===");
	        fout.write("===Stored Rule HashSet===\n");

	        /*
	        while (it1.hasNext()) {
	            System.out.println(it1.next());		            
	        }
	        */		        
	        
	        //System.out.println("\nTesting\n"); pass!
	        for(int index1= 0; index1<myRuleTitles.size();index1++){
        		fout.write(myRuleTitles.get(index1)+"\n");
	        	String s1= myRuleTitles.get(index1);		  
	        	//System.out.println("In S1 \n"+s1+"\n");
	        	for(int index2=0; index2<myRuleTitles.size();index2++){
	        		String s2= myRuleTitles.get(index2);
	        		if(index1!=index2){
		        		//System.out.println("In S2 \n"+s2+"\n");
	        			RuleCompare c= new RuleCompare();
	        			int res =c.compare(s1, s2);
	        			//System.out.println(res+"\n");
	        			
	        			if (res==1){
	        				redundantRule.add(myRuleTitles.get(index1));
	        				myRuleTitles.remove(index1);
	        				//System.out.println("Removing "+s1);
	        			}
	        		}		        		
	        	}		        	
	        }
	        //System.out.println("\nThe Updated Rules");
	        //System.out.println(myRuleTitles.toString());
	        System.out.println("\nRemoved Rules");
	        System.out.println(redundantRule.toString());
	        
	       // ArrayList<String>myRuleTitles;
	        /*
	        //updating rules
	        System.out.println("Updated Rules: \n"+myRuleTitles.toString()+"\n");
	        p.rules.clear();
	        for (int l1= 0; l1<myRuleTitles.size();l1++){
		        Rule r= new Rule();
		        r.title=myRuleTitles.get(l1);
	        	p.rules.add(r);		        	
	        }
			*/
	        
//	        //testing
//	    	System.out.println("\nOutputting Original Rules in P\n");
//	        Iterator<Rule>itNew = p.rules.iterator();
//	        while(itNew.hasNext()){
//	        	System.out.println(itNew.next().title);
//	        	
//	        }
	        System.out.println("Filtered Rules:");
			HashSet<Rule> finalRules = new HashSet<Rule>();
	        
	        for (Rule r: p.rules)
			{	
				
				int check=0;
				for (String sNew : redundantRule){
					if(r.title.equalsIgnoreCase(sNew)){
						check =1; //this check tells person to not edit it in Alloy
					}
				}
				if(check==1){continue;}
				
				finalRules.add(r);
			}
			
			//////
			
			result += String.format("one sig %s extends Policy {}{\n" + 
				  "policyTarget = T0\n" +
				  "rules = %s\n" +
				  "combiningAlgo = %s\n}\n\n", p.name, Rule.combineTitles(finalRules, prefix), p.combiningAlgo);
			
			if (crossReferencingRules)
			{			
				for (Rule r: finalRules)
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
				for (Rule r: finalRules)
				{	
					
					//testing if the check is successful->pass
					System.out.println(r.title);
					
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
		fout.close();
		
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
