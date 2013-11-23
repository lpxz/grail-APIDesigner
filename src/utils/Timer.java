package utils;

public class Timer {
    public static long lasttime =-1;
    public static void init()
    {
    	lasttime =System.currentTimeMillis();;
    }
    
    public static long timeSinceLast()
    {
    	long cur = System.currentTimeMillis();
    	long passed = cur-lasttime;
    	lasttime=cur;
    	return passed;
    }
    
    public static void reportTimeSinceLast()
    {
    	System.out.println("time since last:" + timeSinceLast());
    	
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
