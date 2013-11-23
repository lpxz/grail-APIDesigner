package atomicCompositions.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.omg.CORBA.portable.ValueBase;

import soot.Body;
import soot.BodyTransformer;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;

public class DeepDDGTransformer extends BodyTransformer {
	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		SootMethod sm = b.getMethod();

		Body bb = sm.getActiveBody();
		//System.out.println(bb);

		Iterator<Unit> unitIt = bb.getUnits().iterator();
		while (unitIt.hasNext()) {
			Unit unit = (Unit) unitIt.next();
			Stmt stmt = (Stmt) unit;
			if (!stmt.containsInvokeExpr()) {
				continue;
			}

			//if(unit.toString().contains("exitmonitor"))
			{
				Set<Unit> units = getDeepDataDependents(bb, unit);
				System.out.println("given the unit:" + unit);
				System.out.println("the data dependents are:");
				for (Unit unititem : units) {
					System.out.println(unititem);
				}
				System.out.println(" \n");

			}
		}
	}

	HashMap<Body, SimpleLocalUses>	cache_clu	= new HashMap<Body, SimpleLocalUses>();

	HashSet<Unit>					toret_ddg	= new HashSet<Unit>();

	public Set<Unit> getDeepDataDependents(Body bb, Unit unit) {
		toret_ddg.clear();
		Stmt unitStmt = (Stmt) unit;
		if (!unitStmt.containsInvokeExpr())
			return toret_ddg;
		InvokeExpr unitIe = unitStmt.getInvokeExpr();

		if (unitIe.getMethod().getName().contains(SootMethod.constructorName)
				|| unitIe.getMethod().getName()
						.contains(SootMethod.staticInitializerName))
			return toret_ddg;

		Set<SootMethod> targets = WalaCallGraph.getCalleeMethods(bb, unitStmt);
		targets.add(unitIe.getMethod());
		Set<SootField> defFields = getDefedFields(targets);

		Set<Unit> transitiveSuccs = getTransSuccs(bb, unit);
		for (Unit transiveSucc : transitiveSuccs) {
			Stmt transiveSuccStmt = (Stmt) transiveSucc;
			if (transiveSuccStmt == unitStmt)
				continue;
			if (!transiveSuccStmt.containsInvokeExpr())
				continue;

			InvokeExpr transiveSuccStmtIE = transiveSuccStmt.getInvokeExpr();
			if (transiveSuccStmtIE.getMethod().getName()
					.contains(SootMethod.constructorName)
					|| transiveSuccStmtIE.getMethod().getName()
							.contains(SootMethod.staticInitializerName))
				continue;

			if (transiveSuccStmtIE.getMethod().getDeclaringClass()
					.equals(unitIe.getMethod().getDeclaringClass())) {
				Set<SootMethod> succtargets = WalaCallGraph.getCalleeMethods(
						bb, unitStmt);
				succtargets.add(transiveSuccStmtIE.getMethod());
				Set<SootField> usedFields2 = getUsedFields(succtargets);

				boolean overlap = false;
				for (SootField usedField : usedFields2) {
					if (defFields.contains(usedField))
						overlap = true;
				}
				if (overlap)// change as a result.
				{
					toret_ddg.add(transiveSucc);
				}

			}

		}

		return toret_ddg;

	}

	public static Set<SootField>	tmp4getUsedFields	= new HashSet<SootField>();

	private Set<SootField> getUsedFields(Set<SootMethod> succtargets) {
		tmp4getUsedFields.clear();
		for (SootMethod succtarget : succtargets) {
			if (!succtarget.hasActiveBody())
				continue;
			tmp4getUsedFields.addAll(getUsedFields(succtarget.getActiveBody()));
		}
		return tmp4getUsedFields;
	}

	public static Set<SootField>	tmp4getDefedFields	= new HashSet<SootField>();

	private Set<SootField> getDefedFields(Set<SootMethod> targets) {
		tmp4getDefedFields.clear();
		for (SootMethod target : targets) {
			if (!target.hasActiveBody())
				continue;
			tmp4getDefedFields.addAll(getDefedFields(target.getActiveBody()));
		}

		return tmp4getDefedFields;
	}

	private Set<SootField> getUsedFields(Body bbInvoked) {
		Set<SootField> usedFields = new HashSet<SootField>();

		for (Unit unit : bbInvoked.getUnits()) {
			Stmt stmt = (Stmt) unit;
			if (stmt.containsFieldRef()) {
				if (!stmt.getFieldRef().getField().getDeclaringClass()
						.equals(bbInvoked.getMethod().getDeclaringClass()))
					continue;
				if (stmt instanceof AssignStmt) {
					int equals = stmt.toString().indexOf('=');
					String right = stmt.toString().substring(equals + 1);
					if (right
							.contains(stmt.getFieldRef().getField().toString())) {
						usedFields.add(stmt.getFieldRef().getField());

					}

				}
			}
		}

		return usedFields;

	}

	private Set<SootField> getDefedFields(Body bbInvoked) {
		Set<SootField> defedFields = new HashSet<SootField>();
		for (Unit unit : bbInvoked.getUnits()) {
			Stmt stmt = (Stmt) unit;
			if (stmt.containsFieldRef()) {
				if (!stmt.getFieldRef().getField().getDeclaringClass()
						.equals(bbInvoked.getMethod().getDeclaringClass()))
					continue;
				if (stmt instanceof AssignStmt) {
					int equals = stmt.toString().indexOf('=');
					String left = stmt.toString().substring(0, equals);
					if (left.contains(stmt.getFieldRef().getField().toString())) {
						defedFields.add(stmt.getFieldRef().getField());

					}

				}
			}
		}

		return defedFields;
	}

	HashSet<Unit>	transSuccs	= new HashSet<Unit>();
	Stack<Unit>		stack		= new Stack<Unit>();

	private Set<Unit> getTransSuccs(Body bb, Unit unit) {
		BriefUnitGraph bug = new BriefUnitGraph(bb);
		transSuccs.clear();// same as  visited.
		stack.clear();

		stack.push(unit);
		transSuccs.add(unit);
		while (!stack.isEmpty()) {
			Unit popped = stack.pop();
			List<Unit> succs = bug.getSuccsOf(popped);
			for (Unit succ : succs) {
				if (!transSuccs.contains(succ)) {
					stack.push(succ);
					transSuccs.add(succ);
				}
			}

		}

		return transSuccs;
	}

}
