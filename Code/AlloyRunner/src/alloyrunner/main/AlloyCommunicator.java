package alloyrunner.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* Communicates with AlloyIGEx.jar */
public class AlloyCommunicator 
{
 
    private ProcessBuilder pb;
    Process p;
    boolean closed = false;
    PrintWriter writer;

    Thread outThread;
    Thread errThread;    
    


	/* Processes the library output file and loads it into a String List*/
	public static int MAX_BUF_SIZE = 40000; // CAREFUL!!! THE SIZE OF XML FILE MAY EXCEED THIS LIMIT    
    public static ArrayList<String> readLibraryOutputAsStringArray(String pathname) throws IOException {

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
    
    private static boolean isWindows() {
    	String os = System.getProperty("os.name");
    	if (os == null) {
    		throw new IllegalStateException("os.name");
    	}
    	os = os.toLowerCase();
    	return os.startsWith("windows");
    }

    public static File getJreExecutable() throws FileNotFoundException {
    	String jreDirectory = System.getProperty("java.home");
    	if (jreDirectory == null) {
    		throw new IllegalStateException("java.home");
    	}
    	File exe;
    	if (isWindows()) {
    		exe = new File(jreDirectory, "bin/java.exe");
    	} else {
    		exe = new File(jreDirectory, "bin/java");
    	}
    	if (!exe.isFile()) {
    		throw new FileNotFoundException(exe.toString());
    	}
    	return exe;
    }

     
    AlloyCommunicator(List<String> commandList, PrintStream inputStreamFromProcess, PrintStream errorStreamFromProcess) {
        pb = new ProcessBuilder(commandList);
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot execute PowerShell.exe", ex);
        }
        writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(p.getOutputStream())), true);
        
        Gobbler outGobbler = new Gobbler(p.getInputStream(), inputStreamFromProcess);
        Gobbler errGobbler = new Gobbler(p.getErrorStream(), errorStreamFromProcess);
        outThread = new Thread(outGobbler);
        errThread = new Thread(errGobbler);
        outThread.start();
        errThread.start();
    }
     
    public void execute(String command) {
        if (!closed) {
            writer.println(command.length());
            writer.write(command);
            writer.flush();
        } else {
            throw new IllegalStateException("Power console has ben closed.");
        }
    }
 
    public void close() {
        try {
            execute("quit");
            p.waitFor();
            outThread.join();
            errThread.join();
        } catch (InterruptedException ex) {
        }
    }
 }

class Gobbler implements Runnable {
	 
    private PrintStream out;
    private String message;
 
    private BufferedReader reader;
 
    public Gobbler(InputStream inputStream, PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
               this.out = out;
        this.message = ( null != message ) ? message : "";
    }
 
    public void run() {
        String line;
 
        try {
            while (null != (line = this.reader.readLine())) {
                out.println(message + line);
            }
            this.reader.close();
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}

