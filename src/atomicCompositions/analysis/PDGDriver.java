package atomicCompositions.analysis;

import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;

import japa.parser.ast.visitor.SameBlockChecker;
import japa.parser.ast.visitor.SearchForClassFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.ibm.wala.examples.drivers.PDFSDG;

import atomicCompositions.datastructure.ClientViewOfLibrary;
import atomicCompositions.datastructure.Composition;

import soot.Body;
import soot.BodyTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.scalar.SimpleLocalUses;
import utils.CentralConstants;
import utils.SootHelper;

public class PDGDriver {

	public static String			syncKeyWord	= "entermonitor";
	public static BufferedWriter	bWriter		= null;

	public static void main(String[] args) throws Exception {
		Recompiler
				.javac("/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test");
		WalaSlicerTransformer.prepareSDG(
				"/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/bin",
				getWalaClassName("ClientClass"),
				PDFSDG.getDataDependenceOptions("no_base_no_heap"),
				PDFSDG.getControlDependenceOptions("no_exceptional_edges"));

		String givenArg = "-i org.apache -f J -pp -cp "
				+ "/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/bin"
				+ " -process-dir /Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/bin";

		String[] finalArgs = givenArg.split(" "); //args[0].split(" ");//
		soot.Main.v().processCmdLine(finalArgs);
		utils.ProjectDirectoryHelper.setProject_Dir_Name(finalArgs);
		utils.Options.setOptions();
		Finder.setSootOptions();
		Scene.v().loadNecessaryClasses();
		Pack jtp = PackManager.v().getPack("jtp");

		jtp.add(new Transform("jtp.ACfinder", new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phaseName,
					Map options) {
				SootMethod sm = b.getMethod();

				if (SootHelper.isConstructor(sm))
					return;
				Iterator ittt = b.getUnits().snapshotIterator();
				while (ittt.hasNext()) {
					Stmt stmt = (Stmt) ittt.next();
					if (!stmt.containsInvokeExpr())
						continue;
					Set<Unit> pdgChildren = WalaSlicerTransformer
							.getDependentStmts_final(b, stmt);
					if (pdgChildren.size() != 0) {
						System.err.println("the dependents of :" + stmt);
					}

					for (Unit child : pdgChildren) {
						System.err.println("child: " + child);

					}
				}

			}

			private int getLineNum(Host h) {
				if (h.hasTag("LineNumberTag")) {
					return ((LineNumberTag) h.getTag("LineNumberTag"))
							.getLineNumber();
				}
				if (h.hasTag("SourceLineNumberTag")) {
					return ((SourceLineNumberTag) h
							.getTag("SourceLineNumberTag")).getLineNumber();
				}
				if (h.hasTag("SourceLnPosTag")) {
					return ((SourceLnPosTag) h.getTag("SourceLnPosTag"))
							.startLn();
				}
				return -1;
			}

		}));

		PackManager.v().runPacks();

	}

	public static String getWalaClassName(String string) {
		return "L" + string.replace(".", "/"); // yes, /, not \ or \\

	}

}
