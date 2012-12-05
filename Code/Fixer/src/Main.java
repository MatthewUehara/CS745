import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String result = "";
		
		String input = "";
		
/*		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		
		try {
			while((line = br.readLine()) != null)
			{
				input = input + line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			input = "";
		}*/
/*		
		try {
			input = Utilities.getFileContentsAsString("test.als");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	 	VerificationResult verificationResult = (new Verifier()).verify("test.als", "C:/ClaferIE/CS745/Code/Fixer");
		
	 	if (verificationResult.isError)
	 	{
			result = "{\"error\":\"verification\"}";	 		
	 	}
	 	else
	 	{
	 		ArrayList<Fix> fixes = (new Fixer()).propose(verificationResult);
	 		
	 		int numFixes = fixes.size();
	 		
			String myFix = "Remove rule 1 from P1";
			String myEst = "1";
			
			result += "\"fixes\" : [{\"id\":\"1\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"},";
			result += "{\"id\":\"2\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"},";
			result += "{\"id\":\"3\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"}]";
	
			String verificationText = "Policy P1, rule R1, Policy P2, rule R1";
			String verificationDecision = "inconsistent";
			
			result += ",\"verificationText\" : \"" + verificationText + input + "\"";
			result += ",\"verificationResult\" : \"" + verificationDecision + "\"";
			
			result = "{" + result + "}";
	 	}

		System.out.println(result);
	}

}
