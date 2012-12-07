package safety.onlyoneapplicable;

import general.classes.Fix;
import general.classes.VerificationResult;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import alloyrunner.main.Utilities;

public class SafetyOnlyOneApplicable {

	private String fileName;
	private String jarPath;
	
	private String verFileName = "verification_result.txt";
	
	public SafetyOnlyOneApplicable(String fileName, String jarPath) {

		this.fileName = fileName;
		this.jarPath = jarPath;
	}

	public String verify() {
		String result = "";
		
	 	VerificationResult verificationResult = (new Verifier(this.jarPath)).verify(this.fileName);
		
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

		 		String contents = "";
		 		
		 		try {
					contents = Utilities.getFileContentsAsString(this.fileName);
				} catch (IOException e) {
					result = String.format("{\"error\":\"%s\"}", "Impossible to propose fixes");	 		
				}
		 		
		 		ArrayList<Fix> fixes = (new Fixer(this.jarPath)).propose(contents, verificationResult);
		 		
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
	 	
	 	verificationResult.save(verFileName);
	 	
	 	return result;
	}
	
	public String fix(String fixCommand) {
		String result = "";

		String fixId = "";
		
		String[] parts = fixCommand.split("_");
		if (parts.length > 0)
			fixId = parts[1];
		
		if (fixId.isEmpty())
			return "{\"error\":\"Error fixing: command has error, should be -fix_[number]\"}";
		
		try
		{
			Fixer fixer = new Fixer(this.jarPath);
			
			VerificationResult ver = new VerificationResult();
			ver.load(verFileName);
			String inputFileContents = Utilities.getFileContentsAsString(this.fileName);
	
			inputFileContents = URLDecoder.decode(inputFileContents.replace("+", "%2B"), "UTF-8").replace("%2B", "+").replace("<br>", "\n");
			result = fixer.fix(inputFileContents, ver, fixId);
			
			result = "{\"value\":\"" + URLEncoder.encode(result, "UTF-8").replaceAll("\\+","%20") + "\"}";
			
		}
		catch(Exception e)
		{
			result = "{\"error\":\"Error fixing\"}";
		}
		
		return result;
	}

}
