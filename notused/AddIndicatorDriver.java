package incorrect.compositions.prediction;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;



import Drivers.Setup;
import Drivers.Utils;
import Drivers.DirectedGraphToDotGraph.DotNamer;
import soot.Body;
import soot.BodyTransformer;
import soot.EntryPoints;
import soot.G;
import soot.Modifier;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.RValueBox;
import soot.jimple.paddle.PaddleContextSensitiveCallGraph;
import soot.jimple.toolkits.visitor.RecursiveVisitor;
import soot.jimple.toolkits.visitor.Visitor;
import soot.jimple.toolkits.visitor.VisitorForActiveTesting;
import soot.jimple.toolkits.visitor.VisitorForPrinting;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.ZonedBlockGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.LoopedPDGNode;
import soot.toolkits.graph.pdg.PDGNode;
import soot.toolkits.graph.pdg.PDGRegion;
import soot.toolkits.graph.pdg.RegionAnalysis;
import soot.util.Chain;

public class AddIndicatorDriver {


	 
   
	public static String syncKeyWord = "entermonitor";
	public static BufferedWriter bWriter = null;

	public static void main(String[] args) throws IOException { // wjtp.tn
		// @link LockProducer
        String argsOfToy2 = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin:/home/lpxz/eclipse/workspace/soot24/bin Toy2";// soot.jimple.toolkits.thread.synchronizationLP.Jimples.HelloWorld"; // java.lang.Math
		String argsOfToyW = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin ToyBlocks"; // java.lang.Math
		String argsOfJimpleHelloWorld = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin:/home/lpxz/eclipse/workspace/soot24/bin soot.jimple.toolkits.thread.synchronizationLP.Jimples.HelloWorld"; // java.lang.Math
		String argsOfpaddleJar = "-f J -pp -cp /home/lpxz/eclipse/workspace/soot24/paddlePublic/ -process-dir /home/lpxz/eclipse/workspace/soot24/paddlePublic/"; // java.lang.Math
		String argsOfmtrt = "-f c -pp -cp /home/lpxz/eclipse/workspace/APIDesigner/bin -main-class Preprocess.Test Preprocess.Test"; // java.lang.Math
		
		// do not use the jar directly,
		// unzip it to a folder, and parse the folder like above.
		// /home/lpxz/javapool/jdk1.3.1_20/jre/lib/rt.jar

		int benchmark =-1;
		String interString = "";
		switch (benchmark) {
		case 0: 
			interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Test"; // java.lang.Math
	      break;
        case 1:  
        	interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Bayes.Learner"; // java.lang.Math
		      break;
        case 2:	     
        	interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Genome.Genome"; // java.lang.Math
		      break;
        case 3:
        	interString =  "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Intruder.Intruder"; // java.lang.Math
		      break;
        case 4:
        	interString ="-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app KMeans.KMeans"; // java.lang.Math
	          break;
        case 5: 
        	interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Labyrinth3D.Labyrinth"; // java.lang.Math
	     	  break ;
        case 6:
        	interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app MatrixMultiply.MatrixMultiply"; // java.lang.Math
		      break;
        case 7:
			 interString ="-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app SSCA2.SSCA2"; // java.lang.Math
	    	  break; 
        case 8:
        	 interString= "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Vacation.Vacation"; // java.lang.Math
			  break;
        case 9:
        	 interString="-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Yada.java.yada"; // java.lang.Math
			  break;
	    default:
	    	System.out.println("what benchmarks to process?");
		}
			
			
		
		
		String[] finalArgs = argsOfmtrt.split(" ");
		soot.Main.v().processCmdLine(finalArgs);
		
	
		
		List excludesList= new ArrayList();
		excludesList.add("jrockit.");
		excludesList.add("com.bea.jrockit");
		excludesList.add("sun.");
		Options.v().set_exclude(excludesList);
		
	///	Options.v().set_output_dir("paddle_public.jar");
	//	Options.v().set_output_jar(true);// same as the name as the outdir (actually substitue it), so must be set.
		
		
		Scene.v().loadNecessaryClasses();
		// Setup.setPhaseOptionsForPaddleWork();
//		Setup.setPhaseOptionsForSparkWork();



		
		 
		Pack jtp = PackManager.v().getPack("jtp");
		addgetPDGPackToJtp(jtp);
		// Pack wjtp = PackManager.v().getPack("wjtp");
		// addVisitorToWjtp(wjtp);

		PackManager.v().runPacks();// 1
		PackManager.v().writeOutput();
		G.reset();
	}

	// List entryPoints=EntryPoints.v().methodsOfApplicationClasses();
	// List mainEntries = new ArrayList();
	// for(int i=0;i< entryPoints.size(); i++)
	// {
	// if(entryPoints.get(i).toString().contains("main"))
	// {TestDominationOfCS
	// mainEntries.add(entryPoints.get(i));
	// }
	// }
	//
	// soot.Scene.v().setEntryPoints(mainEntries); // process : app and its
	// reachable methods.

	private static void addgetPDGPackToJtp(Pack jtp) {
// the implementation is too fuzzy and the interface is really bad. switch to indus.
		// hope it works, I do not want to move to wala now!!
		jtp.add(new Transform("jtp.getPDG", new AddIndicatorTransformer()));
		}

	

	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main)
			Scene.v().setMainClass(c);
		return c;
	}

	public void testGetShimpleBody() {
		// fail("Not yet implemented"); // TODO
	}

}
