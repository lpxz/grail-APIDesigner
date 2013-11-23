package edu.hkust.clap.monitor;
import java.io.BufferedWriter;
import java.io.File;
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

public class AddAtomIntentionDriver {

    public static String syncKeyWord = "entermonitor";

    public static BufferedWriter bWriter = null;

    public static SootClass throwableClass = null;

    public static void main(String[] args) throws IOException {
        String argsOfToy2 = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin:/home/lpxz/eclipse/workspace/soot24/bin Toy2";
        String argsOfToyW = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin ToyBlocks";
        String argsOfJimpleHelloWorld = "-f J -pp -cp /home/lpxz/eclipse/workspace/Playground/bin:/home/lpxz/eclipse/workspace/soot24/bin soot.jimple.toolkits.thread.synchronizationLP.Jimples.HelloWorld";
        String argsOfpaddleJar = "-f J -pp -cp /home/lpxz/eclipse/workspace/soot24/paddlePublic/ -process-dir /home/lpxz/eclipse/workspace/soot24/paddlePublic/";
        String argsOfmtrt = "-f c -pp -cp /home/lpxz/pool/jdk1.6.0_13/jre/lib/jsse.jar:/home/lpxz/eclipse/workspace/specjbb2005/bin -process-dir /home/lpxz/eclipse/workspace/specjbb2005/bin";
        int benchmark = -1;
        String interString = "";
        switch(benchmark) {
            case 0:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Test";
                break;
            case 1:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Bayes.Learner";
                break;
            case 2:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Genome.Genome";
                break;
            case 3:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Intruder.Intruder";
                break;
            case 4:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app KMeans.KMeans";
                break;
            case 5:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Labyrinth3D.Labyrinth";
                break;
            case 6:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app MatrixMultiply.MatrixMultiply";
                break;
            case 7:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app SSCA2.SSCA2";
                break;
            case 8:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Vacation.Vacation";
                break;
            case 9:
                interString = "-f J -pp -cp /home/lpxz/eclipse/workspace/jstamp/bin --app Yada.java.yada";
                break;
            default:
                System.out.println("what benchmarks to process?");
        }
        String[] finalArgs = argsOfmtrt.split(" ");
        List excludesList = new ArrayList();
        excludesList.add("jrockit.");
        excludesList.add("com.bea.jrockit");
        excludesList.add("sun.");
        Options.v().set_exclude(excludesList);
        Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path") + File.pathSeparator + System.getProperty("java.class.path"));
        throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
        Scene.v().setSootClassPath(null);
        Pack jtp = PackManager.v().getPack("jtp");
        addgetPDGPackToJtp(jtp);
        soot.Main.main(finalArgs);
        G.reset();
    }

    private static void addgetPDGPackToJtp(Pack jtp) {
        jtp.add(new Transform("jtp.getPDG", new AddAtomIntentionTransformer()));
    }

    private static SootClass loadClass(String name, boolean main) {
        SootClass c = Scene.v().loadClassAndSupport(name);
        c.setApplicationClass();
        if (main) Scene.v().setMainClass(c);
        return c;
    }

    public void testGetShimpleBody() {
    }
}
