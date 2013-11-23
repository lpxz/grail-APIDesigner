package incorrectCompositions.confirm.dynamicenforce;

import java.lang.reflect.Method;

import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;
import incorrectCompositions.detection.onlineMonitoring.addMonitor.AddMonitorUtil;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.ShutDownCleanerThread;
import incorrectCompositions.detection.setupCompositionMetadataInSource.AddClientMethodAtomicity;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Mover;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Runner;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;
import utils.CentralConstants;
import utils.ProjectDirectoryHelper;

public class DynamicWitness {

	/**
	 * @param args
	 */

	public static String	runtimeLibName	= "incorrectCompositions.confirm.dynamicenforce.RuntimeLibraryBothBlocking";

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

		// step 1 : add enter composition. at atomic composition
		String smallestDetected = ProjectDirectoryHelper
				.findSmallestIncorrectCompositionDetected();

		if (smallestDetected == null) // none composition to do.
			return;
		SerializableComposition composition = (SerializableComposition) Serializer
				.deserialize(smallestDetected);

		System.err.println("Now, enforce the AV of: " + composition);

		System.err
				.println("First, check out the original version (which may be updated before): "
						+ CentralConstants.projectdirectory);
		SourceVersionControl.checkoutOrigin(CentralConstants.projectdirectory);

		System.err
				.println("step1: add enter exit composition, and PCR. Then compile.");

		//  step 2: insert at P, C, R.
		// first, enhance the composition, otherwise, I do not know where to insert R.
		AddPCR.addPCR(composition); // also add import. 
		AddEnterExitComposition.addEnterExitComposition(composition); // do not move this to the front, as it may make addPCR throw exceptions. (line change).

		Recompiler.javac(CentralConstants.projectdirectory);

		System.err.println("step 2: execute.");
		String argline = "";
		for (String str : CentralConstants.args) {
			argline = argline + " " + str;
		}
		Runner.run(CentralConstants.mainclass, argline,
				CentralConstants.projectdirectory);

	}

}
