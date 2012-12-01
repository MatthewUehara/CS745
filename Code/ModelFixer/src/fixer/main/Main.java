package fixer.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class Main {
	
	public static int MAX_BUF_SIZE = 40000; // CAREFUL!!! THE SIZE OF XML FILE MAY EXCEED THIS LIMIT
	
    public static int launch(List<String> cmdarray) throws IOException,
    		InterruptedException {
    	byte[] buffer = new byte[1024];

    	ProcessBuilder processBuilder = new ProcessBuilder(cmdarray);
    	processBuilder.redirectErrorStream(true);
    	Process process = processBuilder.start();
    	
    	InputStream in = process.getInputStream();
    	while (true) {
    		int r = in.read(buffer);
    		if (r <= 0) {
    			break;
    		}
    		System.out.write(buffer, 0, r);
    	}
    	return process.waitFor();
    }
/*    
    private static String readFile(String path) throws IOException {
    	  FileInputStream stream = new FileInputStream(new File(path));
    	  try {
    	    FileChannel fc = stream.getChannel();
    	    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    	    return Charset.defaultCharset().decode(bb).toString();
    	  }
    	  finally {
    	    stream.close();
    	  }
    	}    
*/
    
    private static String getFileContentsAsString(String pathname) throws IOException {

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

    private static ArrayList<String> readFileAsStringArray(String pathname) throws IOException {    
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

    private static ArrayList<String> readLibraryOutputAsStringArray(String pathname) throws IOException {

    	char[] buf = new char[MAX_BUF_SIZE];
    	ArrayList<String> result = new ArrayList<String>();
    	
    	try{
    		  // Open the file that is the first 
    		  // command line parameter
    		  FileInputStream fstream = new FileInputStream(pathname);
    		  // Get the object of DataInputStream
    		  DataInputStream in = new DataInputStream(fstream);
    		  BufferedReader br = new BufferedReader(new InputStreamReader(in), MAX_BUF_SIZE);
    		  String strLine;
    		  
    		  //Read File Line By Line
    		  while ((strLine = br.readLine()) != null)   {
    		  // Print the content on the console
    			  String slen = strLine;
    			  if (strLine.length() == 0)
    				  continue;
    			  
    			  int len = Integer.parseInt(slen);
    			  if (len == 0)
    			  {
    				  result.add("");   			  
    			  }
    			  else
    			  {
    				int read = br.read(buf, 0, len);
    			    System.out.println(read);
    				
    			  	String realData = String.copyValueOf(buf,  0, len);
    			  	result.add(realData);
    			  }
    		  }
    		  //Close the input stream
    		  in.close();
    		    }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }    	
    	
            return result;
    }        
    
    public static void main(String[] args) {
    	try {
//    		Runtime.getRuntime().exec("c:/");
    		
    		List<String> cmdarray = new ArrayList<String>();
        	
    		String dir = "C:\\ClaferIE\\CS745\\Code\\ModelFixer\\bin\\";
    		String tempResultFileName = "result.txt";
    		
    		cmdarray.add(ExternalInterface.getJreExecutable().toString());
    		cmdarray.add("-jar");
    		cmdarray.add(dir + "AlloyIGEx.jar");    	

    		PrintStream out = new PrintStream(new File(tempResultFileName));
    		
    		ExternalInterface pc = new ExternalInterface(cmdarray, out, System.out);
            String fileName = dir + "test.als";

            System.out.println("========== load");
            
//            Scanner sc = new Scanner(new File(fileName));
//            String contents;
            
            String contents = getFileContentsAsString(fileName);
            
            pc.execute("load"); 
            pc.execute(contents);

            pc.execute("unsatCoreMinimization"); 
            pc.execute("0");             

            pc.execute("resolve");
            pc.execute("next");
            
            System.out.println("Getting UNSAT CORE...");
            pc.execute("unsatCore");

            System.out.println("UNSAT CORE HAS BEEN GOT...");                      

            System.out.println("========== Exiting .... bye.");
            
            pc.close();
            
            ArrayList<String> sl = readLibraryOutputAsStringArray(tempResultFileName);
            
            int pos = 0;
            int sigCount = Integer.parseInt(sl.get(pos++));
            
            System.out.println("Sig count: " + sigCount);
            
            // just skip first records;
            
            for (int i = 0; i < sigCount; i++)
            {
                pos++; //sig.label);
                pos++; //writeMessage(multiplicity(sig));
                pos++; //writeMessage(sig instanceof Sig.PrimSig ? "" : removeCurly(sig.type().toString()));
                String sScope = sl.get(pos++);
                if (sScope.compareTo("True") == 0) 
                	pos++;                
            }

            int commandCount = Integer.parseInt(sl.get(pos++));
            
            String sConsistent = sl.get(pos++);
            if (sConsistent.compareTo("True") == 0) // if the model is consistent, return
            {
                System.out.println("Intance found...");
                String sXML = sl.get(pos++);                
                System.out.println(sXML); // GETTING INSTANCE XML!!!
                                
                pos++; // misc stuff, I don't know, we don't need
                
            }
            else // printing UNSAT CORE
            {
            
	            boolean bConsistent = false;
	            
	            int unsatCoreSize = Integer.parseInt(sl.get(pos++));
	            System.out.println("Number of UNSAT cores: " + unsatCoreSize);
	            
	            System.out.println("UNSAT CORE Markers (2 lines per core): ");
	            
	            // NOW Display the constaints:
	            
	            ArrayList<String> alsFile = readFileAsStringArray(fileName);
	            
	            
	            for (int i = 0; i < unsatCoreSize; i++)
	            {
	            	int y1 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int x1 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int y2 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int x2 = Integer.parseInt(sl.get(pos++));
	            	
	            	String constraint = "";
	                String lineSeparator = System.getProperty("line.separator");
	                
	            	if (y2 > y1) // single lines
	            	{
	            		constraint = alsFile.get(y1).substring(x1);
	            		for (int j = y1 + 1; j < y2; j++)
	            		{
	            			constraint += alsFile.get(j);
	            		}
	            		
	            		constraint += alsFile.get(y2).substring(0, x2);
	            	}
	            	else // y2 == y1
	            	{
	            		constraint = alsFile.get(y1).substring(x1, x2);            		
	            	}
	                                
	            	System.out.println(y1 + " " + x1 + " " + y2 + " " + x2); // line char line char            
	            	System.out.println(constraint);            
	            }
	      
	    		System.out.println("OK");		
            }
            
    	} 
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}
    }

}	
