package incorrectCompositions.confirm.dynamicenforce;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;

//import java.util.concurrent.CyclicBarrier;

public class RuntimeLibrary {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static boolean					AVconfirmed	= false;

	public static HashMap<Thread, Object>	firstInoke	= new HashMap<Thread, Object>();

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

	static Thread	pcThread	= null;
	static Object	mutex		= new Object();

	//	static CyclicBarrier	cBarrier	= new CyclicBarrier(2);

	public static void bidAtC(Thread currentThread, Object arg) {

		synchronized (mutex) {

			if (AVconfirmed)
				return;
			if (inComposition(currentThread)) {
				if (pcThread == null && firstInoke.get(currentThread) != null // how to be a king.
						&& firstInoke.get(currentThread) == arg) {
					// it is a real composition + noking_now = king.
					pcThread = currentThread;

					try {
						mutex.wait();//	okay, wait here. some thread will come to save you.
						firstInoke.remove(pcThread);// clear all things. 
						status.put(pcThread, false); //early version of exitComposition.
						pcThread = null; // no king now.
						Thread.dumpStack();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {

				// not interesting.
			}

		}

	}

	public static boolean inComposition(Thread t) {
		if (!status.containsKey(t))
			return false;
		Boolean boolean1 = (Boolean) status.get(t);
		return boolean1.booleanValue();
	}

	public static HashMap<Thread, Object>	status	= new HashMap<Thread, Object>();

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
		}

	}

	//synchronized keyword  is necessary.
	public static void checkAtR(Thread currentThread, Object arg) {
		synchronized (mutex) {

			if (AVconfirmed)
				return;
			if (pcThread != null && pcThread != currentThread
					&& firstInoke.get(pcThread) == arg) {
				// we got an AV here. report and free the pcThread.
				mutex.notify();//cBarrier.await();
				AVconfirmed = true;
				System.err.println("AV identified. ");
				Thread.dumpStack();
			}
		}
	}
}
