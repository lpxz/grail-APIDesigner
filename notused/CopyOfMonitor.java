package incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor;

import incorrectCompositions.detection.offlinePrediction.CommonUtil;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.*;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.*;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.generator.*;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context.PreciseSTEContextEncoderDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.HashMap;
import java.util.Vector;

//import com.google.common.collect.ArrayListMultimap;

public class CopyOfMonitor {
	static int											counter				= 0;
	public static Throwable								crashedException	= null;

	public static AbstractNode							entryNode;
	static HashMap<Integer, String>						indexSPEMap			= null;
	public static HashMap<Integer, MessageNode>			indexToDeterminNode	= new HashMap<Integer, MessageNode>();
	private static HashMap<Integer, Integer>			lockCountsMap		= new HashMap<Integer, Integer>();

	private static HashMap<Integer, LockNode>			lockDepMap			= new HashMap<Integer, LockNode>();

	public static String[]								mainargs;
	public static String								methodname;

	public static MonitorData							mondata;
	private static int									objectIndexCounter	= 500;

	private static HashMap<Object, Integer>				objectMemMap		= new HashMap<Object, Integer>();

	static HashMap<Integer, HashMap<Object, Integer>>	spe2obj2rtID		= new HashMap<Integer, HashMap<Object, Integer>>();
	public static HashMap<String, Long>					threadNameToIdMap	= new HashMap<String, Long>();

	public static HashMap<Thread, MessageNode>			threadToExitNode	= new HashMap<Thread, MessageNode>();
	public static HashMap<Thread, MessageNode>			threadToStartNode	= new HashMap<Thread, MessageNode>();
	public static int									VECARRAYSIZE		= 500;

	public synchronized static void accessSPE(int index, long threadId) {
	}

	public synchronized static void crashed(Throwable crashedException) {
		Parameters.isCrashed = true;
		System.exit(-1);
	}

	public synchronized static void enterCommonMethodAfter(int methoId,
			long threadId, String msubsig) {
		// we use the asm to extract the context, so you do not need to maintain the stack by yourself.
	}

	public synchronized static void enterMonitorAfter(Object o, int iid,
			long threadId) {
		if (o.toString().endsWith(".STATIC.")) {
			try {
				o = Class.forName(o.toString().replace(".STATIC.", "")); // synchronized(A.class){}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);
		if (isNotReentrant(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.LOCK);

			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);
			mondata.addToTrace(newNode);
		}

	}

	public synchronized static void enterSyncMethodBefore(int iid, long threadId) {
		int id = getObjectMem(iid);
		if (isNotReentrant(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.LOCK);

			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);

			mondata.addToTrace(newNode);
		}
	}

	public synchronized static void enterSyncMethodBefore(Object o, int iid,
			long threadId) {
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);

		if (isNotReentrant(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.LOCK);

			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);

			mondata.addToTrace(newNode);
		}
	}

	public synchronized static void exitCommonMethodBefore(int methoId,
			long threadId, String msubsig) {
		// do not need to maintain the context stack yourself, we use the asm to extract the context.

	}

	public synchronized static void exitMonitorBefore(Object o, int iid,
			long threadId) {
		if (o.toString().endsWith(".STATIC.")) {
			try {
				o = Class.forName(o.toString().replace(".STATIC.", ""));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);
		if (isLastRelease(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.UNLOCK);
			lockDepMap.put(id, newNode);
			mondata.addToTrace(newNode);
		}

	}

	public synchronized static void exitSyncMethodAfter(int iid, long threadId) {
		int id = getObjectMem(iid);
		if (isLastRelease(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.UNLOCK);

			lockDepMap.put(id, newNode);

			mondata.addToTrace(newNode);
		}
	}

	public synchronized static void exitSyncMethodAfter(Object o, int iid,
			long threadId) {
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);

		if (isLastRelease(id)) {
			LockNode newNode = new LockNode(id, threadId,
					AbstractNode.TYPE.UNLOCK);

			lockDepMap.put(id, newNode);

			mondata.addToTrace(newNode);
		}
	}

	public static void generateTestDriver(String traceFile_) {
		try {
			CrashTestCaseGenerator.main(new String[] { traceFile_,
					Util.getTmpReplayDirectory() });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String getName4RWNode(int spe) {
		if (indexSPEMap == null) {
			ObjectInputStream in = null;
			String filename = CommonUtil.getTmpTransDirectory() + "spe."
					+ mondata.getClassName() + ".gz";

			try {
				File file = new File(filename);

				if (filename.endsWith(".gz")) {
					in = new ObjectInputStream(new GZIPInputStream(
							new FileInputStream(file)));
				} else {
					in = new ObjectInputStream(new FileInputStream(file));
				}

				indexSPEMap = (HashMap<Integer, String>) CommonUtil
						.loadObject(in);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
		return indexSPEMap.get(spe);

	}

	private static int getObjectMem(Object o) {
		if (objectMemMap.get(o) == null) {
			objectMemMap.put(o, objectIndexCounter++);
		}
		return objectMemMap.get(o);
	}

	private static boolean isLastRelease(int id) {
		Integer num = lockCountsMap.get(id);
		assert (num != null);

		if (num == 1) {
			lockCountsMap.put(id, null);
			return true;
		} else {
			lockCountsMap.put(id, --num);
			return false;
		}
	}

	private static boolean isNotReentrant(int id) {
		Integer num = lockCountsMap.get(id);
		if (num == null) {
			lockCountsMap.put(id, 1);
			return true;
		} else {
			lockCountsMap.put(id, ++num);
			return false;
		}
	}

	public synchronized static void joinRunThreadAfter(Thread t, long threadId) {
		int iid = System.identityHashCode(t);
		int id = getObjectMem(iid);

		MessageNode newNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.RECEIVE);

		mondata.addToTrace(newNode);

		MessageNode node = threadToExitNode.get(t);
		if (node != null) {
			newNode.setDepNode(node);
		}
	}

	public synchronized static void mainThreadStartRun(long threadId,
			String methodName, String[] args) {

	}

	public synchronized static void mainThreadStartRun0(String classname,
			String[] args) {

		Thread mainThread = Thread.currentThread();
		long threadId = mainThread.getId();

		threadNameToIdMap.put(Parameters.MAIN_THREAD_NAME, threadId);// Thread.currentThread().getName()
		mainargs = args;
		methodname = classname + ".main";

		getObjectMem(System.identityHashCode(mainThread));

		mondata = new MonitorData();
		mondata.setClassName(classname);

	}

	public synchronized static void notifyAllBefore(Object o, int iid,
			long threadId) {
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);

		MessageNode newNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.SEND);

		mondata.addToTrace(newNode);

		indexToDeterminNode.put(id, newNode);
	}

	public synchronized static void notifyBefore(Object o, int iid,
			long threadId) {
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);
		MessageNode newNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.SEND);

		mondata.addToTrace(newNode);

		indexToDeterminNode.put(id, newNode);
	}

	public synchronized static void readBeforeInstance(Object o, int iid,
			long threadId, int line, String msig, String jcode) {

		int rtID4each = rtID4each(o, iid);// iid is the inpreicse spe (class-based) encoding, which will be
											// replaced later in engineMain, 
											// we replace it with the precise encoding (object-based) now directly

		RWNode newNode = new RWNode(line, rtID4each, threadId,
				AbstractNode.TYPE.READ);
		newNode.setNewMem(rtID4each);//
		newNode.setMemString(getName4RWNode(iid));
		newNode.setMsig(msig);
		newNode.setJcode(jcode);

		//TODO hack it to let it aware of the composition Lib/CLient.
		if (PropertyManager.useasmStack) {
			if (PropertyManager.isInteresting(msig)) {
				Vector contextVal = PreciseSTEContextEncoderDecoder
						.assignContextValue(Thread.currentThread(), msig);
				newNode.setIntContext(contextVal);
			}
		}

		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);

	}

	public synchronized static void readBeforeStatic(int iid, long threadId,
			int line, String msig, String jcode) {
		int rtID4each = rtID4each(null, iid);
		RWNode newNode = new RWNode(line, rtID4each, threadId,
				AbstractNode.TYPE.READ);
		newNode.setNewMem(rtID4each);
		newNode.setMemString(getName4RWNode(iid));
		newNode.setMsig(msig);
		newNode.setJcode(jcode);

		if (PropertyManager.useasmStack) {
			if (PropertyManager.isInteresting(msig)) {
				Vector contextVal = PreciseSTEContextEncoderDecoder
						.assignContextValue(Thread.currentThread(), msig);
				newNode.setIntContext(contextVal);
			}
		}

		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);
	}

	private static int rtID4each(Object o, int speid) { // null is just a special object
		HashMap<Object, Integer> obj2rtID = spe2obj2rtID.get(speid);
		if (obj2rtID == null) {
			obj2rtID = new HashMap<Object, Integer>();
			spe2obj2rtID.put(speid, obj2rtID);
		}
		int systemIdentityCode = System.identityHashCode(o);// null-> 0
		Integer tmp = obj2rtID.get(systemIdentityCode);
		if (tmp == null) {
			int tmpcounter = counter++;
			obj2rtID.put(systemIdentityCode, tmpcounter);
			tmp = obj2rtID.get(systemIdentityCode);
		}
		return tmp.intValue();

	}

	public static String saveMonitorData() {
		String traceFile_ = null;
		File traceFile_monitordata = null;
		File traceFile_threadNameToIdMap = null;
		ObjectOutputStream fout_monitordata;
		ObjectOutputStream fout_threadNameToIdMap;

		// SAVE Runtime Information
		try {
			System.out.println("come heresss!!!");
			// for replay!
			traceFile_monitordata = File.createTempFile("Clap",
					"_monitordata.trace.gz",
					new File(Util.getReplayTmpDirectory(methodname)));

			String traceFileName = traceFile_monitordata.getAbsolutePath();
			int index = traceFileName.indexOf("_monitordata");
			traceFile_ = traceFileName.substring(0, index);
			traceFile_threadNameToIdMap = new File(traceFile_
					+ "_threadNameToIdMap.trace.gz");

			assert (traceFile_monitordata != null && traceFile_threadNameToIdMap != null);

			fout_monitordata = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_monitordata)));
			fout_threadNameToIdMap = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							traceFile_threadNameToIdMap)));

			Util.storeObject(mondata, fout_monitordata);
			Util.storeObject(threadNameToIdMap, fout_threadNameToIdMap);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return traceFile_;
	}

	public static void setVecArraySize(Integer size) {
		VECARRAYSIZE = size;
		objectIndexCounter = size;
	}

	public synchronized static void startRunThreadBefore(Thread t, long threadId) {
		int iid = System.identityHashCode(t);
		int id = getObjectMem(iid);

		MessageNode startNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.SEND);

		mondata.addToTrace(startNode);
		threadToStartNode.put(t, startNode);
	}

	public synchronized static void threadExitRun(long threadId) {
		Thread currentThread = Thread.currentThread();

		int iid = System.identityHashCode(currentThread);
		int id = getObjectMem(iid);

		MessageNode exitNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.SEND);

		mondata.addToTrace(exitNode);

		threadToExitNode.put(currentThread, exitNode);
	}

	public synchronized static void threadStartRun(long threadId) {
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		threadNameToIdMap.put(threadName, threadId);

		int iid = System.identityHashCode(currentThread);
		int id = getObjectMem(iid);

		MessageNode newNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.RECEIVE);

		mondata.addToTrace(newNode);

		MessageNode node = threadToStartNode.get(currentThread);
		if (node != null) {
			newNode.setDepNode(node);
		}
	}

	public synchronized static void waitAfter(Object o, int iid, long threadId) {
		iid = System.identityHashCode(o);
		int id = getObjectMem(iid);

		MessageNode newNode = new MessageNode(id, threadId,
				AbstractNode.TYPE.RECEIVE);

		mondata.addToTrace(newNode);

		MessageNode node = indexToDeterminNode.get(id);
		if (node != null) {
			newNode.setDepNode(node);
		}
	}

	public synchronized static void writeBeforeInstance(Object o, int iid,
			long threadId, int line, String msig, String jcode) {

		int rtID4each = rtID4each(o, iid);
		RWNode newNode = new RWNode(line, rtID4each, threadId,
				AbstractNode.TYPE.WRITE);// MAKE THIS NULL TEMPORARILY
		newNode.setNewMem(rtID4each);
		newNode.setMemString(getName4RWNode(iid));
		newNode.setMsig(msig);
		newNode.setJcode(jcode);

		if (PropertyManager.useasmStack) {
			if (PropertyManager.isInteresting(msig)) {
				Vector contextVal = PreciseSTEContextEncoderDecoder
						.assignContextValue(Thread.currentThread(), msig);
				newNode.setIntContext(contextVal);
			}
		}

		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);

	}

	public synchronized static void writeBeforeStatic(int iid, long threadId,
			int line, String msig, String jcode) {

		int rtID4each = rtID4each(null, iid);
		RWNode newNode = new RWNode(line, rtID4each, threadId,
				AbstractNode.TYPE.WRITE);// MAKE THIS NULL TEMPORARILY
		newNode.setNewMem(rtID4each);
		newNode.setMemString(getName4RWNode(iid));
		newNode.setMsig(msig);
		newNode.setJcode(jcode);

		if (PropertyManager.useasmStack) {
			if (PropertyManager.isInteresting(msig)) {
				Vector contextVal = PreciseSTEContextEncoderDecoder
						.assignContextValue(Thread.currentThread(), msig);
				newNode.setIntContext(contextVal);
			}
		}

		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);
	}
}
