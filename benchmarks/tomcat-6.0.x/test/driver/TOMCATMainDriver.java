package driver;

public class TOMCATMainDriver 
{
	public static void main(String[] args)
	{
		try{
		Thread t1 = new Thread()
		{
			public void run()
			{
				//org.apache.catalina.startup.Bootstrap.main(new String[0]);
				String[] arg = {"start"};
				org.apache.catalina.startup.Catalina.main(arg);
			}
		};
		
		Thread t2 = new Thread()
		{
			public void run()
			{
				MainClient.main(new String[0]);
			}
		};
		
		t1.start();
		Thread.sleep(1000);
		t2.start();
		Thread.sleep(10000);
		System.exit(0);
	}catch(Exception e)
	{
		
	}
	}
}
