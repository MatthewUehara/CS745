package fixer.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


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
 
 
public class ExternalInterface {
 
    private ProcessBuilder pb;
    Process p;
    boolean closed = false;
    PrintWriter writer;

    Thread outThread;
    Thread errThread;    
    
//    public PrintStream inputStreamFromProcess;
//    public PrintStream errorStreamFromProcess;
    
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

     
    ExternalInterface(List<String> commandList, PrintStream inputStreamFromProcess, PrintStream errorStreamFromProcess) {
//    	String[] cmdArray = (String[])commandList.toArray();
        pb = new ProcessBuilder(commandList);
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot execute PowerShell.exe", ex);
        }
        writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(p.getOutputStream())), true);
//        this.inputStreamFromProcess = p.getInputStream();
//        this.errorStreamFromProcess = p.getErrorStream();
        
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
