import java.io.*;
import java.util.*;

public class processResults
{
	public static void main(String args[])
	{
		String buffer;
		String successStatus, methodName;
		StringTokenizer values;
		HashMap testStats = new HashMap();
		
		//User didn't specify a log file, notify and exit
		if(args.length == 0)
		{
			System.out.println("Please enter a log file");
			System.exit(0);
		}
	
		try
		{
			String inputLog = args[0];
			Scanner logScanner = new Scanner(new File(inputLog));
			Stats s;	

			while(logScanner.hasNext())
			{
				buffer = logScanner.nextLine();
				//parse the individual lines "[PASS|FAIL] [methodName] [timeStamp]" (we don't care about the last one)
				values = new StringTokenizer(buffer);
				successStatus = values.nextToken();
				methodName = values.nextToken();
				
				//update the stats for this particular method
				s = (Stats)(testStats.get(methodName));
				if (s == null) s = new Stats();
				s.testsRun++;
				
				if(successStatus.equals("PASS"))
				{
					s.successfulTests++;
				}
				
				testStats.put(methodName, s);
			}
			
			Set keys = testStats.keySet();
			for (int i = 0; i < keys.size(); i++)
			{
				String method = keys.toArray()[i].toString();
				s = (Stats)(testStats.get(method));
				int failedTests = s.testsRun - s.successfulTests;
				float successRate = 100 * ((float) s.successfulTests / (float) s.testsRun);
				System.out.println("[" + method + "] Tests:" + s.testsRun + " Passes:" + s.successfulTests + " Fails:" + failedTests + " Success Rate:" + successRate + "%");
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
