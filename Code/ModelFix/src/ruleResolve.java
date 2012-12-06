import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ruleResolve {

	public ruleResolve(){}
	
    public static void writeStringToFile(String pathname, String text) throws IOException 
    {
    	PrintStream out = null;
	    try {
	        out = new PrintStream(new FileOutputStream(pathname));
	        out.print(text);
	    }
	    finally {
	        if (out != null) out.close();
	    }
    }
	
	public void resolve(BufferedReader reader, BufferedWriter writer) throws Exception{
        
		int lineCount=0;
        String line;
    	String newLine = System.getProperty("line.separator");

        // Loop through each line, stashing the line into our line variable.
    	Pattern r = Pattern.compile("\\bPolicy3_Rule_Professor_ReadModify_Marks_Deny extends\\b", Pattern.CASE_INSENSITIVE);
    	int check1=0;

    	int lineAt=0;
    	
    	
    	while ( (line = reader.readLine()) != null) {
    	    lineCount++;
    	    
    	    
    	    
/*    	    writer.write(line);
    	    writer.write(newLine);*/
    	    
    	    Matcher m = r.matcher(line);

    	    // indicate all matches on the line
    	    if(m.find()) {
    	        System.out.println("Word was found at position " + 
    	                       m.start() + " on line " + lineCount);
    	        check1=1;
    	        lineAt=lineCount;
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
    	    }

    	}
    	
	}
}
