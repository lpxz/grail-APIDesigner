package incorrect.compositions.prediction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pldi.locking.MethodLocker;

import Drivers.Utils;
import Drivers.DirectedGraphToDotGraph.DotNamer;
import soot.Body;
import soot.BodyTransformer;
import soot.Modifier;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.PDGNode;


public class AddAtomIntentionTransformer extends BodyTransformer{
		@Override
		protected void internalTransform(Body b, String phaseName,
				Map options) {
			SootMethod  sm =b.getMethod();
          
			Body bb =sm.getActiveBody();
			if(!sm.isSynchronized() && sm.hasActiveBody()) 
			{

				 Body body = sm.getActiveBody();
				 PatchingChain<Unit> units = body.getUnits();
				 Stmt firstNon =body.getFirstNonIdentityStmt();
				 if(firstNon instanceof JReturnStmt || firstNon instanceof JReturnVoidStmt)
				 {// the empty body incurs problems during the lock injection.
					 Stmt nop=Jimple.v().newNopStmt();
					 units.insertBefore(nop, firstNon);									 
				 }								 
				 MethodLocker.addlock(sm);
			}	
			bb.validate();
		}
	
	}
	

