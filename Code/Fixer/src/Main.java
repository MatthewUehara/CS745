import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import safety.onlyoneapplicable.Fixer;
import safety.onlyoneapplicable.SafetyOnlyOneApplicable;
import safety.onlyoneapplicable.Verifier;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String result = "";
		
		String arg = "";
		if (args.length > 0)
			arg = args[0];
		
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
		
		try {
			Utilities.writeStringToFile("current_file.als", input.replaceAll("<br>", "\n"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		SafetyOnlyOneApplicable safetyProperty = new SafetyOnlyOneApplicable("current_file.als", "C:/ClaferIE/CS745/Code/Fixer");
		
		if (arg.startsWith("-fix"))
		{
			result = safetyProperty.fix(arg);
		}
		else
			result = safetyProperty.verify();
		
		System.out.println(result);
	}

}
