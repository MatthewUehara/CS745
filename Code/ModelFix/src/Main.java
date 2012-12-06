import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class Main {
	
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
		System.out.println(inputPattern+"\n");
		
    	Pattern r = Pattern.compile(inputPattern, Pattern.CASE_INSENSITIVE);
    	int check1=0;
    	int check2=0;
    	int lineAt=0;
    	
		// Location of file to read
		File file = new File("PolicySetB.als");
 
		Scanner scanner = new Scanner(file);
		String newLine = System.getProperty("line.separator");
		// Location of file to output
	    Writer output = null;
	    File file1 = new File("ResolvedPolicySetB.als");
	    output = new BufferedWriter(new FileWriter(file1,false));
	    
		while (scanner.hasNextLine()) {
    	    lineCount++;
			String line = scanner.nextLine();
			Matcher m = r.matcher(line);


    	    if(m.find()) {
        	    // Effect case
    	    	if(choice==1){
        	        System.out.println("Effect was found at position " + 
 	                       m.start() + " on line " + lineCount);
        	        check1=1;
        	        lineAt=lineCount;
    	    	}
        	    //Override case
    	    	if(choice==2){
        	        System.out.println("Override was found at position " + 
 	                       m.start() + " on line " + lineCount);
        	        check2=1;
        	        lineAt=lineCount; 	
    	    	}
    	    }


    	    if(check1==1 && (lineCount-lineAt)==2){
    	    	
    	    	//System.out.println(line);
    	    	lineAt=0;
    	    	String [] temp=line.split("=",-1);
    	    	System.out.println(Arrays.toString(temp));
    	    	//System.out.println(temp[1]);
    	    	if(temp[1].equalsIgnoreCase(" Deny")){
    	    		temp[1]="Permit";//!!!!!!!!!!!!
    	    	}
    	    	else{
    	    		temp[1]="Deny";
    	    	}
    	    	System.out.println("Updated temp: ");
    	    	System.out.println(Arrays.toString(temp));
    	    	line=temp[0]+"= "+temp[1];
    	    }
    	    
    	    if(check2==1 && (lineCount-lineAt)==3){
    	    	
    	    	//System.out.println(line);
    	    	lineAt=0;
    	    	String [] temp=line.split("=",-1);
    	    	System.out.println(Arrays.toString(temp));
    	    	//System.out.println(temp[1]);
    	    	if(temp[1].equalsIgnoreCase(" DenyOverrides")){
    	    		temp[1]="PermitOverrides";//!!!!!!!!!!!!
    	    	}
    	    	else{
    	    		temp[1]="DenyOverrides";
    	    	}
    	    	System.out.println("Updated temp: ");
    	    	System.out.println(Arrays.toString(temp));
    	    	line=temp[0]+"= "+temp[1];
    	    }	
			//System.out.println(line);
    	    
    	    
			//writeFile(line);
    	    output.write(line);
    	    output.write(newLine);
		}
		scanner.close();
		output.close();
		System.out.println("File Copied"); 
	}
 

	
	public static void main(String[] args) throws Exception {	
		
		//String userInput1="Policy2_Rule_Professor_ReadModify_Marks_Deny"; //effect
		String userInput1="Policy1_Rule_Professor_ReadModify_Marks_Permit";
		//String userInput2="Policy1"; //override
		String userInput2="Policy2";
		int flag=0; //1 for effct and 2 for override....
		readFile(userInput2	,2);

	}
 
 
}