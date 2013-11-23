package incorrectCompositions.detection.onlineMonitoring.addMonitor;

import incorrectCompositions.detection.onlineMonitoring.addMonitor.contexts.InvokeContext;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.contexts.RHSContextImpl;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.contexts.RefContext;
import soot.ArrayType;
import soot.Body;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;
import utils.CentralConstants;

public class ExtendedVisitor extends BaseVisitor {

	public ExtendedVisitor(BaseVisitor visitor) {
		super(visitor);
	}

	public void visitStmtAssign(SootMethod sm, Chain units,
			AssignStmt assignStmt) {
		nextVisitor.visitStmtAssign(sm, units, assignStmt);
	}

	public void visitStmtEnterMonitor(SootMethod sm, Chain units,
			EnterMonitorStmt enterMonitorStmt) {
		BaseVisitor.totalaccessnum++;
		BaseVisitor.instrusharedaccessnum++;

		Value op = enterMonitorStmt.getOp();
		///sync(A.class){}   sync static method(){ }, consistent!

		Type type = op.getType();
		String sig = type.toString();
		Value memory = StringConstant.v(sig);

		BaseVisitor.addCallAccessSyncObjInstance(sm, units, enterMonitorStmt,
				"enterMonitorAfter", op, memory, false);

		nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
	}

	public void visitStmtExitMonitor(SootMethod sm, Chain units,
			ExitMonitorStmt exitMonitorStmt) {
		BaseVisitor.totalaccessnum++;
		BaseVisitor.instrusharedaccessnum++;

		Value op = exitMonitorStmt.getOp();
		Type type = op.getType();
		String sig = type.toString();
		Value memory = StringConstant.v(sig);

		BaseVisitor.addCallAccessSyncObjInstance(sm, units, exitMonitorStmt,
				"exitMonitorBefore", op, memory, true);

		nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
	}

	public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s,
			InstanceInvokeExpr invokeExpr, InvokeContext context) {
		String sigclass = invokeExpr.getMethod().getDeclaringClass().getName();//+"."+invokeExpr.getMethod().getName();
		Value memory = StringConstant.v(sigclass);

		Value base = invokeExpr.getBase();
		String sig = invokeExpr.getMethod().getSubSignature();
		if (sig.equals("void wait()") || sig.equals("void wait(long)")
				|| sig.equals("void wait(long,int)")) {

			BaseVisitor.addCallAccessSyncObjInstance(sm, units, s, "waitAfter",
					base, memory, false);
			BaseVisitor.instrusharedaccessnum++;
			BaseVisitor.totalaccessnum++;

			//} else if (sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) {

		} else if (sig.equals("void notify()")) {
			BaseVisitor.addCallAccessSyncObjInstance(sm, units, s,
					"notifyBefore", base, memory, true);
			BaseVisitor.instrusharedaccessnum++;
			BaseVisitor.totalaccessnum++;

		} else if (sig.equals("void notifyAll()")) {
			BaseVisitor.addCallAccessSyncObjInstance(sm, units, s,
					"notifyAllBefore", base, memory, true);
			BaseVisitor.instrusharedaccessnum++;
			BaseVisitor.totalaccessnum++;

		} else if (sig.equals("void start()")
				&& isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
			BaseVisitor.addCallstartRunThreadBefore(sm, units, s,
					"startRunThreadBefore", invokeExpr.getBase());
		} else if ((sig.equals("void join()") || sig.equals("void join(long)") || sig
				.equals("void join(long,int)"))
				&& isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
			BaseVisitor.addCallJoinRunThreadAfter(sm, units, s,
					"joinRunThreadAfter", invokeExpr.getBase());
		} else if (invokeExpr.getMethod().isSynchronized()) {
			if (true)
				return;

			//            Visitor.addCallAccessSyncObj(sm,units, s, "enterSyncMethodBefore", memory, true);
			//        	Visitor.instrusharedaccessnum++;
			//        	Visitor.totalaccessnum++;
			//        	
			//            Visitor.addCallAccessSyncObj(sm,units, s, "exitSyncMethodAfter", memory, false);
			//        	Visitor.instrusharedaccessnum++;
			//        	Visitor.totalaccessnum++;
		}

		nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s,
			StaticInvokeExpr invokeExpr, InvokeContext context) {

		if (true)
			return;//TODO check it.

		SootClass appClass = invokeExpr.getMethod().getDeclaringClass();
		//SootMethod appMethod = invokeExpr.getMethod();
		//appMethod.setModifiers(appMethod.getModifiers()&~Modifier.SYNCHRONIZED);

		//appClass.isApplicationClass()
		String sig = appClass.getName() + ".STATIC.";//+"."+invokeExpr.getMethod().getName();

		if (invokeExpr.getMethod().isSynchronized()) {
			Value memory = StringConstant.v(sig);

			BaseVisitor.addCallAccessSPE(sm, units, s, "enterSyncMethodBefore",
					memory, true);
			BaseVisitor.instrusharedaccessnum++;
			BaseVisitor.totalaccessnum++;

			BaseVisitor.addCallAccessSPE(sm, units, s, "exitSyncMethodAfter",
					memory, false);
			BaseVisitor.instrusharedaccessnum++;
			BaseVisitor.totalaccessnum++;
		}

		nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitArrayRef(SootMethod sm, Chain units, Stmt s,
			ArrayRef arrayRef, RefContext context) {

		nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
	}

	public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s,
			InstanceFieldRef instanceFieldRef, RefContext context) {
		BaseVisitor.totalaccessnum++;

		Value base = instanceFieldRef.getBase();

		String sig = instanceFieldRef.getField().getDeclaringClass().getName()
				+ "." + instanceFieldRef.getField().getName();
		Value memory = StringConstant.v(sig);

		if (!instanceFieldRef.getField().isFinal()
				&& !JTPTransformer.isInsideConstructor) {
			//			if(sig.contains("HashIterator")&&sig.contains("modCount"))
			//				System.out.print(true);

			if (isMarkerField(sig))//Visitor.sharedVariableWriteAccessSet.contains(sig))
			{

				String methodname = "readBeforeInstance";

				if (context != RHSContextImpl.getInstance()) {
					methodname = "writeBeforeInstance";
				} else if (instanceFieldRef.getField().getType() instanceof ArrayType) {

					Stmt nextStmt = (Stmt) units.getSuccOf(s);
					if (s instanceof AssignStmt
							&& nextStmt instanceof AssignStmt) {
						AssignStmt assgnStmt = (AssignStmt) s;
						AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
						if (assgnNextStmt.getLeftOp().toString()
								.contains(assgnStmt.getLeftOp().toString())) {
							methodname = "writeBeforeInstance";
						}
					}
				}

				BaseVisitor.addCallAccessSPEInstance(sm, units, s, methodname,
						base, memory, true);
				BaseVisitor.instrusharedaccessnum++;
			}
		}
		nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef,
				context);
	}

	public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s,
			StaticFieldRef staticFieldRef, RefContext context) {
		BaseVisitor.totalaccessnum++;
		String sig = staticFieldRef.getField().getDeclaringClass().getName()
				+ "." + staticFieldRef.getField().getName();
		Value memory = StringConstant.v(sig);

		if (!staticFieldRef.getField().isFinal()
				&& !JTPTransformer.isInsideConstructor)
			if (isMarkerField(sig))//Visitor.sharedVariableWriteAccessSet.contains(sig))
			{
				String methodname = "readBeforeStatic";

				if (context != RHSContextImpl.getInstance()) {
					methodname = "writeBeforeStatic";
				} else if (staticFieldRef.getField().getType() instanceof ArrayType) {
					Stmt nextStmt = (Stmt) units.getSuccOf(s);
					if (s instanceof AssignStmt
							&& nextStmt instanceof AssignStmt) {
						AssignStmt assgnStmt = (AssignStmt) s;
						AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
						if (assgnNextStmt.getLeftOp().toString()
								.contains(assgnStmt.getLeftOp().toString())) {
							methodname = "writeBeforeStatic";
						}
					}
				}

				BaseVisitor.addCallAccessSPEStatic(sm, units, s, methodname,
						memory, true);
				BaseVisitor.instrusharedaccessnum++;
			}
		nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
	}

	private boolean isMarkerField(String sig) {// haha~
		//
		//		if (sig.contains("level") || sig.contains("salary"))
		//			return true;// domain knowledge. The marker fields all start with "AC_"
		//		else
		//			return false;
		//		

		if (sig.contains(CentralConstants.ACprefix))
			return true;// domain knowledge. The marker fields all start with "AC_"
		else
			return false;
	}
}
