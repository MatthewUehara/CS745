package alloyrunner.main;

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

    		String sInputFileName = sDirectory + "ModelTestA.als";
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

//            System.out.println("========== load");            
            String contents = Utilities.getFileContentsAsString(fileName); // laod all Alloy file contents to string
            
            pc.execute("load"); // pass load command
            pc.execute(contents); // pass Alloy file contents

            pc.execute("unsatCoreMinimization"); // set UNSAT core minimization to the best one
            pc.execute("0");             

            pc.execute("resolve"); // resolve - solves the model
            pc.execute("next"); // Ask to get next instance.
            
//            System.out.println("Getting UNSAT CORE...");
            
            pc.execute("unsatCore"); // get UNSAT core

//            System.out.println("UNSAT CORE HAS BEEN GOT...");                      

//            System.out.println("========== Exiting .... bye.");
            
            pc.close(); // closing the communicator
            
            
            /* Now we process all the result we got using Alloy API */
            
            ArrayList<String> sl = AlloyCommunicator.readLibraryOutputAsStringArray(sTempResultFileName); 
            // get all output of alloy commands as a string list. But we need to process the output at the same
            // order as we executed commands
            
            int pos = 0;
            int sigCount = Integer.parseInt(sl.get(pos++)); // this gets all SIG count.
            
//            System.out.println("Sig count: " + sigCount);
            
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
            

            
            String sXML = "<alloy></alloy>";
            
            if (sConsistent.compareTo("True") == 0) // if the model is consistent, then we got XML
            {
                sXML = sl.get(pos++);                                                
                pos++; // remaining misc stuff, I don't know, we don't need it                
            }
            else // model is inconsistent, and we have UNSAT cores
            {	            
	            int unsatCoreSize = Integer.parseInt(sl.get(pos++));
            }
            
            System.out.println(sXML); // GETTING INSTANCE XML!!!
            Utilities.writeStringToFile(sXMLResultFileName, sXML);
            
//	            System.out.println("Number of UNSAT cores: " + unsatCoreSize);   
//	            System.out.println("UNSAT CORE Markers (2 lines per core): ");
	            
	            // NOW Display the constaints:
/*	            
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
*/	      
            
    	} 
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    	}
    }

    
    public static void runModel(String alloyFileName, String libRoot) {
    	try {
    		
    		/* Preliminary settings. Probably need to pass them using command line  */
    		
    		String sDirectory = "";
    		
    		File file = new File(alloyFileName);
    		
    		if (!file.exists())
    		{
                System.out.println(String.format("File does not exists: '%s'", alloyFileName));
                return;
    		}

    		try
    		{
    			File fDirectory = file.getParentFile();
    			sDirectory = fDirectory.getAbsolutePath() + "/";
    		}
    		catch(Exception e)
    		{
    			sDirectory = "";
    		}
    		
    		String sInputFileName = alloyFileName;
    		String sTempResultFileName = sDirectory + "output_all.txt";
    		String sXMLResultFileName = sDirectory + "output_instance.xml";
    		
    		String sToolPath = "";
    		
    		if (libRoot.equals(""))
    		{
    			sToolPath = "";
    		}
    		else 
    		{
    			sToolPath = libRoot + "/";
    		}
    		
    		String sLibrary = "AlloyIGEx.jar";
    		
    		/* run java -jar AlloyIGEx.jar */ 
    		// This library uses Alloy API and gets all the necessary result
    		
    		PrintStream out = new PrintStream(new File(sTempResultFileName));    		

    		List<String> cmdarray = new ArrayList<String>();    		
    		cmdarray.add(AlloyCommunicator.getJreExecutable().toString());
    		cmdarray.add("-jar");
    		
    		sToolPath += sLibrary;
    		
    		cmdarray.add(sToolPath);    	

    		file = new File(sToolPath);
    		
    		if (!file.exists())
    		{
                System.out.println(String.format("Library is not found: '%s'", sToolPath));
                return;
    		}    	    		
    		
    		try{
	    		
	    		// pc - process communicator
	    		AlloyCommunicator pc = new AlloyCommunicator(cmdarray, out, System.out);
	    		// results of all commands will be passed to the out stream. Then we just need to process this stream
	    		
	    		//---------------------------------------------
	    		
	    		String fileName = sInputFileName;
	
	//            System.out.println("========== load");            
	            String contents = Utilities.getFileContentsAsString(fileName); // laod all Alloy file contents to string
	            
	            pc.execute("load"); // pass load command
	            pc.execute(contents); // pass Alloy file contents
	
	            pc.execute("unsatCoreMinimization"); // set UNSAT core minimization to the best one
	            pc.execute("0");             
	
	            pc.execute("resolve"); // resolve - solves the model
	            pc.execute("next"); // Ask to get next instance.
	            
	//            System.out.println("Getting UNSAT CORE...");
	            
	            pc.execute("unsatCore"); // get UNSAT core
	
	//            System.out.println("UNSAT CORE HAS BEEN GOT...");                      
	
	//            System.out.println("========== Exiting .... bye.");
	            
	            pc.close(); // closing the communicator
	            
	            
	            /* Now we process all the result we got using Alloy API */
	            
	            ArrayList<String> sl = AlloyCommunicator.readLibraryOutputAsStringArray(sTempResultFileName); 
	            // get all output of alloy commands as a string list. But we need to process the output at the same
	            // order as we executed commands
	            
	            int pos = 0;
	            int sigCount = Integer.parseInt(sl.get(pos++)); // this gets all SIG count.
	            
	//            System.out.println("Sig count: " + sigCount);
	            
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
	            
	
	            
	            String sXML = "<alloy></alloy>";
	            
	            if (sConsistent.compareTo("True") == 0) // if the model is consistent, then we got XML
	            {
	                sXML = sl.get(pos++);                                                
	                pos++; // remaining misc stuff, I don't know, we don't need it                
	            }
	            else // model is inconsistent, and we have UNSAT cores
	            {	            
		            int unsatCoreSize = Integer.parseInt(sl.get(pos++));
	            }
	            
	            System.out.println(sXML); // GETTING INSTANCE XML!!!
	            Utilities.writeStringToFile(sXMLResultFileName, sXML);
	            
    		}
    		catch(Exception e)
    		{
    			System.out.println("Error: " + e.toString());
    		}
            
    	} 
    	catch (Exception e) 
    	{
    		System.out.println("Error: " + e.toString());
    	}
    }
    
    
    
    public static void main(String[] args)  
    {
    	if (args.length < 2)
    	{
	    	System.out.println("You should specify two arguments: full alloy file path and tools directory");
	    	return;
    	}
    	
    	String alloyFileName = args[0];//"C:/ClaferIE/CS745/Code/Fixer/test.als";//args[0];
    	String libRoot =  args[1]; //"C:/ClaferIE/CS745/Code/Fixer"; //args[1];
    	
        runModel(alloyFileName, libRoot);
    }

}	
