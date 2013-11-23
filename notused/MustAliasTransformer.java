package atomicCompositions.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;
import soot.jimple.toolkits.pointer.StrongLocalMustAliasAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;

public class MustAliasTransformer extends BodyTransformer {
	HashMap<SootClass, HashSet<Stmt>>	classLib2Invokes_inSingleBody	= new HashMap<SootClass, HashSet<Stmt>>();

	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		SootMethod sm = b.getMethod();

		Body bb = sm.getActiveBody();
		mapClassLib2Invokes(bb, classLib2Invokes_inSingleBody);

		Iterator<SootClass> keyIt = classLib2Invokes_inSingleBody.keySet()
				.iterator();
		while (keyIt.hasNext()) {
			SootClass sootClass = (SootClass) keyIt.next();
			HashSet<Stmt> stmts = classLib2Invokes_inSingleBody.get(sootClass);
			for (Stmt i : stmts) {
				for (Stmt j : stmts) {
					if (i != j) {
						if (checkMustAlias(bb, i, j))
							System.out.println(" must alias");
					}
				}
			}
		}
	}

	HashMap<Body, LocalMustAliasAnalysis>	cache_lma	= new HashMap<Body, LocalMustAliasAnalysis>();

	public LocalMustAliasAnalysis createLocalMustAnalysis(Body bb) {
		LocalMustAliasAnalysis lma = cache_lma.get(bb);
		if (lma != null)
			return lma;
		else {
			lma = new StrongLocalMustAliasAnalysis(new EnhancedUnitGraph(bb));
			;
			cache_lma.put(bb, lma);
			return lma;
		}

	}

	private void mapClassLib2Invokes(Body bb,
			HashMap<SootClass, HashSet<Stmt>> classLib2Invokes_inSingleCS2) {
		Iterator<Unit> unitIt = bb.getUnits().iterator();
		while (unitIt.hasNext()) {
			Unit unit = (Unit) unitIt.next();
			Stmt stmt = (Stmt) unit;
			if (stmt.containsInvokeExpr()) {
				SootClass sc = stmt.getInvokeExpr().getMethod()
						.getDeclaringClass();
				putCollectively(classLib2Invokes_inSingleCS2, sc, stmt);
			}
		}
	}

	private void putCollectively(
			HashMap<SootClass, HashSet<Stmt>> classLib2Invokes_inSingleCS2,
			SootClass sc, Stmt stmt) {
		HashSet<Stmt> stmts = classLib2Invokes_inSingleCS2.get(sc);
		if (stmts == null) {
			stmts = new HashSet<Stmt>();
			classLib2Invokes_inSingleCS2.put(sc, stmts);
		}
		stmts.add(stmt);
	}

	public boolean checkMustAlias(Body bb, Stmt i, Stmt j) {

		if (i.getInvokeExpr() instanceof InstanceInvokeExpr
				&& j.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr insti = (InstanceInvokeExpr) i.getInvokeExpr();
			InstanceInvokeExpr instj = (InstanceInvokeExpr) j.getInvokeExpr();
			if (insti.toString().contains("void <init>")
					|| instj.toString().contains("void <init>"))
				return false;
			Local basei = (Local) insti.getBase();
			Local basej = (Local) instj.getBase();
			LocalMustAliasAnalysis lma = createLocalMustAnalysis(bb);
			boolean yes = lma.mustAlias(basei, i, basej, j);
			return yes;
		}
		return false;
	}

}
