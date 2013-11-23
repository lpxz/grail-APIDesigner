package driver;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class MainClient {

	static int CLIENT_NUM = 2;
	static int OP_NUM = 2;
	private static long time_start;
	private static long time_end;
	static boolean DEBUG=true;
	static String str="http://localhost:8080/";
	public static void main(String[] args) 
	{
		if(args.length>0)
		{
			CLIENT_NUM=Integer.valueOf(args[0]).intValue();
			OP_NUM=Integer.valueOf(args[1]).intValue();
		}
		if(args.length>2)
		{
			str=args[2];

		}
		
		if(args.length>3)
		{
			Workload.THINKTIME=Integer.valueOf(args[3]).intValue();

		}
		
		if(args.length>4)
		{
			DEBUG=false;

		}
		
		Thread[] T = new Thread[CLIENT_NUM];
		SendingClient[] SClient = new SendingClient[CLIENT_NUM];
		//ReceivingClient[] SClient = new ReceivingClient[CLIENT_NUM];
		CountDownLatch startSignal = new CountDownLatch(1);
	    Semaphore doneSignal = new Semaphore(0);
	    
	    
	    for (int k=0;k<CLIENT_NUM;k++) 
	    {
		    SClient[k]=new SendingClient(startSignal,doneSignal);
	    	//SClient[k]=new ReceivingClient(startSignal,doneSignal);
		    SClient[k].index=k+1;
		    SClient[k].count=OP_NUM;
		    SClient[k].URL=str;
		    SClient[k].DEBUG=DEBUG;
		    T[k] = new Thread(SClient[k]);
	    }
    
		for (int k=0;k<CLIENT_NUM;k++)
		{
			T[k].start();
			try {
				doneSignal.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(DEBUG)
		System.out.println("----- All clients are created -----");
		
		time_start=System.nanoTime();
		
  		startSignal.countDown();      // let all threads proceed	
  		
		for (int k=0;k<CLIENT_NUM;k++)
		{
			try {
				T[k].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		time_end=System.nanoTime();
		
		long time = (time_end-time_start)/1000000;
		
		System.out.println("----- Work done in "+time+" ms. -----");
		System.exit(0);
	}
}
