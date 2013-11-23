package atomicCompositions.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.Body;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.Stmt;
import utils.SootHelper;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.summaries.SummarizedMethod;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.strings.Atom;
import com.ibm.wala.util.strings.StringStuff;

public class SootWalaBridge {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static Set<Unit> fromWalaToSoot(Body bb, Collection<Statement> slice) {
		Set<Unit> ret = new HashSet<Unit>();
		for (Statement statement : slice) {
			if ((statement instanceof NormalStatement)) {
				NormalStatement normalStatement = (NormalStatement) statement;
				if (normalStatement.getInstruction() instanceof SSAInvokeInstruction) {
					Unit sootUnit = fromWalaToSoot(bb, statement);// statement may not be in the bb directly.
					if (sootUnit != null)
						ret.add(sootUnit);
				}
			}

		}

		return ret;
	}

	private static Unit fromWalaToSoot(Body bb, Statement statement) {
		if (!(statement instanceof NormalStatement))
			throw new RuntimeException("we currently analyze only invokes.");
		if (!(((NormalStatement) statement).getInstruction() instanceof SSAInvokeInstruction)) {
			throw new RuntimeException("we currently analyze only invokes.");
		}
		SSAInvokeInstruction ssaInvokeInstruction = (SSAInvokeInstruction) ((NormalStatement) statement)
				.getInstruction();

		if (statement.getNode().getMethod() instanceof SummarizedMethod)
			return null; // we are not interested in the invocation of the summarized/stub system methods (such as arraycopy.). 
		// besides, it makes the statement.getNode() work incorrectly. Normally, getNode() returns the container method.
		// but for the invocation of a summarized method, getNode() returns the invoked method.

		CGNode n = findMethod(WalaSlicerTransformer.cg, bb.getMethod());// statement may not belong to bb directly, (wala slicing feature).
		if (n != statement.getNode()) {
			return null; // we ignore the sliced statements that are out of the current method.
		}
		for (Unit unit : bb.getUnits()) {
			Stmt stmt = (Stmt) unit;
			if (equivalentStmt(ssaInvokeInstruction, stmt, statement.getNode(),
					bb))
				return stmt;
		}

		return null;
	}

	public static CGNode findMethod(CallGraph cg, SootMethod sootMethod) {
		//		System.err.println(sootMethod);

		for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
			CGNode n = it.next();
			//			System.out.println(n.getMethod().toString());

			IMethod walamethod = n.getMethod();
			if (equivalentMethod(walamethod.getReference(), sootMethod))
				return n;

		}
		throw new RuntimeException("cannot find the wala method.");
	}

	// DO not use walamethod.getNumberOfParameters() as it will implicitly add 1 for "this" argument.
	// walamethodReferenc.getNumberOfParameters() is correct according to soot's expectation.
	public static boolean equivalentMethod(MethodReference walamethodreference,
			SootMethod sootMethod) {
		String walaClassPlain = StringStuff
				.jvmToReadableType(walamethodreference.getDeclaringClass()
						.getName().toString()); // dollar to dot
		String sootClassPlain = sootMethod.getDeclaringClass().getName()
				.replace("$", ".");
		boolean sameClassname = walaClassPlain.equals(sootClassPlain);
		if (sameClassname) {
			boolean sameMethodName = walamethodreference.getName().toString()
					.equals(sootMethod.getName());
			if (sameMethodName) {
				//check arg.  
				if (walamethodreference.getNumberOfParameters() == sootMethod
						.getParameterCount()) {
					boolean allParaSameType = true;
					for (int i = 0; i < walamethodreference
							.getNumberOfParameters(); i++) {
						TypeReference walaParaI = walamethodreference
								.getParameterType(i);
						Type sootParaI = sootMethod.getParameterType(i);
						String walaParaIString = StringStuff
								.jvmToReadableType(walaParaI.getName()
										.toString());
						String sootParaIString = sootParaI.toString().replace(
								"$", ".");
						if (!walaParaIString.equals(sootParaIString)) {
							allParaSameType = false;
						}

					}
					if (allParaSameType)// finally.
						return true;

				}

			}

		}

		return false;
	}

	//// IMPORTANT: do not use the following api, it is buggy.
	//		SlicerTest.findMethod(WalaSlicerTransformer.cg, bb.getMethod() 
	//				.getName());// 
	public static Statement fromSoottoWala(Body bb, Stmt unit) {
		if (!WalaSlicerTransformer.isWalaPrepared()
				|| !unit.containsInvokeExpr())
			throw new RuntimeException(
					"we expect that you set up the wala and analyze only the invokes");

		CGNode n = findMethod(WalaSlicerTransformer.cg, bb.getMethod());// .

		IR ir = n.getIR();
		for (Iterator<SSAInstruction> it = ir.iterateAllInstructions(); it
				.hasNext();) {
			SSAInstruction s = it.next();
			if (s instanceof SSAInvokeInstruction) {
				SSAInvokeInstruction call = (SSAInvokeInstruction) s;
				boolean equivalent = equivalentStmt(call, unit, n, bb);// in this context, containerNode(call)=n.
				if (equivalent) {
					IntSet indices = ir
							.getCallInstructionIndices(((SSAInvokeInstruction) s)
									.getCallSite());
					Assertions.productionAssertion(indices.size() == 1,
							"expected 1 but got " + indices.size());
					return new NormalStatement(n, indices.intIterator().next());
				}
			}
		}

		throw new RuntimeException(
				"cannot find the wala counterpart? maybe the line No constraint is too strict?");
	}

	private static boolean equivalentStmt(SSAInvokeInstruction call, Stmt unit,
			CGNode n, Body bb) {
		if (!unit.containsInvokeExpr())
			return false; // invoke!= non-invoke.
		int lineNo = SootHelper.getLine(unit);
		SootMethod calleeMethod = unit.getInvokeExpr().getMethod();

		boolean lineSame = false;
		boolean calleeMethodSameName = false;

		if (lineNo == WalaSlicerTransformer.getLineNo(n, call))// maybe weaken this condition?
			lineSame = true;
		if (equivalentMethod(call.getCallSite().getDeclaredTarget(),
				calleeMethod))
			calleeMethodSameName = true;

		return lineSame && calleeMethodSameName;
	}
}
