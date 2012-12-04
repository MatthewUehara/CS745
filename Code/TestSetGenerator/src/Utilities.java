

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Utilities {

    
	/* load the whole TEXT file as a String Array */
    public static ArrayList<String> readFileAsStringArray(String pathname) throws IOException {    
  	  ArrayList<String> result = new ArrayList<String>();

  	  try{
    	  
    	  // Open the file that is the first 
    	  // command line parameter
    	  FileInputStream fstream = new FileInputStream(pathname);
    	  // Get the object of DataInputStream
    	  DataInputStream in = new DataInputStream(fstream);
    	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	  String strLine;
    	  //Read File Line By Line
    	  while ((strLine = br.readLine()) != null)   {
    	  // Print the content on the console
    		  result.add (strLine);
    	  }
    	  //Close the input stream
    	  in.close();
    	}
	    catch (Exception e){//Catch exception if any
	    	  
	    	System.err.println("Error: " + e.getMessage());
	    }

    	return result;

	}
    
	/* load the whole file as a single string */
    public static String getFileContentsAsString(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }    
    
	/* write the whole text to file */
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
    

	public static String[] splitByCaps(String str)
	{
        String res = "";
        for(int i = 0; i < str.length(); i++) {
                Character ch = str.charAt(i);
                if(Character.isUpperCase(ch))
                        res += " " + ch;
                else
                        res += ch;
        }
        
        return res.trim().split(" ", -1);
	}

	public static Set<String> createSet(String[] words) {
		HashSet<String> result = new HashSet<String>();
		for (int i = 0; i < words.length; i++)
		{
			result.add(words[i]);
		}
		
		return result;
	}

	
	public static String combineSet(Set<String> set, String sep, String prefix) {
		String result = "";
		
		for(String s : set)
		{
		    result += sep + prefix + s;
		}
		
		if (result.length() > 0)
			result = result.substring(sep.length());
		
		return result;
	}
	

}
