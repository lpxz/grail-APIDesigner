package atomicCompositions.analysis;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;


public class DDGTransformer extends BodyTransformer{
		@Override
		protected void internalTransform(Body b, String phaseName,
				Map options) {
			SootMethod  sm =b.getMethod();
			if(!b.getMethod().getName().equals("init")) return;

			Body bb =sm.getActiveBody();
			System.out.println(bb);
			SimpleLocalUses slu  =createLocalUses(bb);
		    	       
            
            Iterator<Unit> unitIt =bb.getUnits().iterator();
            while (unitIt.hasNext()) {
				Unit unit = (Unit) unitIt.next();
				Stmt stmt = (Stmt)unit;
				if(!stmt.containsInvokeExpr())
				{
					continue;
				}
				
				//if(unit.toString().contains("exitmonitor"))
				{
					Set<Unit> units = getDataDependents(slu, unit);
					System.out.println("given the unit:" + unit);
					System.out.println("the data dependents are:");
					for(Unit unititem :units)
					{
						System.out.println(unititem);
					}
					System.out.println(" \n" );
					
				}
			}
	    }

		HashMap<Body, SimpleLocalUses> cache_clu = new HashMap<Body, SimpleLocalUses>();
		public SimpleLocalUses createLocalUses(Body bb) {
			SimpleLocalUses toret =cache_clu.get(bb);
			if(toret !=null) return toret;
			else {
				UnitGraph ug = new EnhancedUnitGraph(bb);//brief: multiple heads. exceptional: wrong			   
				toret=  new SimpleLocalUses(ug, new SmartLocalDefs(ug, new SimpleLiveLocals(ug)));  
				cache_clu.put(bb, toret);
				return toret;
			}
			 
		}

		HashSet<Unit> toret_ddg = new HashSet<Unit>();
		public Set<Unit> getDataDependents(SimpleLocalUses slu, Unit unit) {	
			toret_ddg.clear();
			
			Iterator iterator = slu.getUsesOf(unit).iterator();
			while (iterator.hasNext()) {
				UnitValueBoxPair pair = (UnitValueBoxPair) iterator.next();
				toret_ddg.add(pair.getUnit()); 				
			}				
			return toret_ddg;
		}
		
		
		
	}

