package fixer.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

public class Main {
	    
    public static void apiTest() {
    	try {
    		
    		/* Preliminary settings. Probably need to pass them using command line  */
    		
    		String sDirectory = System.getProperty("user.dir") + "/";

    		String sInputFileName = sDirectory + "ModelTest1.als";
    		String sTempResultFileName = sDirectory + "output_all.txt";
    		String sXMLResultFileName = sDirectory + "output_instance.xml";
    		String sToolPath = sDirectory + "/tools/";
    		String sLibrary = "AlloyIGEx.jar";
    		
    		/* run java -jar AlloyIGEx.jar */ 
    		// This library uses Alloy API and gets all the necessary result
    		
    		PrintStream out = new PrintStream(new File(sTempResultFileName));    		

    		List<String> cmdarray = new ArrayList<String>();    		
    		cmdarray.add(AlloyCommunicator.getJreExecutable().toString());
    		cmdarray.add("-jar");
    		cmdarray.add(sToolPath + sLibrary);    	

    		// pc - process communicator
    		AlloyCommunicator pc = new AlloyCommunicator(cmdarray, out, System.out);
    		// results of all commands will be passed to the out stream. Then we just need to process this stream
    		
    		//---------------------------------------------
    		
    		String fileName = sInputFileName;

            System.out.println("========== load");            
            String contents = Utilities.getFileContentsAsString(fileName); // laod all Alloy file contents to string
            
            pc.execute("load"); // pass load command
            pc.execute(contents); // pass Alloy file contents

            pc.execute("unsatCoreMinimization"); // set UNSAT core minimization to the best one
            pc.execute("0");             

            pc.execute("resolve"); // resolve - solves the model
            pc.execute("next"); // Ask to get next instance.
            
            System.out.println("Getting UNSAT CORE...");
            
            pc.execute("unsatCore"); // get UNSAT core

            System.out.println("UNSAT CORE HAS BEEN GOT...");                      

            System.out.println("========== Exiting .... bye.");
            
            pc.close(); // closing the communicator
            
            
            /* Now we process all the result we got using Alloy API */
            
            ArrayList<String> sl = AlloyCommunicator.readLibraryOutputAsStringArray(sTempResultFileName); 
            // get all output of alloy commands as a string list. But we need to process the output at the same
            // order as we executed commands
            
            int pos = 0;
            int sigCount = Integer.parseInt(sl.get(pos++)); // this gets all SIG count.
            
            System.out.println("Sig count: " + sigCount);
            
            // just skip first records;
            
            for (int i = 0; i < sigCount; i++) // we can process all sigs, but we don't need them. So we mostly skip them
            {
                pos++; //sig.label);
                pos++; //writeMessage(multiplicity(sig));
                pos++; //writeMessage(sig instanceof Sig.PrimSig ? "" : removeCurly(sig.type().toString()));
                String sScope = sl.get(pos++);
                if (sScope.compareTo("True") == 0) 
                	pos++;                
            }

            String s = sl.get(pos++);
            int commandCount = Integer.parseInt(s); // get the number of commands (e.g. "run InconsistentPolicy")
            
            String sConsistent = sl.get(pos++); // this is important. If here is TRUE, then instance was generated.
            
            if (sConsistent.compareTo("True") == 0) // if the model is consistent, then we got XML
            {
                System.out.println("Intance found...");
                String sXML = sl.get(pos++);                
                System.out.println(sXML); // GETTING INSTANCE XML!!!
                Utilities.writeStringToFile(sXMLResultFileName, sXML);
                                
                pos++; // remaining misc stuff, I don't know, we don't need it
                
            }
            else // model is inconsistent, and we have UNSAT cores
            {	            
            	Utilities.writeStringToFile(sXMLResultFileName, ""); // empty XML
            	
	            int unsatCoreSize = Integer.parseInt(sl.get(pos++));
	            System.out.println("Number of UNSAT cores: " + unsatCoreSize);   
	            System.out.println("UNSAT CORE Markers (2 lines per core): ");
	            
	            // NOW Display the constaints:
	            
	            ArrayList<String> alsFile = Utilities.readFileAsStringArray(fileName);
	            
	            
	            for (int i = 0; i < unsatCoreSize; i++)
	            {
	            	int y1 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int x1 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int y2 = Integer.parseInt(sl.get(pos++)) - 1;
	            	int x2 = Integer.parseInt(sl.get(pos++));
	            	
	            	String constraint = "";
//	                String lineSeparator = System.getProperty("line.separator");
	                
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
	      
	    		System.out.println("Finished printung UNSAT core");		
            }
            
    	} 
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}
    }
    
    public static void main(String[] args)  
    {
    	apiTest();
    }

}	
