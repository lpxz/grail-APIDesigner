package driver;

import java.util.concurrent.Semaphore;

public class JigsawDriver 
{
	public static void main(final String[] args)
	{
		final String[] arg =args;
//		new Thread()
//		{
//			public void run()
//			{
//				for(int i=0; i<=10;i++)
//				{
//					try {
//						Thread.sleep(1000);
//						DeveloperInterface.invokeDSU();
//
//					} catch (InterruptedException e) {
//						// XXX Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		}.start();
		
		try{
			
			
		Thread t1 = new Thread()
		{
			public void run()
			{
				org.w3c.jigsaw.Main.main(args);
			}
		};
		
//		Semaphore ss = new Semaphore(0);
//		ss.acquire();
//		ss.release();
		Thread t2 = new Thread()
		{
			public void run()
			{
				JigsawHarnessPretex.main(args);
			}
		};
		
		t1.start();
		t2.start();
		Thread.sleep(10000);
		
	//	System.exit(0);
	}catch(Exception e)
	{
		
	}
	}
}
