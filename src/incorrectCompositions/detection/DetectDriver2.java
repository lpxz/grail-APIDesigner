package incorrectCompositions.detection;

import incorrectCompositions.detection.offlinePrediction.CommonUtil;
import incorrectCompositions.detection.offlinePrediction.EngineMain;
import incorrectCompositions.detection.offlinePrediction.TraceEngine;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.AddMonitorMain;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.BaseVisitor;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.ShutDownCleanerThread;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor.MonitorData;
import incorrectCompositions.detection.setupCompositionMetadataInSource.AddClientMethodAtomicity;
import incorrectCompositions.detection.setupCompositionMetadataInSource.AddLibAPIMarker;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Remover;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;

import java.lang.reflect.Method;
import java.util.List;

import edu.hkust.clap.monitor.Monitor;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class DetectDriver2 {

	// idempotent: you can run the DetectDrvier2.main for any times, without affecting the projects.

	// the arguments:
	// The projectname, the mainclass name, the arguments (remember that we need to monitor the run.)
	// we do not provide the way to set the excludeoptions or includeoptions currently. 
	// one can always hardcode them in the source code of this project.
	public static void main(String[] args) {
		if (args.length < 2)
			throw new RuntimeException(
					"argument list: projectname mainclass arguments");

		CentralConstants.projectname = args[0];//"Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);
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

		// find the smallest todo Composition, then process it via Driver1 and Driver2, seperately.
		String smallestTodo = ProjectDirectoryHelper
				.findSmallestTodoComposition();
		if (smallestTodo == null) // none composition to do. nothing changes, just return.
			return;

		System.err.println("Now, process: " + smallestTodo);

		SerializableComposition composition = (SerializableComposition) Serializer
				.deserialize(smallestTodo);

		try {
			monitor_predict(composition);

			System.err.println("Now, as it is processed, we remove: "
					+ smallestTodo);
			Remover.removeFile(smallestTodo);////  remove the current compsoition from todoQueue.
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private static void monitor_predict(SerializableComposition composition) {
		// 1 set up the composition Metadata in the source code.
		//		SourceVersionControl.backupDir(CentralConstants.projectdirectory);
		//		AddClientMethodAtomicity.addClientMethodAtomicity(composition);
		//		AddLibAPIMarker.addLibAPIMarker(composition);
		//		Recompiler.javac(CentralConstants.projectdirectory);
		//
		//		// predictive analysis goes here.
		//
		//		// 2 transform
		//		BaseVisitor.setObserverClass(CentralConstants.monitorClass);
		//
		//		AddMonitorMain.transformRuntimeVersion(CentralConstants.mainclass);
		//
		//		AddMonitorMain.saveTransformResults(CentralConstants.mainclass);

		// 3 record

		String appname = CentralConstants.mainclass;
		String[] mainArgs = CentralConstants.args;

		// replace the stub monitor.class to real monitor.class (the functional or the implementation version)
		System.err
				.println("step 3: copy Monitor.java_impl as Monitor.java, compile");
		System.err.println(CentralConstants.implMonitorSrc() + " -> "
				+ CentralConstants.inUseMonitorSrc());

		Copier.copyFileFromTo(CentralConstants.implMonitorSrc(),
				CentralConstants.inUseMonitorSrc());
		Recompiler.javac(System.getProperty("user.dir"));// compile our analysis project.

		Monitor.mainThreadStartRun0(appname, mainArgs);
		Class<?> c;
		try {
			c = Class.forName(appname);
			Class[] argTypes = new Class[] { String[].class };
			Method main = c.getDeclaredMethod("main", argTypes);
			main.setAccessible(true);

			Runtime.getRuntime().addShutdownHook(new ShutDownCleanerThread());
			System.err.println("step 3: monitoring, record the trace");

			main.invoke(null, (Object) mainArgs);
		} catch (Exception e) {
			throw new RuntimeException("reflection errors");
		}

		//4  prediction.
		System.err.println("step 4: predictive analysis now");

		MonitorData mondata = EngineMain.getMonitorData();
		TraceEngine engine = new TraceEngine(mondata);
		engine.setCheckAtomicity();
		engine.preProcess();
		engine.findAllPatterns();
		engine.saveAVsOnly(composition);
		//		engine.saveAVsOnly();

	}

}
