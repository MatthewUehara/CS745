package safety.onlyoneapplicable;

import general.classes.Fix;
import general.classes.VerificationResult;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.*;
import java.util.Arrays;

import alloyrunner.main.Utilities;


public class Fixer {
	
	private String jarPath;
	
	public Fixer(String jarPath)
	{
		this.jarPath = jarPath;
	}
	
	private String invert(String originalEffect)
	{
		// inverts the effect (Permit -> Deny, Deny -> Permit)
		if (originalEffect.contains("Permit"))
			return originalEffect.replaceAll("Permit", "Deny");
		
		return originalEffect.replaceAll("Deny", "Permit");
	}
	
	
	public static void readFile(String input, int choice) throws Exception {
 
		
		int lineCount=0;

        // Loop through each line, stashing the line into our line variable.
		
		String inputPattern;
		
		if(choice==1){
			inputPattern="\\b"+input+" extends\\b"; //Policy3_Rule_Professor_ReadModify_Marks_Deny extends
		}
		else{
			inputPattern="\\b"+input+" extends\\b"; //Policy3 extends
			
		}
//		System.out.println(inputPattern+"\n");
		
    	Pattern r = Pattern.compile(inputPattern, Pattern.CASE_INSENSITIVE);
    	int check1=0;
    	int check2=0;
    	int lineAt=0;
    	
		// Location of file to read
		File file = new File("PolicySet_Temp.als");
 
		Scanner scanner = new Scanner(file);
		String newLine = System.getProperty("line.separator");
		// Location of file to output
	    Writer output = null;
	    File file1 = new File("ResolvedPolicySet_Temp.als");
	    output = new BufferedWriter(new FileWriter(file1,false));
	    
		while (scanner.hasNextLine()) {
    	    lineCount++;
			String line = scanner.nextLine();
			Matcher m = r.matcher(line);


    	    if(m.find()) {
        	    // Effect case
    	    	if(choice==1){
//        	        System.out.println("Effect was found at position " + 
// 	                       m.start() + " on line " + lineCount);
        	        check1=1;
        	        lineAt=lineCount;
    	    	}
        	    //Override case
    	    	if(choice==2){
//        	        System.out.println("Override was found at position " + 
// 	                       m.start() + " on line " + lineCount);
        	        check2=1;
        	        lineAt=lineCount; 	
    	    	}
    	    }


    	    if(check1==1 && (lineCount-lineAt)==2){
    	    	
    	    	//System.out.println(line);
    	    	lineAt=0;
    	    	String [] temp=line.split("=",-1);
//    	    	System.out.println(Arrays.toString(temp));
    	    	//System.out.println(temp[1]);
    	    	if(temp[1].equalsIgnoreCase(" Deny")){
    	    		temp[1]="Permit";//!!!!!!!!!!!!
    	    	}
    	    	else{
    	    		temp[1]="Deny";
    	    	}
//    	    	System.out.println("Updated temp: ");
//    	    	System.out.println(Arrays.toString(temp));
    	    	line=temp[0]+"= "+temp[1];
    	    }
    	    
    	    if(check2==1 && (lineCount-lineAt)==3){
    	    	
    	    	//System.out.println(line);
    	    	lineAt=0;
    	    	String [] temp=line.split("=",-1);
//    	    	System.out.println(Arrays.toString(temp));
    	    	//System.out.println(temp[1]);
    	    	if(temp[1].equalsIgnoreCase(" DenyOverrides")){
    	    		temp[1]="PermitOverrides";//!!!!!!!!!!!!
    	    	}
    	    	else{
    	    		temp[1]="DenyOverrides";
    	    	}
//    	    	System.out.println("Updated temp: ");
//    	    	System.out.println(Arrays.toString(temp));
    	    	line=temp[0]+"= "+temp[1];
    	    }	
			//System.out.println(line);
    	    
    	    
			//writeFile(line);
    	    output.write(line);
    	    output.write(newLine);
		}
		scanner.close();
		output.close();
//		System.out.println("File Copied"); 
	}
 
	

	private int getNumberOfFutureFixesRecursive(String inputFileContents, VerificationResult ver, String fixId, int depth)
	{
		
		try {
			String fixed = fix(inputFileContents, ver, fixId);			
			String tmpFileName = "temp_fixed_file.als";
			Utilities.writeStringToFile(tmpFileName, fixed);
			Verifier v = new Verifier(this.jarPath);
			VerificationResult newResult = v.verify(tmpFileName);
			if (newResult.isConsisent)
				return 0;
			
			int nextDepth = depth - 1;
			if (nextDepth == 0)
				return 1;
			
			int[] results = new int[4];
			
			results[0] = getNumberOfFutureFixesRecursive(fixed, newResult, "1", nextDepth);
			results[1] = getNumberOfFutureFixesRecursive(fixed, newResult, "2", nextDepth);
			results[2] = getNumberOfFutureFixesRecursive(fixed, newResult, "3", nextDepth);
			results[3] = getNumberOfFutureFixesRecursive(fixed, newResult, "4", nextDepth);
			
			Arrays.sort(results);
			
			return results[0] + 1;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 10000;
		}
				
	}
	
	
	private String estimateNumberOfFutureFixes(String inputFileContents, VerificationResult ver, String fixId)
	{		
		int depth = 2;
		
		int recResult = getNumberOfFutureFixesRecursive(inputFileContents, ver, fixId, depth);
		if (recResult == depth)
		{
			return ">=" + recResult;
		}
		else
			return recResult + "";
	}
	
	public ArrayList<Fix> propose(String inputFileContents, VerificationResult ver) {
		// returns the list of proposed fixes.
		
		ArrayList<Fix> result = new ArrayList<Fix>();
		
		Fix f;
		
		f = new Fix();
		f.id = "1";
		f.statement = String.format("In policy '%s', rule '%s', change the rule effect to '%s'", 
				ver.Policy1, ver.Rule1, invert(ver.Rule1Effect)
				);
		f.est = estimateNumberOfFutureFixes(inputFileContents, ver, "1");
		result.add(f);

		
		f = new Fix();
		f.id = "2";
		f.statement = String.format("In policy '%s', rule '%s', change the rule effect to '%s'", 
				ver.Policy2, ver.Rule2, invert(ver.Rule2Effect)
				);
		f.est = estimateNumberOfFutureFixes(inputFileContents, ver, "2");
		result.add(f);
		
		f = new Fix();
		f.id = "3";
		f.statement = String.format("In policy '%s' change the CombiningAlgo to '%s'", 
				ver.Policy1, invert(ver.Policy1Algo)
				);
		f.est = estimateNumberOfFutureFixes(inputFileContents, ver, "3");
		result.add(f);
		
		f = new Fix();
		f.id = "4";
		f.statement = String.format("In policy '%s' change the CombiningAlgo to '%s'", 
				ver.Policy2, invert(ver.Policy2Algo)
				);
		f.est = estimateNumberOfFutureFixes(inputFileContents, ver, "4");
		result.add(f);
		
		return result;
	}

	
	public String fix(String inputFileContents, VerificationResult ver, String fixId) throws Exception {
	// returns corrected file
		
		String s = inputFileContents;
		
		if (fixId.equals("1"))
			s = applyFix(s, ver.Rule1, 1);
		
		else if (fixId.equals("2"))
			s = applyFix(s, ver.Rule2, 1);
		
		else if (fixId.equals("3"))
			s = applyFix(s, ver.Policy1, 2);

		else if (fixId.equals("4"))
			s = applyFix(s, ver.Policy2, 2);
		
		return s;
	}

	private String applyFix(String source, String replacement, int repId) {
		// TODO Auto-generated method stub
		String s = ""; // replace rule effect
		
		try {
			Utilities.writeStringToFile("PolicySet_temp.als", source);			
			readFile(replacement, repId);			
			s = Utilities.getFileContentsAsString("ResolvedPolicySet_temp.als");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return s;
	}
	
	
}
