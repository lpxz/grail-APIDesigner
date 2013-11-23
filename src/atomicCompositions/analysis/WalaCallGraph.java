package atomicCompositions.analysis;

import java.util.HashSet;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Statement.Kind;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.strings.StringStuff;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;

public class WalaCallGraph {

	// the library is not used as a standalone.

	static Set<SootMethod>	ret	= new HashSet<SootMethod>();

	public static Set<SootMethod> getCalleeMethods(Body bb, Stmt unitStmt) {
		ret.clear();
		if (WalaSlicerTransformer.cg == null)
			return ret;

		CGNode container = SootWalaBridge.findMethod(WalaSlicerTransformer.cg,
				bb.getMethod());// .

		Statement statement = SootWalaBridge.fromSoottoWala(bb, unitStmt);

		if (statement.getKind() == Kind.NORMAL) {
			NormalStatement n = (NormalStatement) statement;
			SSAInstruction st = n.getInstruction();
			if (st instanceof SSAInvokeInstruction) {
				SSAAbstractInvokeInstruction call = (SSAAbstractInvokeInstruction) st;
				Set<CGNode> targets = WalaSlicerTransformer.cg
						.getPossibleTargets(container, call.getCallSite());
				for (CGNode target : targets) {
					TypeName targetType = target.getMethod()
							.getDeclaringClass().getName();
					SootClass sc = Scene.v().getSootClass(
							StringStuff.jvmToBinaryName(targetType.toString()));
					for (SootMethod sootMethod : sc.getMethods()) {
						if (SootWalaBridge.equivalentMethod(target.getMethod()
								.getReference(), sootMethod)) {
							ret.add(sootMethod);
						}
					}

				}

			}
		}
		return ret;
	}
}
