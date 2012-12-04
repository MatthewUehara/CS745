import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.SAXException;


public class Main {

	/**
	 * @param args
	 */
	
	  public static void xpath(String query)
			   throws ParserConfigurationException, SAXException,IOException, XPathExpressionException {

					DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
					domFactory.setNamespaceAware(true); 
						
					DocumentBuilder builder = domFactory.newDocumentBuilder();
					Document doc = builder.parse("");
			    
					XPath xpath = XPathFactory.newInstance().newXPath();
					// XPath Query for showing all nodes value
					XPathExpression expr = xpath.compile(query);

					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList nodes = (NodeList) result;
					for (int i = 0; i < nodes.getLength(); i++) {
						System.out.println(nodes.item(i).getNodeValue()); 
					}
			  
				}	
	
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
		String result = execute("java -jar C:/ClaferIE/CS745/Code/ACPVerifier/AlloyRunner.jar C:/ClaferIE/CS745/Code/ACPVerifier/ModelVerificationTest_RED.als C:/ClaferIE/CS745/Code/ACPVerifier");
		
		ProcessResult(result);
		
//		System.out.println(result);
	}

}
