package safety.onlyoneapplicable;

import general.classes.Fix;
import general.classes.VerificationResult;

import java.util.ArrayList;

public class SafetyOnlyOneApplicable {

	private String fileName;
	private String jarPath;
	
	public SafetyOnlyOneApplicable(String fileName, String jarPath) {

		this.fileName = fileName;
		this.jarPath = jarPath;
	}

	public String verify() {
		String result = "";
		
	 	VerificationResult verificationResult = (new Verifier()).verify(this.fileName, this.jarPath);
		
	 	if (verificationResult.isError)
	 	{
			result = String.format("{\"error\":\"%s\"}", verificationResult.getOutput());	 		
	 	}
	 	else
	 	{
	 		String verificationText = verificationResult.getOutput();
			String verificationDecision;
			
			if (verificationResult.isConsisent)
				verificationDecision = "consistent";
			else verificationDecision = "inconsistent";
			
			result += "\"verificationText\" : \"" + verificationText + "\"";
			result += ",\"verificationResult\" : \"" + verificationDecision + "\"";
			
		 	if (!verificationResult.isConsisent)
		 	{

		 		ArrayList<Fix> fixes = (new Fixer()).propose(verificationResult);
		 		
		 		result += ",\"fixes\" : [";
		 		
		 		String fixList = "";
		 		
		 		for (Fix f : fixes)
		 		{
		 			fixList += String.format(",{\"id\":\"%s\", \"statement\":\"%s\",\"est\":\"%s\"}", f.id, f.statement, f.est);
		 		}
		 		
		 		if (fixList.length() > 0)
		 			fixList = fixList.substring(1);
		 		
		 		result +=  fixList + "]";
		 	}			
			
			result = "{" + result + "}";
	 	}
	 	return result;
	}

}
