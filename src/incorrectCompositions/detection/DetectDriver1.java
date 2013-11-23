package incorrectCompositions.detection;

import incorrectCompositions.detection.onlineMonitoring.addMonitor.AddMonitorMain;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.BaseVisitor;
import incorrectCompositions.detection.setupCompositionMetadataInSource.AddClientMethodAtomicity;
import incorrectCompositions.detection.setupCompositionMetadataInSource.AddLibAPIMarker;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;

import java.util.List;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;
// Driver1 and Driver2 are excuted sequentially.
//Driver 1: setupSource_transform
// Driver 2: monitor_predict_restore

public class DetectDriver1 {

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

		// find the smallest todo Composition, then process it via Driver1 and Driver2, seperately.
		String smallestTodo = ProjectDirectoryHelper
				.findSmallestTodoComposition();

		if (smallestTodo == null) // none composition to do.
			return;
		System.err.println("Now, process: " + smallestTodo);

		SerializableComposition composition = (SerializableComposition) Serializer
				.deserialize(smallestTodo);

		System.err
				.println("First, check out the original version (which may be updated before): "
						+ CentralConstants.projectdirectory);
		SourceVersionControl.checkoutOrigin(CentralConstants.projectdirectory);
		try {
			setupSource_transform(composition);// three stuff are changed: Monitor.class, bencharmk/src, tmp/runtime/benchmark/*.class
		} catch (Throwable e) { //in case that some crash happens, clean the project.
			e.printStackTrace();
		}

	}

	private static void setupSource_transform(
			SerializableComposition composition) {
		// 1 set up the composition Metadata in the source code.

		System.err
				.println("step 1: set up the composition metadata in the source code, then compile.");
		AddClientMethodAtomicity.addClientMethodAtomicity(composition);
		AddLibAPIMarker.addLibAPIMarker(composition);
		Recompiler.javac(CentralConstants.projectdirectory);

		// predictive analysis goes here.

		// 2 transform
		System.err.println("step 2: inject the monitor code to the bytecode.");
		// change back to the Stub monitor.class, so that in the next run, the transformation can inject the stub monitor code easily.
		System.err.println("copy the Monitor.java_stub as Monitor.java");
		System.err.println(CentralConstants.stubMonitorSrc() + " -> "
				+ CentralConstants.inUseMonitorSrc());

		Copier.copyFileFromTo(CentralConstants.stubMonitorSrc(),
				CentralConstants.inUseMonitorSrc());
		Recompiler.javac(System.getProperty("user.dir"));// compile our project.
		BaseVisitor.setObserverClass(CentralConstants.monitorClass);

		AddMonitorMain.transformRuntimeVersion(CentralConstants.mainclass);

		AddMonitorMain.saveTransformResults(CentralConstants.mainclass);

		// 3 record

		//4  prediction.

	}

}
