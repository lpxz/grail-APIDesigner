package incorrectCompositions.detection.onlineMonitoring.addMonitor;

import incorrectCompositions.detection.setupCompositionMetadataInSource.Remover;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.Value;
import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import utils.CentralConstants;

public class AddMonitorMain {

	public static void main(String[] args) {
		CentralConstants.projectname = "Test";
		//		PropertyManager.initialize();

		BaseVisitor.setObserverClass(CentralConstants.monitorClass);

		transformRuntimeVersion(CentralConstants.mainclass);

		saveTransformResults(CentralConstants.mainclass);

	}

	public static void saveTransformResults(String mainclass) {

		System.err.println("*** *** *** *** *** *** *** *** *** *** ");
		System.err.println("*** Total access num: "
				+ BaseVisitor.totalaccessnum);
		System.err.println("*** Instrumented SPE access num: "
				+ BaseVisitor.instrusharedaccessnum);

		HashMap<Integer, String> indexSPEMap = new HashMap<Integer, String>();

		String tmpFolder = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "tmp";
		// tmp folder must be there already.

		Iterator<Value> speSetIt = BaseVisitor.speIndexMap.keySet().iterator();
		System.err.println(" ");
		System.err.println("*** SPE size: " + BaseVisitor.speIndexMap.size());
		System.err.println("*** *** *** *** *** *** *** *** *** *** ");
		System.err.println("*** SPE name: ");
		System.out.println("dump spe to " + tmpFolder
				+ System.getProperty("file.separator") + "spe." + mainclass
				+ ".gz");

		while (speSetIt.hasNext()) {
			Value spe = speSetIt.next();
			String spe_str = spe.toString();
			Integer index = BaseVisitor.speIndexMap.get(spe);
			indexSPEMap.put(index, spe_str);
			System.err.println("*** " + spe_str);
		}

		try {
			File file_IndexSPEMap = new File(tmpFolder
					+ System.getProperty("file.separator") + "spe." + mainclass
					+ ".gz");
			if (!file_IndexSPEMap.exists())
				file_IndexSPEMap.createNewFile();
			ObjectOutputStream fout_IndexSPEMap = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(file_IndexSPEMap)));
			AddMonitorUtil.storeObject(indexSPEMap, fout_IndexSPEMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Iterator<String> methodSetIt = BaseVisitor.methodIndexMap.keySet()
				.iterator();
		System.err.println(" ");
		System.err.println("*** Method size: "
				+ BaseVisitor.methodIndexMap.size());
		System.err.println("*** *** *** *** *** *** *** *** *** *** ");
		System.err.println("*** Method name: ");
		System.out.println("dump method.xx to: " + tmpFolder
				+ System.getProperty("file.separator") + "method." + mainclass
				+ ".gz");

		while (methodSetIt.hasNext()) {
			String methodname = methodSetIt.next();
			Integer index = BaseVisitor.methodIndexMap.get(methodname);
			System.err.println("*** " + methodname);
		}

		try {
			File file_MethodIndexMap = new File(tmpFolder
					+ System.getProperty("file.separator") + "method."
					+ mainclass + ".gz");
			if (!file_MethodIndexMap.exists())
				file_MethodIndexMap.createNewFile();
			ObjectOutputStream fout_MethodIndexMap = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							file_MethodIndexMap)));
			AddMonitorUtil.storeObject(BaseVisitor.methodIndexMap,
					fout_MethodIndexMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void transformRuntimeVersion(String mainclass) { //set soot options.
		PhaseOptions.v().setPhaseOption("jb", "enabled:true");
		Options.v().set_keep_line_number(true);
		PhaseOptions.v().setPhaseOption("jb", "use-original-names:false");
		Options.v().set_whole_program(true);
		Options.v().set_app(true);

		PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
		HashMap<String, String> opt = new HashMap<String, String>();
		opt.put("propagator", "worklist");
		opt.put("simple-edges-bidirectional", "false");
		opt.put("on-fly-cg", "true");
		opt.put("set-impl", "double");
		opt.put("double-set-old", "hybrid");
		opt.put("double-set-new", "hybrid");
		opt.put("pre_jimplify", "true");
		SparkTransformer.v().transform("", opt);

		String newCP = AddMonitorUtil.classpath();
		Scene.v().setSootClassPath(
				System.getProperty("sun.boot.class.path") + File.pathSeparator
						+ System.getProperty("java.class.path")
						+ File.pathSeparator + newCP);

		SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
		Scene.v().setMainClass(appclass);
		Scene.v().loadClassAndSupport(BaseVisitor.observerClass);
		Scene.v().setSootClassPath(null);// let the soot.Main.main() reloads the classpath!

		String outputdir = AddMonitorUtil.getTmpRuntimeProjectFolder();
		System.err.println("IMPORTANT: before the soot run, clear the folder:"
				+ outputdir);
		Remover.removeDir(outputdir);// important. otherwise, -pp would put the classes transformed last time into the cp.
		File outputdirFolder = new File(outputdir);
		if (outputdirFolder.exists())
			throw new RuntimeException(
					" this folder (tmp/runtime/Projectname) should be removed already.");

		String argString = "-cp " + System.getProperty("sun.boot.class.path")
				+ ":" + newCP + " -validate " + mainclass + " -d " + outputdir
				+ AddMonitorUtil.excludeArgString()
				+ AddMonitorUtil.includeArgString(); // do not use -pp option, it puts too many lib into the analysis scope.
		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.transformer", new JTPTransformer()));

		System.err.println("soot args:" + argString);

		//		jimple format: argString="-f jimple "+argString;
		soot.Main.main(argString.split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		soot.G.reset();
		System.err.println("transformed classes are here: " + outputdir);

		System.err
				.println("remove the stub class: edu.hkust.clap.monitor.Monitor");
		Remover.removeFile(outputdir
				+ System.getProperty("file.separator")
				+ BaseVisitor.observerClass.replace(".",
						System.getProperty("file.separator")) + ".class");
	}

}
