package fixer.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
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
    
    private static String readFile(String pathname) throws IOException {

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
    
    public static void main(String[] args) {
    	try {
//    		Runtime.getRuntime().exec("c:/");

    		List<String> cmdarray = new ArrayList<String>();
        	
    		String dir = "C:\\ClaferIE\\CS745\\Code\\ModelFixer\\bin\\";
    		
    		cmdarray.add(ExternalInterface.getJreExecutable().toString());
    		cmdarray.add("-jar");
    		cmdarray.add(dir + "AlloyIGEx.jar");    	

    		ExternalInterface pc = new ExternalInterface(cmdarray);
            String fileName = dir + "test.als";

            System.out.println("========== load");
            
//            Scanner sc = new Scanner(new File(fileName));
//            String contents;
            
            String contents = readFile(fileName);
            
            pc.execute("load"); 
            pc.execute(contents);

            pc.execute("unsatCoreMinimization"); 
            pc.execute("0");             

            pc.execute("resolve");
            pc.execute("next");
            
            System.out.println("UNSAT CORE:");
            pc.execute("unsatCore");

            pc.execute("quit");
            
//            System.out.println("========== Executing cd\\");
//            pc.execute("cd \\"); Thread.sleep(2000);
//            System.out.println("========== Executing dir");
//            pc.execute("dir"); Thread.sleep(2000);
//            System.out.println("========== Executing cd \\temp");
//            pc.execute("cd \\temp"); Thread.sleep(2000);
//            System.out.println("========== Executing dir");
//            pc.execute("dir"); Thread.sleep(2000);
//            System.out.println("========== Executing cd \\bubba");
//            pc.execute("cd \\bubba"); Thread.sleep(2000);

            System.out.println("========== Exiting .... bye.");
            pc.close();

    		System.out.println("OK");		

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

}	
