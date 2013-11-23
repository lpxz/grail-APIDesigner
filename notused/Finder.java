package atomicCompositions.analysis;

import incorrectCompositions.detection.onlineMonitoring.addMonitor.AddMonitorUtil;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import japa.parser.ast.visitor.SameBlockChecker;
import japa.parser.ast.visitor.SearchForClassFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import com.ibm.wala.examples.drivers.PDFSDG;

import atomicCompositions.datastructure.ClientMethod;
import atomicCompositions.datastructure.ClientViewOfLibrary;
import atomicCompositions.datastructure.Composition;
import atomicCompositions.datastructure.LibraryModule;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

import soot.Body;
import soot.BodyTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.util.Chain;
import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import utils.SootHelper;
import utils.Timer;

public class Finder {
	public static void main(String[] args) throws IOException { // wjtp.tn
		if (args.length < 2)
			throw new RuntimeException(
					"argument list: projectname mainclass arguments. arguments are optional.");

		CentralConstants.projectname = args[0];//"Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);// required!
		CentralConstants.mainclass = args[1];
		CentralConstants.args = new String[args.length - 2];
		for (int i = 2; i < args.length; i++) {
			CentralConstants.args[i - 2] = args[i];
		}
		System.out.println("Projectname: " + CentralConstants.projectname);
		System.out.println("mainclass:" + CentralConstants.mainclass);
		System.out.println("arguments:");
		for (String str : CentralConstants.args)
			System.out.print(str + " ");

		String binfolder = CentralConstants.projectname
				+ System.getProperty("file.separator") + "bin";
		String newCP = AddMonitorUtil.classpath();
		String givenArg = "-cp " + newCP + " -pp -f J "
				+ AddMonitorUtil.excludeArgString()
				+ AddMonitorUtil.includeArgString() + " -process-dir "
				+ binfolder;

		Recompiler.javac(CentralConstants.projectname);

		//++++++++++++++++++set up wala!+++++++++++++++++++
		try {
			WalaSlicerTransformer.prepareSDGAllMainEntries(binfolder,
					//					PDGDriver.getWalaClassName(CentralConstants.mainclass),
					PDFSDG.getDataDependenceOptions("no_base_no_heap"),
					PDFSDG.getControlDependenceOptions("no_exceptional_edges"));
		} catch (Exception e) {
			throw new RuntimeException("wala errors");
		}

		//++++++++++++++++++set up wala!+++++++++++++++++++

		String[] finalArgs = givenArg.split(" "); //args[0].split(" ");//
		soot.Main.v().processCmdLine(finalArgs);
		utils.ProjectDirectoryHelper.setProject_Dir_Name(finalArgs);
		utils.Options.setOptions();
		setSootOptions();
		Scene.v().loadNecessaryClasses();
		Pack jtp = PackManager.v().getPack("jtp");
		addACfinderPack(jtp);
		PackManager.v().runPacks();
		analysis_report();
		//		TODO try to switch to other compositions.
		//	    let us focus on the compositions found by ICfinder-USE first:	g_atomic_compositions_use
		for (Composition composition : g_atomic_compositions_total) {//g_atomic_compositions_use
			composition.print();

		}

		List<SerializableComposition> serialComps = Serializer
				.toSerializableCompositions(g_atomic_compositions_total);//g_atomic_compositions_use

		Serializer.serialize(serialComps,
				CentralConstants.getFile4CompositionSet());
		for (SerializableComposition serialComp : serialComps) {
			Serializer.serialize(serialComp,
					CentralConstants.getFile4TodoComposition(serialComp));

		}

	}

	public static void setSootOptions() {
		List excludesList = new ArrayList();
		excludesList.add("jrockit.");
		excludesList.add("com.bea.jrockit");
		excludesList.add("sun.");
		Options.v().set_exclude(excludesList);
		Options.v().set_keep_line_number(true);

	}

	public static Set<Composition>	g_atomic_compositions_total	= new HashSet<Composition>();
	public static Set<Composition>	g_atomic_compositions_use	= new HashSet<Composition>();
	public static Set<Composition>	g_atomic_compositions_comp	= new HashSet<Composition>();
	public static Set<Composition>	g_all_compositions			= new HashSet<Composition>();
	public static Set<Composition>	g_atomic_compositions_muvi	= new HashSet<Composition>();

	private static void addACfinderPack(Pack jtp2) {
		jtp2.add(new Transform("jtp.ACfinder", new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phaseName,
					Map options) {
				SootMethod sm = b.getMethod();

				Set<ClientViewOfLibrary> client_view_of_libraries = findClientViewOfLibraries(b);
				for (ClientViewOfLibrary client_view_of_library : client_view_of_libraries) {
					if (noComposition(client_view_of_library))
						continue;
					// ======with USE symptom=========================
					//                	if(Timer.lasttime==-1) Timer.init();
					//					client_view_of_library.print();
					Set<Composition> atomic_compositions_use = applyUseSymptom(client_view_of_library);
					g_atomic_compositions_use.addAll(atomic_compositions_use);
					//                	Timer.reportTimeSinceLast();
					//=================================================

					// ======with complementation symptom==============

					Set<Composition> atomic_compositions_comp = applyComplementSymptom(client_view_of_library);
					g_atomic_compositions_comp.addAll(atomic_compositions_comp);
					//TODO domination-based
					//=================================================

					// ======for MUVI symptom=========================
					// two invocations are likely to be composed atomically if they are often invoked together
					Set<Composition> all_compositions = applyNoSymptom(client_view_of_library);
					g_all_compositions.addAll(all_compositions);
					// ===============================================

				}

			}

			private Set<Composition> applyNoSymptom(
					ClientViewOfLibrary client_view_of_library) {
				Set<Composition> retSet = new HashSet<Composition>();
				List<Stmt> invokes = client_view_of_library.getInvokes();
				Body b = client_view_of_library.getClient_method().getBody();
				for (int i = 0; i < invokes.size(); i++) {
					for (int j = i + 1; j < invokes.size(); j++) {
						Stmt iStmt = invokes.get(i);
						Stmt jStmt = invokes.get(j);
						// istmt and jstmt may invoke the same API, e.g., inc() and inc(). We include it.
						Composition composition = Composition.Factory.formAC(
								iStmt, jStmt, b);
						if (composition != null)
							retSet.add(composition);
					}
				}
				return retSet;
			}

			private Set<Composition> applyComplementSymptom(
					ClientViewOfLibrary client_view_of_library) {
				Set<Composition> retSet = new HashSet<Composition>();
				List<Stmt> invokes = client_view_of_library.getInvokes();
				Body b = client_view_of_library.getClient_method().getBody();
				for (int i = 0; i < invokes.size(); i++) {
					for (int j = i + 1; j < invokes.size(); j++) {
						Stmt iStmt = invokes.get(i);
						Stmt jStmt = invokes.get(j);
						Composition composition = formComplementSymptomComposition(
								iStmt, jStmt, b);
						if (composition != null)
							retSet.add(composition);
					}
				}
				return retSet;
			}

			private Composition formComplementSymptomComposition(Stmt iStmt,
					Stmt jStmt, Body b) {
				if (isInSameBlock(iStmt, jStmt, b.getMethod())) {
					Composition composition = Composition.Factory.formAC(iStmt,
							jStmt, b);
					return composition;
				}
				return null;
			}

			List<Stmt>	afterI	= new ArrayList<Stmt>();

			private Set<Composition> applyUseSymptom(
					ClientViewOfLibrary client_view_of_library) {
				Set<Composition> retSet = new HashSet<Composition>();
				List<Stmt> invokes = client_view_of_library.getInvokes();
				Body b = client_view_of_library.getClient_method().getBody();
				for (int i = 0; i < invokes.size(); i++) {
					//					Use the optimized version in the following
					//					otherwise, you cannot get the results with your poor mac
					//					for(int j=i+1; j<invokes.size();j++ )
					//					{
					//						Stmt iStmt = invokes.get(i);
					//						Stmt jStmt = invokes.get(j);
					//						Composition composition = formUSESymptomComposition(iStmt, jStmt, b);
					//						if(composition!=null) retSet.add(composition);
					//					}
					afterI.clear();
					for (int j = i + 1; j < invokes.size(); j++)
						afterI.add(invokes.get(j));
					Set<Composition> compositions = formUSESymptomCompositions_opt(
							invokes.get(i), afterI, b);
					retSet.addAll(compositions);
				}
				return retSet;
			}

			private boolean noComposition(
					ClientViewOfLibrary client_view_of_library) {
				List<Stmt> invokes = client_view_of_library.getInvokes();
				if (invokes.size() <= 1)
					return true; // trivial case: only an invocation is there.
				return false;
			}

			private boolean isInSameBlock(Stmt invokeI, Stmt invokeJ,
					SootMethod sm) {
				String filename = getSourceFile(sm.getDeclaringClass());
				int line1 = SootHelper.getLine(invokeI);
				int line2 = SootHelper.getLine(invokeJ);
				if (filename != null && line1 != -1 && line2 != -1) {
					// System.out.println("same block:" + invokeI + invokeJ);
					return SameBlockChecker.checkBlock(filename, line1, line2)
							|| SameBlockChecker.checkBlock(filename, line2,
									line1);
				}

				return false;
			}

			public String getSourceFile(SootClass sc) {
				try {

					// if avrora, fix the bug.
					String foundfile = SearchForClassFile.findFile4Class(
							CentralConstants.projectdirectory, sc.getName());
					// System.out.println("class: " + sc.getName() + " " +
					// foundfile );

					return foundfile;
				} catch (Exception e) {
					// e.printStackTrace();
					return null;
				}

				// if (h.hasTag("SourceFileTag")) {
				// return ((SourceFileTag)
				// h.getTag("SourceFileTag")).getAbsolutePath();
				// }
				// return null;
			}

			public int getLineNum(Host h) {
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

			// lpxz: the optimized version of formUSESymptomComposition.
			// it computes the compositions related to iStmt in one procedure.
			// previously, it computes the composition between iStmt and jStmt in one procedure.

			Stack<Unit>			depChainStack	= new Stack<Unit>();
			Stack<Unit>			dfsStack		= new Stack<Unit>();
			Set<Unit>			grey			= new HashSet<Unit>();
			Set<Unit>			black			= new HashSet<Unit>();

			Set<Composition>	retSet			= new HashSet<Composition>();

			private Set<Composition> formUSESymptomCompositions_opt(Stmt iStmt,
					List<Stmt> jStmts, Body b) {
				retSet.clear(); // save memory.

				dfsStack.clear();
				grey.clear();
				black.clear();
				depChainStack.clear();
				dfsStack.push(iStmt);
				while (!dfsStack.isEmpty()) {
					Unit popped = dfsStack.pop();
					if (popped instanceof NopStmt)
						continue;

					if (!grey.contains(popped)) {
						grey.add(popped);// becomes grey. and ...
						depChainStack.push(popped);
						// =============visiting:====================
						Stmt popStmt = (Stmt) popped;
						if (jStmts.contains(popStmt)) {
							// the jStmt is reachable from istmt.
							Stmt jStmt = popStmt;
							Composition composition = Composition.Factory
									.formAC(iStmt, jStmt, b);
							if (utils.Options.recordDepChain)
								composition.setMiscInfo(depChainStack.clone());
							retSet.add(composition);
						}
						// =========================================
						dfsStack.push(popped);// when it comes out nexttime, it is contained in visited, meaning it turns black.
						Set<Unit> children = null;
						try {
							children = getDependents(b, popped);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (children != null)// else, suppose no children.
						{
							for (Unit child : children) {
								if (!grey.contains(child)) {
									dfsStack.push(child);
								}
							}
						}

					} else if (grey.contains(popped) && !black.contains(popped)) {
						black.add(popped);
						depChainStack.pop();
					} else if (black.contains(popped)) {
						// important: safely ignore.
						// consider in a binary tree with 7 nodes, where node 4 has an extra edge to node 3.
					}
				}

				return retSet;
			}

			// logic: first time gets out of dfsstack, white->grey, push it to depChain (in progress nodes).
			//        second time gets out of dfsstack, grey->black, pop it from depChain (complete).
			//        third time gets  out of dfsstack, ignore. it is possible, consider in the binary tree with 7 nodes, where the 4th node connects to the 3rd node.
			private Composition formUSESymptomComposition(Stmt iStmt,
					Stmt jStmt, Body b) {
				dfsStack.clear();
				grey.clear();
				black.clear();
				depChainStack.clear();
				dfsStack.push(iStmt);
				while (!dfsStack.isEmpty()) {
					Unit popped = dfsStack.pop();
					if (popped instanceof NopStmt)
						continue;

					if (!grey.contains(popped)) {
						grey.add(popped);// becomes grey. and ...
						depChainStack.push(popped);
						// =============visiting:====================
						Stmt popStmt = (Stmt) popped;
						if (popStmt == jStmt) {
							// jstmt is reachable from istmt.
							Composition composition = Composition.Factory
									.formAC(iStmt, jStmt, b);
							if (utils.Options.recordDepChain)
								composition.setMiscInfo(depChainStack.clone());
							return composition;
						}

						// =========================================
						dfsStack.push(popped);// when it comes out nexttime, it is contained in visited, meaning it turns black.
						Set<Unit> children = null;
						try {
							children = getDependents(b, popped);
						} catch (Exception e) {
						}
						if (children != null)// else, suppose no children.
						{
							for (Unit child : children) {
								if (!grey.contains(child)) {
									dfsStack.push(child);
								}
							}
						}

					} else if (grey.contains(popped) && !black.contains(popped)) {
						black.add(popped);
						depChainStack.pop();
					} else if (black.contains(popped)) {
						// important: safely ignore.
						// consider in a binary tree with 7 nodes, where node 4 has an extra edge to node 3.
					}
				}

				return null;
			}

			DeepDDGTransformer	DDDGT	= new DeepDDGTransformer();

			private Set<Unit> getDependents(Body bb, Unit popped) {
				Set<Unit> children = new HashSet<Unit>();
				if (utils.Options.interPDataDependent) {// TODO improve it.
					// efficient heap-based data dependence computation, which complements the wala lib.
					Set<Unit> deepdatachildren = DDDGT.getDeepDataDependents(
							bb, popped);
					children.addAll(deepdatachildren);
				}

				Set<Unit> pdgChildren = WalaSlicerTransformer
						.getDependentStmts_final(bb, (Stmt) popped);

				children.addAll(pdgChildren);
				return children;
			}

		}));

	}

	protected static Set<ClientViewOfLibrary> findClientViewOfLibraries(Body b) {
		Set<ClientViewOfLibrary> retSet = new HashSet<ClientViewOfLibrary>();
		Iterator<Unit> unitIt = b.getUnits().iterator();
		while (unitIt.hasNext()) {
			Unit unit = (Unit) unitIt.next();
			Stmt stmt = (Stmt) unit;
			if (stmt.containsInvokeExpr()) {
				SootClass libclass = stmt.getInvokeExpr().getMethod()
						.getDeclaringClass();
				if (invokeAtomicAPI(stmt) && containsInstanceInvoke(stmt)) {
					ClientViewOfLibrary client_view_of_library = getOrAdd(
							retSet, b, libclass);
					client_view_of_library.addStmt(stmt);
				}
			}
		}
		return retSet;

	}

	private static boolean invokeAtomicAPI(Stmt stmt) {
		SootMethod api = stmt.getInvokeExpr().getMethod();
		if (api.isSynchronized()) {
			return true;
		} else {
			if (api.hasActiveBody()) {
				Body b = api.getActiveBody();
				for (Unit unit : b.getUnits()) {
					if (unit instanceof MonitorStmt)// corresponds to a synchronized block.
						return true;
				}
			}
			return false;
		}
	}

	private static boolean containsInstanceInvoke(Stmt stmt) {
		return stmt.getInvokeExpr() instanceof InstanceInvokeExpr;
	}

	private static ClientViewOfLibrary getOrAdd(
			Set<ClientViewOfLibrary> retSet, Body b, SootClass sc) {
		Iterator<ClientViewOfLibrary> it = retSet.iterator();
		while (it.hasNext()) {
			ClientViewOfLibrary clientViewOfLibrary = (ClientViewOfLibrary) it
					.next();
			if (clientViewOfLibrary.equivalent(b, sc))
				return clientViewOfLibrary;
		}
		// not found.
		ClientViewOfLibrary clientViewOfLibrary = new ClientViewOfLibrary(
				b.getMethod(), sc);// single-class library
		retSet.add(clientViewOfLibrary);
		return clientViewOfLibrary;
	}

	protected static HashMap<Pair, Set<SootMethod>> mergeACs(
			HashMap<Pair, Set<SootMethod>> ACs1,
			HashMap<Pair, Set<SootMethod>> ACs2) {
		HashMap<Pair, Set<SootMethod>> ac_totalSymptom = new HashMap<Pair, Set<SootMethod>>();
		// two sets may overlap.
		Iterator<Pair> pairIT = ACs1.keySet().iterator();
		while (pairIT.hasNext()) {
			Pair pair = (Pair) pairIT.next();
			Set<SootMethod> methods = ACs1.get(pair);
			ac_totalSymptom.put(pair, methods);
		}

		Iterator<Pair> pairIT2 = ACs2.keySet().iterator();
		while (pairIT2.hasNext()) {
			Pair pair2 = (Pair) pairIT2.next();
			Set<SootMethod> methods2 = ACs2.get(pair2);
			Set<SootMethod> existingMethods = ac_totalSymptom.get(pair2);
			if (existingMethods == null)
				ac_totalSymptom.put(pair2, methods2);// directly replace.
			else
				existingMethods.addAll(methods2);
		}
		return ac_totalSymptom;
	}

	// APIsInvokedTogether2Callers: it helps count in how many client methods two API methods (independent of the invocation sites)
	// are invoked together.
	static HashMap<Pair, Set<SootMethod>>	APIsInvokedTogether2Callers	= new HashMap<Pair, Set<SootMethod>>();

	private static Set<Composition> applyMUVISymptom(
			Set<Composition> compositions) {
		// =======statistics
		APIsInvokedTogether2Callers.clear();
		Iterator<Composition> iterator = compositions.iterator();
		while (iterator.hasNext()) {
			Composition composition = iterator.next();
			SootMethod clientMethod = composition.getClient_method()
					.getMethod();
			Pair key = new Pair(composition.getFirst().getInvokeExpr()
					.getMethod(), composition.getLast().getInvokeExpr()
					.getMethod());
			Set<SootMethod> valueList = APIsInvokedTogether2Callers.get(key);
			if (valueList == null) {
				valueList = new HashSet<SootMethod>();
				APIsInvokedTogether2Callers.put(key, valueList);
			}
			valueList.add(clientMethod);
		}

		//==========judge:
		Iterator<Composition> iterator2 = compositions.iterator();
		while (iterator2.hasNext()) {
			Composition composition = iterator2.next();

			Pair key = new Pair(composition.getFirst().getInvokeExpr()
					.getMethod(), composition.getLast().getInvokeExpr()
					.getMethod());
			Set<SootMethod> valueList = APIsInvokedTogether2Callers.get(key);
			if (valueList != null && valueList.size() >= frquencyThreshold) {
				g_atomic_compositions_muvi.add(composition);
				//TODO print later
			}
		}
		return g_atomic_compositions_muvi;

	}

	public static int	frquencyThreshold	= 1;

	public static void setThreshold(int arg) {
		frquencyThreshold = arg;
	}

	private static Set<Composition> ac_symptom1_butNot_symptom2(
			Set<Composition> ac_symptom1, Set<Composition> ac_symptom2) {
		Set<Composition> ac_symptom1_butNot_symptom2 = new HashSet<Composition>();
		ac_symptom1_butNot_symptom2.addAll(ac_symptom1);
		for (Composition composition : ac_symptom2) {
			if (ac_symptom1_butNot_symptom2.contains(composition))
				ac_symptom1_butNot_symptom2.remove(composition);
		}

		return ac_symptom1_butNot_symptom2;
	}

	private static void analysis_report() {
		System.out.println("\n");

		Set clientclassesSet = new HashSet();
		clientclassesSet.addAll(motherClassesOfACs(g_atomic_compositions_use));
		clientclassesSet.addAll(motherClassesOfACs(g_atomic_compositions_comp));
		int CM = clientclassesSet.size();
		clientclassesSet.clear(); // avoid memory bloat

		//TODO test the hashset-related mechanism. Does it filter duplications properly?
		g_atomic_compositions_total.addAll(g_atomic_compositions_use);
		g_atomic_compositions_total.addAll(g_atomic_compositions_comp);
		int AC_total = g_atomic_compositions_total.size();
		int LM = classesInvolvedByAC(g_atomic_compositions_total);
		int API = APIsInvolvedbyAC(g_atomic_compositions_total);

		// a new iteration of MUVI: set a larger frequency threshold.
		setThreshold(utils.Options.muvi_threshold);
		g_atomic_compositions_muvi = applyMUVISymptom(g_all_compositions);
		int AC_MUVI = g_atomic_compositions_muvi.size();

		// delta: (dep but not MUVI)

		int AC_USE = g_atomic_compositions_use.size();

		Set<Composition> ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_use, g_atomic_compositions_muvi);
		int Delta_USE = ac_symptom1_butNot_symptom2.size();

		// delta: (MUVI but not codep)
		ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_muvi, g_atomic_compositions_use);
		int Deltapi_USE = ac_symptom1_butNot_symptom2.size();

		int AC_COMP = g_atomic_compositions_comp.size();

		// delta: (codep but not MUVI)
		ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_comp, g_atomic_compositions_muvi);
		int Delta_COMP = ac_symptom1_butNot_symptom2.size();

		// delta: (MUVI but not codep)
		ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_muvi, g_atomic_compositions_comp);
		int Deltapi_COMP = ac_symptom1_butNot_symptom2.size();

		// HashMap<Pair, Set<SootMethod>> ac_total = mergeACs(ac_depSymptom,
		// ac_CoDepSymptom);

		ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_total, g_atomic_compositions_muvi);
		int Delta = ac_symptom1_butNot_symptom2.size();

		ac_symptom1_butNot_symptom2 = ac_symptom1_butNot_symptom2(
				g_atomic_compositions_muvi, g_atomic_compositions_total);
		int Deltapi = ac_symptom1_butNot_symptom2.size();

		// =====================================
		System.out.println("# LM: " + LM);
		System.out.println("# API: " + API);
		System.out.println("# CM: " + CM);
		System.out.println("# AC: " + AC_total);
		System.out.println(" ");

		// ==================================
		System.out.println("# AC_MUVI (minSupport=" + frquencyThreshold + "): "
				+ AC_MUVI);

		System.out.println("# AC: " + AC_total);
		System.out.println("# Delta: " + Delta);
		System.out.println("# Delta': " + Deltapi);

		System.out.println("# AC_USE: " + AC_USE);
		System.out.println("# Delta_USE: " + Delta_USE);
		System.out.println("# Delta'_USE: " + Deltapi_USE);

		System.out.println("# AC_COMP: " + AC_COMP);
		System.out.println("# Delta_COMP: " + Delta_COMP);
		System.out.println("# Delta'_COMP: " + Deltapi_COMP);

	}

	static HashSet<String>	motherClassesOfACs	= new HashSet<String>();

	private static HashSet<String> motherClassesOfACs(
			Set<Composition> ac_symptom) {
		motherClassesOfACs.clear();
		Iterator<Composition> iterator = ac_symptom.iterator();
		while (iterator.hasNext()) {
			Composition composition = iterator.next();
			motherClassesOfACs.add(composition.getClient_method().getMethod()
					.getDeclaringClass().getName());
		}
		return motherClassesOfACs;
	}

	static HashSet<SootClass>	classesInvolvedByAC	= new HashSet<SootClass>();

	private static int classesInvolvedByAC(Set<Composition> acset) {
		for (Composition composition : acset) {
			classesInvolvedByAC.add(composition.getFirst().getInvokeExpr()
					.getMethod().getDeclaringClass());
			classesInvolvedByAC.add(composition.getLast().getInvokeExpr()
					.getMethod().getDeclaringClass());
			classesInvolvedByAC.add(composition.getClient_method().getMethod()
					.getDeclaringClass());

		}
		return classesInvolvedByAC.size();
	}

	static HashSet<SootMethod>	APIsInvolvedbyAC	= new HashSet<SootMethod>();

	private static int APIsInvolvedbyAC(Set<Composition> acset) {
		for (Composition composition : acset) {
			APIsInvolvedbyAC.add(composition.getFirst().getInvokeExpr()
					.getMethod());
			APIsInvolvedbyAC.add(composition.getLast().getInvokeExpr()
					.getMethod());
		}

		return APIsInvolvedbyAC.size();
	}

	private static String retrieveClass(String composeIEMethod) {
		int leftJian = composeIEMethod.indexOf('<');
		int maohao = composeIEMethod.indexOf(':');

		return composeIEMethod.substring(leftJian + 1, maohao);
	}

	public static HashSet<String>	methodsNeedingAtomIntention	= new HashSet<String>();

}
