package general.classes;

import java.io.IOException;

import alloyrunner.main.Utilities;

public class VerificationResult {
	public String Rule1;
	public String Policy1;
	public String Rule1Target;
	public String Rule1Effect;
	public String Policy1Algo;
	
	public String Rule2;
	public String Policy2;
	public String Rule2Target;
	public String Rule2Effect;
	public String Policy2Algo;
	
	public String RequestSubject;
	public String RequestAction;
	public String RequestResource;
	
	public String PolicySet;
	
	public boolean isConsisent;

	public boolean isError;
	public String errorText;
	
	public String getOutput()
	{
		if (this.isError)
			return this.errorText;
		
		if (this.isConsisent)
			return "The model is consistent. OK.";
		
		return String.format("In policy set %s, policies %s and %s conflicting: the former's rule %s returns %s, the latter's rule %s returns %s.", 
				this.PolicySet, this.Policy1, this.Policy2, this.Rule1, this.Rule1Effect, this.Rule2, this.Rule2Effect				
				);
	}
	
	public VerificationResult(boolean _isConsisent)
	{
		this.isConsisent = _isConsisent;
		this.isError = false;
	}

	public VerificationResult()
	{
		this.isError = false;
	}

	public void save(String verFileName) {
		String result = "";
		String sep = "|";
		
		result += this.Rule1 + sep;
		result += this.Policy1 + sep;
		result += this.Rule1Target + sep;
		result += this.Rule1Effect + sep;
		result += this.Policy1Algo + sep;
		
		result += this.Rule2 + sep;
		result += this.Policy2 + sep;
		result += this.Rule2Target + sep;
		result += this.Rule2Effect + sep;
		result += this.Policy2Algo + sep;
		
		result += this.RequestSubject + sep;
		result += this.RequestAction + sep;
		result += this.RequestResource + sep;
		
		result += this.PolicySet;
		
		try {
			Utilities.writeStringToFile(verFileName, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void load(String verFileName) {
		try{
			String contents = Utilities.getFileContentsAsString(verFileName);
			String[] parts = contents.split("\\|", -1);
			
			this.Rule1 = parts[0];
			this.Policy1 = parts[1];
			this.Rule1Target = parts[2];
			this.Rule1Effect = parts[3];
			this.Policy1Algo = parts[4];
			
			this.Rule2 = parts[5];
			this.Policy2 = parts[6];
			this.Rule2Target = parts[7];
			this.Rule2Effect = parts[8];
			this.Policy2Algo = parts[9];
			
			this.RequestSubject = parts[10];
			this.RequestAction = parts[11];
			this.RequestResource = parts[12];
			
			this.PolicySet = parts[13];
		}
		catch(Exception e)
		{
			
		}
		
	}
}
