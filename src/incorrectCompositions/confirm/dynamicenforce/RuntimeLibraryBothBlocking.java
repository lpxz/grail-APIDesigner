package incorrectCompositions.confirm.dynamicenforce;

import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import utils.CentralConstants;

public class RuntimeLibraryBothBlocking {

	static {
		Thread wakeupThread = new Thread() {
			public void run() {
				{

					while (numOfWaiting < 2) {
						try {

							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
					synchronized (mutex) {
						if (numOfWaiting == 2) {
							System.err.println("wake up.");
							mutex.notifyAll();
						}
					}

				}

			}

		};
		wakeupThread.start();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static boolean					AVconfirmed	= false;

	public static HashMap<Thread, Object>	firstInoke	= new HashMap<Thread, Object>();
	public static HashMap<Thread, Object>	rInoke		= new HashMap<Thread, Object>();

	static Thread							rThread		= null;
	static Thread							pcThread	= null;
	static Object							mutex		= new Object();

	public static HashMap<Thread, Object>	status		= new HashMap<Thread, Object>();

	public static void bidAtP(Thread currentThread, Object arg) {
		synchronized (mutex) {
			if (AVconfirmed)
				return;
			if (inComposition(currentThread)) {
				firstInoke.put(currentThread, arg);
			} else {
				// not interesting.
			}
		}

	}

	//	static CyclicBarrier	cBarrier		= new CyclicBarrier(2);
	static int	numOfWaiting	= 0;

	public static void bidAtC(Thread currentThread, Object arg) {

		synchronized (mutex) {

			if (AVconfirmed)
				return;
			if (inComposition(currentThread)) {
				if (pcThread == null && firstInoke.get(currentThread) != null // how to be a king.
						&& firstInoke.get(currentThread) == arg) {
					// it is a real composition + noking_now = king.
					pcThread = currentThread;

					int trycount = 0;

					while (!AVconfirmed && trycount <= 10) {
						try {
							numOfWaiting++;
							mutex.wait();
							numOfWaiting--;
						} catch (Exception e) {
							e.printStackTrace();
						}
						AVconfirmed = AVconfirmed
								|| (arg == rInoke.get(rThread));

					}

					if (AVconfirmed) {
						System.err.println("AV identified. ");
						if (CentralConstants.printStack4ConfirmedBug)
							Thread.dumpStack();
					}

					exitComposition(currentThread);// early exit.

				}

			}
		}

		if (AVconfirmed) {// do not put it into above Atomic block. the rinvoke would wait for a long time too because it cannot grab the monitor.
			try {
				Thread.sleep(1000); // leave sufficient time for rinvoke finish its job. 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static boolean inComposition(Thread t) {
		if (!status.containsKey(t))
			return false;
		Boolean boolean1 = (Boolean) status.get(t);
		return boolean1.booleanValue();
	}

	public static void enterComposition(Thread currentThread) {
		synchronized (mutex) {
			// you can add some parameter, like cmoposition id.
			if (AVconfirmed)
				return;
			status.put(currentThread, true);
		}

	}

	public static void exitComposition(Thread currentThread) {
		synchronized (mutex) {
			if (AVconfirmed)
				return;
			status.put(currentThread, false);
			firstInoke.remove(currentThread);
			pcThread = null;
		}

	}

	//synchronized keyword  is necessary.
	public static void checkAtR(Thread currentThread, Object arg) {
		synchronized (mutex) {

			if (AVconfirmed)
				return;

			if (rThread == null) {
				rInoke.put(currentThread, arg);// similar to bidAtP.
				rThread = currentThread; // king.

				try {
					numOfWaiting++;
					mutex.wait(10000); // deadlock: two waits both in a sync block. one enters, the other cannot any more.
					numOfWaiting--;
				} catch (Exception e) {//TimeOut.
					e.printStackTrace();
					numOfWaiting--;
					rInoke.remove(currentThread);
					rThread = null; // similar to exitComposition.
					return;
				}
				AVconfirmed = AVconfirmed || (arg == firstInoke.get(pcThread));
				if (AVconfirmed) {
					System.err.println("AV identified. ");
					if (CentralConstants.printStack4ConfirmedBug)
						Thread.dumpStack();
				}

				rInoke.remove(currentThread);
				rThread = null; // similar to exitComposition.

			}

		}

	}
}
