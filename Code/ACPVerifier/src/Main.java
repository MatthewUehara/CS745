import java.io.IOException;
import java.io.InputStream;


public class Main {

	/**
	 * @param args
	 */
	
    private static String execute( String command ) throws IOException  { 
        Process p = Runtime.getRuntime().exec( "cmd /c " + command );
        InputStream i = p.getInputStream();
        StringBuilder sb = new StringBuilder();
        for(  int c = 0 ; ( c =  i.read() ) > -1  ; ) {
            sb.append( ( char ) c );
        }
        i.close();
        return sb.toString();
    }	
	
    public static void ProcessResult(String sXmlResult)
    {
    	if (sXmlResult.startsWith("<alloy></alloy>"))
    	{	
    		System.out.println("The model is consistent");
    		return;
    	}

    	System.out.println("The model is inconsistent");
    	System.out.println(sXmlResult);
    	
    	
    }
    
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String result = execute("java -jar C:/ClaferIE/CS745/Code/ACPVerifier/AlloyRunner.jar C:/ClaferIE/CS745/Code/ACPVerifier/ModelVerificationTest.als C:/ClaferIE/CS745/Code/ACPVerifier");
		
		ProcessResult(result);
		
//		System.out.println(result);
	}

}
