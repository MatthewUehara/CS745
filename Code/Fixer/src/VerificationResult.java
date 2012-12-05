
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
	
	private boolean isConsisent;

	public boolean isError;
	
	public VerificationResult(boolean _isConsisent)
	{
		this.isConsisent = _isConsisent;
		this.isError = false;
	}

	public VerificationResult()
	{
		this.isError = false;
	}
}
