import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RuleCompare {
	
	public RuleCompare(){
		System.out.print("Creating RuleCompare Engine...\n");
	}
	/*
	 * returns 1 if we need to delete first, 2 for second, 0 for nothing
	 */
	public int compare(String s1, String s2){
		String [] temp1= s1.split("_");
		System.out.println(Arrays.toString(temp1));   //easy way to print test array
		String [] temp2= s2.split("_");	
		System.out.println(Arrays.toString(temp2));
		
		//rule format checking
		if(temp1.length!=temp2.length){return 3;}
		int l1=temp1.length;
		if(!temp1[l1-1].equalsIgnoreCase(temp2[l1-1])){return 0;}
		int flag=0;
		for (int count=1; count<l1;count++){

			flag=0;
			String regex=temp1[count];
			String compare=temp2[count];	
//if(temp1[1].length()>temp2[1].length())
				
			if (temp1[1].equalsIgnoreCase(temp2[1]) && temp1[2].length()>temp2[1].length()){
				regex=temp2[count];
				compare=temp1[count];
				flag=1;
			}
			if(temp1[1].length()>temp2[1].length()){
				regex=temp2[count];
				compare=temp1[count];
			}

			
			System.out.println(regex);
			System.out.println(compare);
			
		    Pattern pattern = Pattern.compile(regex);
		    Matcher matcher = pattern.matcher(compare);
		    System.out.println("find(): "+matcher.find()+"\n");
		    //System.out.println("Flag Value: "+flag+"\n");
		    
		    //check if they are subset relation, if not, no deletion
		    if(!matcher.find(0)){return 0;}
		}
		if(flag==1){ return 2;}
		return 1;
	}
}
