package general.classes;

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
		// TODO Auto-generated method stub
		
	}

	public void load(String verFileName) {
		// TODO Auto-generated method stub
		
	}
}
