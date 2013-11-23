// Main.java
// $Id: Main.java,v 1.1 2010/06/15 12:30:03 smhuang Exp $  
// (c) COPYRIGHT MIT and INRIA, 1997.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.jigsaw;


/**
 * A place holder for running Jigsaw.
 */

public class Main {

    public static void main(String args[]) {
    	args[0]="10";
    	final String sleepSecs=args[0];
    	String[] newArgs= new String[args.length-1];
    	for(int i=0;i<newArgs.length; i++)
    	{
    		newArgs[i]= args[i+1];
    	}
    	(new Thread(){
    		  public void run() {
    			  {
    				  try {
						Thread.sleep(Integer.parseInt(sleepSecs)*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				  System.out.println("after "+sleepSecs+"secs, "+"auto-close, commander:lpxz");
    				  System.exit(0);
    		        }

    		    }
    		
    	}).start();
    	
	org.w3c.jigsaw.daemon.ServerHandlerManager.main(newArgs);
    }

}
