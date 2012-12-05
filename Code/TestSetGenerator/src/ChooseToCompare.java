import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class ChooseToCompare {
	
	public ChooseToCompare(){		
		System.out.print("Choosing the combination...\n");
	}
	
	public void Choose() throws IOException{
		File file = new File("PolicySetB.csv");
		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;
		HashSet<String> ps = new HashSet<String>();
		int numPs=0;
		while((line = bufRdr.readLine())!= null)
		{	
			String []field = line.split(",",-1);
			//System.out.println(Arrays.toString(field));
			if (field[0].equals("Policies")){
				for(String val: field){
					if(!val.equals("Policies")){
						ps.add(val);
						numPs++;
					}
				}
		        Iterator it1 = ps.iterator();
		        System.out.println("===Rule HashSet===");
		        while (it1.hasNext()) {
		            System.out.println(it1.next());
		        }
				//System.out.println(numPs);
				//break;
			}

		}
		
		bufRdr.close();
		
	}

}
