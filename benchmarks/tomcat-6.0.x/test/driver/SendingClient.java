package driver;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

public class SendingClient implements IClient,Runnable {
	private final int SIZE=Workload.SCRIPT.length;
	int count;
	int index;
	long time_start ;
	long time_end;
	boolean DEBUG=true;
	private int thinkTime=10;
	private double maxThink=150.0, thinkInterval=2.0;
	
	private RandomNumberGenerator randDynGen;
	private final CountDownLatch startSignal;
	private final Semaphore doneSignal;
	final HttpClient client;
	String URL="http://localhost:8080/";
	String url="http://localhost:8080/index.html"; 
	private ResponseHandler responseHandler;
	
	public SendingClient(CountDownLatch startSignal, Semaphore doneSignal) {
	      this.startSignal = startSignal;
	      this.doneSignal = doneSignal;
	      client = new DefaultHttpClient();
	      responseHandler = new BasicResponseHandler();
	}
	public void doAction() throws ClientActionException {
		try {
			
			HttpGet httpGet = new HttpGet(url);
			client.execute(httpGet, responseHandler);
			
			String responseBody = client.execute(httpGet, responseHandler);
			System.out.println(responseBody);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new ClientActionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClientActionException(e);
		}finally{
			return;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		doneSignal.release();
		if(DEBUG)
		System.out.println("Sending Client "+index+" is ready.");
		try {
			startSignal.await();
			if(DEBUG)
			System.out.println("Sending Client "+index+" action fired.");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			long time;
			int state=-1;
			
			randDynGen= new RandomNumberGenerator(6+index);
			for (int k=0;k<count;k++)
			{
				state = nextState(state);
				url=URL+Workload.SCRIPT[state];
//				if(!(state==15||state==16||state==0)&&thinkTime>0)
//					thinkFirst();
				time_start=System.nanoTime();
				doAction();
				time_end=System.nanoTime();
				time =(time_end-time_start)/1000; 
				System.out.println(index+"   "+state+"   "+time);
			}
			
		} catch (ClientActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  				
	}

	private int nextState(int state)
	{
//		double randNum;
//		randNum=randDynGen.nextDouble();
//		for (int i=0;i<Workload.stateTrans[state].length;i++)
//		{
//			if(randNum<Workload.stateProb[state][i])
//				return Workload.stateTrans[state][i];
//		}
		int randNum = randDynGen.nextInt()+state;
		if(randNum<0)
			randNum = -randNum;
		return randNum%SIZE;
	}
	
	private void thinkFirst(){
		
		 double x, e;
	        long remainder;
	        do {
	            do {
	                    x = Math.random();
	            } while (x == 0);

	            e = - Math.log(x);
	            e *= (thinkTime-(thinkInterval/2));
	        } while (e > maxThink);
	        
	        int tmp = (int)(e/thinkInterval);
	        
	        if (e%thinkInterval > 0)
	            tmp++;
	        
	        tmp *= thinkInterval*Workload.THINKTIME;

	
	            remainder = tmp;
	            
	            long now = System.currentTimeMillis();
	            
	        if (remainder > 0) {
	            	
	            	try {
	                    Thread.sleep(remainder);
	                } catch (InterruptedException exc) { 
	                    remainder -= (System.currentTimeMillis() - now);
	                }
	        }
	}
	
}
