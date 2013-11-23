package incorrect.compositions.prediction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Drivers.Utils;
import Drivers.DirectedGraphToDotGraph.DotNamer;
import soot.Body;
import soot.BodyTransformer;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.PDGNode;


public class AddIndicatorTransformer extends BodyTransformer{
		@Override
		protected void internalTransform(Body b, String phaseName,
				Map options) {
			SootMethod  sm =b.getMethod();
            if(sm.getName().equals("xxx"))
            	System.out.println("xxx");
            else {
				return;
			}
            
            SootClass motherClass = b.getMethod().getDeclaringClass();   		
    		String nameString = "LPXZ_" + motherClass.getName().replace("$", "").replace(".", "_");
    		
    		SootField sootField = null;
    		if(!motherClass.declaresFieldByName(nameString)) //insert the field only once
    		 sootField= addField2Class(nameString,motherClass );
    		else
    			sootField=motherClass.getFieldByName(nameString);    		
    		
    		
    		addFieldAssign2Method(sootField, b);			
	    }

		private void addFieldAssign2Method(SootField sootField, Body b) {
			Stmt firstStmt = b.getFirstNonIdentityStmt();
			
	   		PatchingChain<Unit> units = b.getUnits();

	    	
    		Local localVar = Jimple.v().newLocal(sootField.getName()+ b.getMethod().getName(),
    				IntType.v());
    		;

    		if(!b.getLocals().contains(localVar))
    		{
    			b.getLocals().add(localVar);
    		}else
    		{
    			return ; // already add local and insert.
    		}
    		
    		
    		// assign new object to lock obj
    		Stmt newStmt = Jimple.v().newAssignStmt(localVar, IntConstant.v(5));
    		units.insertBefore(newStmt,firstStmt );
    	

    		Stmt newStmt2 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(sootField.makeRef()),
    				localVar);
    		units.insertBefore(newStmt2,firstStmt);			
    		
			
			
		}

		private SootField addField2Class(String nameString, SootClass motherClass) {
			SootField globalLockObj = null;
	   		try {
    			globalLockObj = motherClass.getFieldByName(nameString
    					);
    			// field already exists
    		} catch (RuntimeException re) {
    			// field does not yet exist (or, as a pre-existing error, there is
    			// more than one field by this name)
    			globalLockObj = new SootField(nameString, IntType.v(), Modifier.STATIC | Modifier.PUBLIC);
    			motherClass.addField(globalLockObj);
    		}
    		
//    		SootMethod enforceM = null;
//    		Body clinitBody =null;
//    		Unit firstStmt =null;
//    		boolean addingNewClinit = !motherClass.declaresMethod("void <clinit>()");
//    		if (addingNewClinit) {
//    			enforceM = new SootMethod("<clinit>", new ArrayList(), VoidType
//    					.v(), Modifier.PUBLIC | Modifier.STATIC);
//    			clinitBody = Jimple.v().newBody(enforceM);
//    			enforceM.setActiveBody(clinitBody);
//    			motherClass.addMethod(enforceM);
//    			clinitBody.getUnits().add(Jimple.v().newReturnVoidStmt());
//    			firstStmt = clinitBody.getFirstNonIdentityStmt();// no non-id stmt
//    			 if(firstStmt instanceof JReturnStmt || firstStmt instanceof JReturnVoidStmt)
//    			 {// the empty body incurs problems during the lock injection.
//    				 Stmt nop=Jimple.v().newNopStmt();
//    				 clinitBody.getUnits().insertBefore(nop, firstStmt);									 
//    			 }	
//    		} else {
//    			enforceM = motherClass.getMethod("void <clinit>()");
//    			if(!enforceM.hasActiveBody())
//    				enforceM.retrieveActiveBody();
//    			clinitBody = (JimpleBody) enforceM.getActiveBody();
//    			firstStmt = clinitBody.getFirstNonIdentityStmt();
//    		}
//    		PatchingChain<Unit> clinitUnits = clinitBody.getUnits();
//
//    	
//    		Local ctxtListLocal = Jimple.v().newLocal("clinit_local",
//    				IntType.v());
//    		;
//
//    		if(!clinitBody.getLocals().contains(ctxtListLocal))
//    		{
//    			clinitBody.getLocals().add(ctxtListLocal);
//    		}   		
//    		
//    		// assign new object to lock obj
//    		Stmt newStmt = Jimple.v().newAssignStmt(ctxtListLocal, IntConstant.v(0));
//    		clinitUnits.insertBefore(newStmt,firstStmt );
//
//
//    		Stmt newStmt2 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(globalLockObj.makeRef()),
//    				ctxtListLocal);
//    		clinitUnits.insertBefore(newStmt2,firstStmt);	
    		
    		return globalLockObj;
		}
		

		
	}
	

