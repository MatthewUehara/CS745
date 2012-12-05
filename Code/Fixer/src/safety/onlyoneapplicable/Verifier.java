package safety.onlyoneapplicable;
	
import general.classes.VerificationResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


	public class Verifier {

		/**
		 * @param args
		 */
		private Document alloyXMLDoc;
		
		  public NodeList xpath(String query)
		   throws ParserConfigurationException, SAXException,IOException, XPathExpressionException {

				XPath xpath = XPathFactory.newInstance().newXPath();
				// XPath Query for showing all nodes value
				XPathExpression expr = xpath.compile(query);

				Object result = expr.evaluate(alloyXMLDoc, XPathConstants.NODESET);
				NodeList nodes = (NodeList) result;
				
				if (nodes.getLength() == 0)
					return null;
				
				return nodes;
				
			}	
		
		public void InitXPath(String xmlText) throws ParserConfigurationException, SAXException, IOException
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); 
				
			
			
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			alloyXMLDoc = builder.parse(new InputSource(new ByteArrayInputStream(xmlText.getBytes("utf-8"))));
			
		}
		  
	    private static String execute( String command )  {

	    	String result = "";
	    	try
	    	{
		        Process p = Runtime.getRuntime().exec( "cmd /c " + command );
		        InputStream i = p.getInputStream();
		        StringBuilder sb = new StringBuilder();
		        for(  int c = 0 ; ( c =  i.read() ) > -1  ; ) {
		            sb.append( ( char ) c );
		        }
		        i.close();
		        result = sb.toString();
	    	}
	    	catch(Exception e)
	    	{
	    		result = "";
	    	}
	        return result;
	    }	
		
	    public String getSkolemValue(String name)
	    {
	    	String result = "";
	    	
	    	Node currentNode = null;
	    	
			try {
				currentNode = xpath("/alloy/instance/skolem[@label='" + "$" + name + "']/tuple/atom").item(0);
				result = currentNode.getAttributes().getNamedItem("label").getNodeValue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
			
			return result;
			
	    }

	    
		private String getFieldValue(String requestName, String relation) {
			
			String result = "";
			
			try
			{
				Node node = xpath("/alloy/instance/field[@label='" + relation + "']//atom[@label='" + requestName + "']/following-sibling::*[1]").item(0);
				result = node.getAttributes().getNamedItem("label").getNodeValue();
			}
			catch (Exception e)
			{
				
			}
	    	return filterSkolem(result);
		}

		private String filterSkolem(String s) {
			int pos = s.indexOf('$');
			if (pos == -1)
				return s;
			
			return s.substring(0, pos);
		}
	    
	    
	    public VerificationResult processResult(String sXmlResult)
	    {
//	    	System.out.println(sXmlResult);

	    	if (sXmlResult.startsWith("<alloy></alloy>"))
	    	{	
		    	VerificationResult result = new VerificationResult(true);
	    		return result;
	    	}
	    	
	    	if (!sXmlResult.startsWith("<alloy")) // it means error
	    	{	
		    	VerificationResult result = new VerificationResult();
		    	result.isError = true;
		    	result.errorText = sXmlResult;
	    		return result;
	    	}	    	
	    	

	    	try {
				InitXPath(sXmlResult);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	VerificationResult result = new VerificationResult(false);
	    	
	    	result.Rule1 = filterSkolem(getSkolemValue("InconsistentPolicySet_r1"));
	    	result.Rule2 = filterSkolem(getSkolemValue("InconsistentPolicySet_r2"));
	    	result.PolicySet = filterSkolem(getSkolemValue("InconsistentPolicySet_ps"));

	    	String[] temp;
	    	temp = result.Rule1.split("_");
	    	result.Rule1Effect = temp[temp.length - 1];
	    	temp = result.Rule2.split("_");
	    	result.Rule2Effect = temp[temp.length - 1];
	    	
	    	result.Rule1Target = result.Rule1.replace("Rule_", "Target_");
	    	result.Rule2Target = result.Rule2.replace("Rule_", "Target_");
	    	
	    	String reqName = getSkolemValue("InconsistentPolicySet_req");
	    	result.Policy1 = getSkolemValue("InconsistentPolicySet_p1");
	    	result.Policy2 = getSkolemValue("InconsistentPolicySet_p2");
	    	
	    	result.RequestSubject = getFieldValue(reqName, "subject").substring(1);
	    	result.RequestResource = getFieldValue(reqName, "resource").substring(1);
	    	result.RequestAction = getFieldValue(reqName, "action").substring(1);
	    	
	    	result.Policy1Algo = getFieldValue(result.Policy1, "combiningAlgo");
	    	result.Policy2Algo = getFieldValue(result.Policy2, "combiningAlgo");
	    	
	    	result.Policy1 =  filterSkolem(result.Policy1);
	    	result.Policy2 = filterSkolem(result.Policy2);
	    	
	    	return result;
	    	
	    }

		public VerificationResult verify(String fileName, String libPath){
			// TODO Auto-generated method stub
			
			VerificationResult result;

			String toolFileName =  "AlloyRunner.jar";
			
			if (!new File(toolFileName).exists())
			{
				result = new VerificationResult();
				result.isError = true;
				result.errorText = "AlloyRunner.jar was not found.";				
				return result;
			}

			String command = String.format("java -jar \"%s\" \"%s\" \"%s\"",
					toolFileName, fileName, libPath);
			
//			command = "java -jar %s \"C:/ClaferIE/CS745/Code/Fixer/current_file.als\" \"C:/ClaferIE/CS745/Code/Fixer\"";
					
			String xmlFromAnalyzer = execute(command);	
			
			
			if (xmlFromAnalyzer.length() == 0)
			{
				result = new VerificationResult();
				result.isError = true;
				result.errorText = "Empty result is returned.";
			}
			else
			{
				result = processResult(xmlFromAnalyzer);
			}
			
			return result;
		}

	}
