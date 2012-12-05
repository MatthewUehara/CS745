import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String result = "";
		
		String input = "";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
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
		}
		
		String myFix = "Remove rule 1 from P1";
		String myEst = "1";
		
		result += "\"fixes\" : [{\"id\":\"1\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"},";
		result += "{\"id\":\"2\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"},";
		result += "{\"id\":\"3\", \"fix\":\"" + myFix + "\",\"est\":\"" + myEst + "\"}]";

		String verificationText = "Policy P1, rule R1, Policy P2, rule R1";
		String verificationResult = "inconsistent";
		
		result += ",\"verificationText\" : \"" + verificationText + input + args[0] + "\"";
		result += ",\"verificationResult\" : \"" + verificationResult + "\"";
		
		result = "{" + result + "}";
		
		//	    result += "<tr><td>Remove rule 4 from Policy 2</td><td>1</td></tr>";

		System.out.println(result);
	}

}
