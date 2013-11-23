package driver;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.exolab.jms.server.JmsServer;


public class OpenJMSDriver {
	static  CountDownLatch start;
	static  CountDownLatch end;
	public static void main(String []s){
		long start_time = System.currentTimeMillis();
		
		startserver(0);
		int senderno = 2, receiverno= 2, nomsgs=2;//1000
		if(s.length==3)
		{
			senderno = Integer.valueOf(s[0]); 
			receiverno = Integer.valueOf(s[1]); 
			nomsgs = Integer.valueOf(s[2]); 
		}
		System.out.println("args reported by openjms are: " + senderno + " " + receiverno + " " + nomsgs);
		
	   	start = new CountDownLatch(senderno+receiverno);
	   	end = new CountDownLatch(receiverno);
	   	s = new String[] {"queue1",Integer.toString(nomsgs),Integer.toString(senderno)};
	   	startsender(s);
	   	s = new String[] {"queue1",Integer.toString(nomsgs),Integer.toString(receiverno)};
	   	startreceiver(s);
	   	try {
			end.await();
			long end_time= System.currentTimeMillis();
			long total_time = end_time - start_time;
			System.out.println("Total execution time: "+total_time);
			System.exit(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}    
	public static void startserver(int k){
	try {
		Properties p = System.getProperties();
		p.setProperty("derby.system.home","db");
		//p.setProperty("openjms.home",".");
        JmsServer server = new JmsServer("config/openjms.xml");
        server.init();
    } catch (Exception exception) {
        exception.printStackTrace();
        // force termination of any threads
        System.exit(-1);
    }
	}
	
	 public static void startsender(String s[]){
	    	int no = Integer.parseInt(s[2]);
	    	for(int i=0;i<no;i++){
	    		new Sender(s,start).start();
	    		start.countDown();
	    	}
	    }
	 
	 public static void startreceiver(String s[]){
			int no = Integer.parseInt(s[2]);
			for(int i=0;i<no;i++){
				new Receiver(s,start,end).start();
				start.countDown();
			}
		}
}
