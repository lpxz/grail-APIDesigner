package incorrectCompositions.detection.onlineMonitoring.runMonitor;

import edu.hkust.clap.monitor.Monitor;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.RenamePackage;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import utils.CentralConstants;

public class RunMonitorMain {

	// public static boolean mainFinish = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String appname = CentralConstants.mainclass;
		String[] mainArgs = CentralConstants.args;

		// set up the real monitor.class (the functional or the implementation version)
		Copier.copyFileFromTo(
				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java_impl",
				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");
		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");

		//===========run: 
		Monitor.mainThreadStartRun0(appname, mainArgs);
		// you are not running the edu.hkust.clap.monitor.Monitor.class here.

		Class<?> c = Class.forName(appname);// reflection is necessary.
		Class[] argTypes = new Class[] { String[].class };
		Method main = c.getDeclaredMethod("main", argTypes);
		main.setAccessible(true);

		Runtime.getRuntime().addShutdownHook(new ShutDownCleanerThread());
		main.invoke(null, (Object) mainArgs);
		//==========finish run
		// change back to the Stub monitor.class, so that in the next run, the program can inject the stub monitor code easily.
		Copier.copyFileFromTo(
				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java_stub",
				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");
		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");

	}

	// setting the classpath and load the edu...Monitor.class dynamically, 
	// instead of loading them from different projects, which maintain a Monitor.class independently.
	//	private static void run() throws Exception {
	//
	//		// set up the real monitor.class (the functional or the implementation version)
	//		Copier.copyFileFromTo(
	//				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java_impl",
	//				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");
	//		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");
	//
	//		//===========run: 
	//		Monitor.mainThreadStartRun0(appname, mainArgs);
	//		// you are not running the edu.hkust.clap.monitor.Monitor.class here.
	//
	//		Class<?> c = Class.forName(appname);// reflection is necessary.
	//		Class[] argTypes = new Class[] { String[].class };
	//		Method main = c.getDeclaredMethod("main", argTypes);
	//		main.setAccessible(true);
	//
	//		Runtime.getRuntime().addShutdownHook(new ShutDownCleanerThread());
	//		main.invoke(null, (Object) mainArgs);
	//		//==========finish run
	//		// change back to the Stub monitor.class, so that in the next run, the program can inject the stub monitor code easily.
	//		Copier.copyFileFromTo(
	//				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java_stub",
	//				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");
	//		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");
	//
	//	}
}
