package incorrectCompositions.detection.onlineMonitoring.addMonitor;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

public class JTPTransformer extends BodyTransformer {
	private BaseVisitor		visitor;

	public static boolean	isMethodStaticNonPara	= false;
	public static boolean	isInsideConstructor		= false;
	public static boolean	isMethodSynchronized	= false;
	public static boolean	isMethodMain			= false;
	public static boolean	isMethodRunnable		= false;

	public JTPTransformer() {
		DispatchVisitor vv = new DispatchVisitor(null);
		ExtendedVisitor pv = new ExtendedVisitor(vv);
		vv.setNextVisitor(pv);
		visitor = pv;
	}

	protected void internalTransform(Body body, String pn, Map map) {

		AddMonitorUtil.resetParameters();
		SootMethod thisMethod = body.getMethod();

		String smname = thisMethod.getName();
		if (smname.contains("<clinit>")) {
			return;
		}

		if (smname.contains("<init>")) {
			JTPTransformer.isInsideConstructor = true;//we do not consider the fields in constructors.!!
		} else {
			JTPTransformer.isInsideConstructor = false;
		}

		SootClass thisClass = thisMethod.getDeclaringClass();
		String scname = thisClass.getName();

		if (!AddMonitorUtil.shouldInstruThisClass(scname))
			return;

		if (thisMethod.toString().contains("void main(java.lang.String[])")) {
			JTPTransformer.isMethodMain = true;
		} else if (thisMethod.toString().contains("void run()")
				&& AddMonitorUtil.isRunnableSubType(thisClass)) {
			JTPTransformer.isMethodRunnable = true;
		}
		if (thisMethod.isSynchronized()) {
			JTPTransformer.isMethodSynchronized = true;
		}

		Chain units = body.getUnits();

		if (thisMethod.isStatic() && thisMethod.getParameterCount() == 0) {
			JTPTransformer.isMethodStaticNonPara = true;
			Stmt nop = Jimple.v().newNopStmt();
			units.insertBefore(nop, units.getFirst());
		}
		//		System.err.println(body.getMethod());

		//		System.err.println(body);

		Iterator stmtIt = units.snapshotIterator();
		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();

			visitor.visitStmt(thisMethod, units, s);
		}

		//insert after the identityStmt. who appears first in the insertion, who appears the last in the code.
		//do not distinguish the private or public methods, all are treated as public.
		BaseVisitor.addCallMethodEntry(thisMethod, units); // 1 common method
		if (JTPTransformer.isMethodMain)
			BaseVisitor.addCallMainMethodEnterInsert(thisMethod, units); //2 main method
		else if (JTPTransformer.isMethodRunnable)
			BaseVisitor.addCallRunMethodEnterInsert(thisMethod, units); //3 run method

		if (JTPTransformer.isMethodSynchronized) {
			BaseVisitor.addCallMonitorEntry(body); //4 synchronized method
		}

	}

}
