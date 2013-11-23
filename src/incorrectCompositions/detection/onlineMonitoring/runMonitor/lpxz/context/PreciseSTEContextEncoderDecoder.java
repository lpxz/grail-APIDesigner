package incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


// we encode the STE stack to an int stack during the online monitoring, for saving space.
// we load the int stack back and translate it to the STE stack in the offline analysis.
// in the int stack, each element is an integer, which corresponds to a unique STE (stack trace element) in the STE stack.

public class PreciseSTEContextEncoderDecoder {

	// for encoding a vector. save-space optimization.
	public static long				base		= 100000;							// at most N methods are there in the program.

	static HashMap<Thread, Vector>	cacheLine	= new HashMap<Thread, Vector>();	//

	public static List<String>		ctxts		= new ArrayList<String>();

	public static Vector assignContextValue(Thread currentThread, String msig) {
		Vector tmp = new Vector();
		List<String> fullstes = PreciseSTEContextAnalyzer
				.getPreciseStackTraceContext(msig);
		for (int i = 0; i < fullstes.size(); i++) {
			String fullste = fullstes.get(i);
			tmp.add(getValue(fullste));// add to the last
		}
		return tmp;
	}

	public static String[]	unInstruClasses	= {
			"org.apache.log4j.",
			"jrockit.",
			"java.",
			"javax.",
			"xjava.",
			"COM.",
			"com.",
			"cryptix.",
			"sun.",
			"sunw.",
			"junit.",
			"org.junit.",
			"org.xmlpull.",
			"edu.hkust.clap.",
			// the following are copied frpm the transforming options.
			// (commandLine)
			"org.apache.commons.logging.",// annoying, ban it
			"org.apache.xalan.", "org.apache.xpath.", "org.springframework.",
			"org.jboss.", "jrockit.", "edu.", "checkers.",
			"org.codehaus.spice.jndikit.",
			"EDU.oswego.cs.dl.util.concurrent.WaiterPreferenceSemaphore",
			"soot.", "aTSE.", "pldi.", "popl.", "beaver.", "org.jgrapht",
			"ca.pfv.spmf.", "japa.parser.", "polyglot."

											// org.w3c. is the including option
											};

	public static boolean shouldInstruThis_ClassFilter(String scname) {
		for (int k = 0; k < unInstruClasses.length; k++) {
			if (scname.startsWith(unInstruClasses[k])) {
				return false;
			}
		}

		return true;
	}

	public static boolean isApplicationContext(String ste) {
		String classname = PreciseSTEContextHelper.getClass(ste);
		if (shouldInstruThis_ClassFilter(classname)) {
			String methodName = PreciseSTEContextHelper.getMethod(ste);
			if (!methodName.contains("<clinit>")) {
				return true;
			}
		}
		return false;
	}

	public static void getSTEVector(RWNode pNode, List pvList) {
		Vector intv = pNode.getIntContext();
		// ===========get string vector

		if (intv == null)
			return;
		for (int i = 0; i < intv.size(); i++) {

			Integer integer = (Integer) intv.get(i);

			String ctxtString = ctxts.get(integer.intValue());
			pvList.add(ctxtString);
		}

	}

	private static int getValue(String tmp) {
		// String tmp = ste.toString();

		if (ctxts.contains(tmp)) {
			return ctxts.indexOf(tmp);
		} else {
			ctxts.add(tmp);
			return ctxts.indexOf(tmp);
		}

	}

	public static Vector long2Vector(long value) {
		Vector<Integer> vv = new Vector<Integer>();
		while (value != 0) {
			int yushu = (int) (value % base);
			vv.add(0, yushu);// addFirst
			value = value / base;
		}
		return vv;
	}

	public static void main(String[] args) {
		Vector<Integer> vv = long2Vector(325);
		for (int i = 0; i < vv.size(); i++) {
			System.out.println(vv.get(i));
		}
		// Vector<Integer> vv = new Vector<Integer>();
		// vv.add(3);
		// vv.add(2);
		// vv.add(5);
		// System.out.println(vector2long(vv));
	}

	// internal: get [Mn, cn, ... M0,c0], then reverse.
	// rename!
	//	public static Vector<ContextMethod> translate2MCpairV(Vector intv) {
	//		// ===========get string vector
	//		tmpVector.clear();
	//		for (int i = 0; i < intv.size(); i++) {
	//			Integer integer = (Integer) intv.get(i);
	//			String ctxtString = ctxts.get(integer.intValue());
	//			tmpVector.add(ctxtString);
	//		}
	//		// =========== get stmt vector
	//		Vector<ContextMethod> toret = new Vector<ContextMethod>();
	//		for (int i = 0; i <= tmpVector.size() - 2; i++)// 0 is the last called
	//														// |tmpVector.size()-1
	//														// is the run(), it is
	//														// possble size==1
	//		{
	//			String curSTE = (String) tmpVector.get(i);
	//			String secSTE = (String) tmpVector.get(i + 1);
	//			SootMethod curMethod = SootAgent4Pecan.getMethod(
	//					FullSTEManipulater.getClass(curSTE),
	//					FullSTEManipulater.getMethodSig(curSTE));
	//			SootMethod secMethod = SootAgent4Pecan.getMethod(
	//					FullSTEManipulater.getClass(secSTE),
	//					FullSTEManipulater.getMethodSig(secSTE));
	//			if (secMethod == null) {
	//				System.out.println("xxx");
	//				SootAgent4Pecan.getMethod(FullSTEManipulater.getClass(secSTE),
	//						FullSTEManipulater.getMethodSig(secSTE));
	//			}
	//			Unit unit = SootAgent4Pecan.getInvokeUnit(secMethod,
	//					FullSTEManipulater.getInvokeLine(secSTE), curMethod);
	//			ContextMethod pair = new ContextMethod(curMethod.getSignature(),
	//					unit.toString(), FullSTEManipulater.getInvokeLine(secSTE));
	//			toret.add(pair);
	//
	//		}
	//		// for handling the size-1 index, including the case size ==1, which is
	//		// missed by the abvoe for loop.
	//		String curSTE = (String) tmpVector.get(tmpVector.size() - 1);// the last
	//																		// one
	//		SootMethod curMethod = SootAgent4Pecan.getMethod(
	//				FullSTEManipulater.getClass(curSTE),
	//				FullSTEManipulater.getMethodSig(curSTE));
	//		Stmt tmpStmt = method2UniqueFakeStmt.get(curMethod);
	//		if (tmpStmt == null)// only when no one is present, uniqueness
	//		{
	//			InvokeExpr ie = null;
	//			if (curMethod.isStatic()) {
	//				ie = Jimple.v().newStaticInvokeExpr(curMethod.makeRef());// otherwise,
	//																			// wrong
	//																			// staticness
	//			} else {
	//				ie = Jimple.v().newSpecialInvokeExpr(jvmInstance,
	//						curMethod.makeRef());
	//			}
	//			tmpStmt = Jimple.v().newInvokeStmt(ie);
	//			method2UniqueFakeStmt.put(curMethod, tmpStmt);
	//		}
	//		ContextMethod pair = new ContextMethod(curMethod.getSignature(),
	//				tmpStmt.toString(), -1);// called by
	//										// jvm
	//		toret.add(pair);
	//		return toret;
	//	}

	public static Vector translate2StringV(Vector intv) {
		Vector tmpVector = new Vector();
		for (int i = 0; i < intv.size(); i++) {
			Integer integer = (Integer) intv.get(i);
			String ctxtString = ctxts.get(integer.intValue());
			tmpVector.add(ctxtString);
		}
		return tmpVector;
	}

	public static long vector2long(Vector vv) { // 325
		long tmp = 0;
		for (int i = 0; i < vv.size(); i++) {
			int value = ((Integer) vv.get(i)).intValue();
			if (tmp * base + value > Long.MAX_VALUE) {
				return tmp;// cut from the middle!!! at least we preserve the
							// bottom entries of the vector.
			}
			tmp = tmp * base;
			tmp += value;

		}
		return tmp;
	}

	public static Vector getSTEcontext(RWNode pnode) {
		Vector stringV = new Vector();
		Vector intv = pnode.getIntContext();
		for (int i = 0; i < intv.size(); i++) {
			Integer integer = (Integer) intv.get(i);
			String ctxtString = ctxts.get(integer.intValue());
			stringV.add(ctxtString);
		}
		return stringV;
	}

}
