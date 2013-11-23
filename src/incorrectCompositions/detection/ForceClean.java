package incorrectCompositions.detection;

import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;
import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class ForceClean {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1)
			throw new RuntimeException("argument list: projectname  ");

		CentralConstants.projectname = args[0];//"Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);// required!
		//		CentralConstants.mainclass = args[1];
		//		CentralConstants.args = new String[args.length - 2];
		//		for (int i = 2; i < args.length; i++) {
		//			CentralConstants.args[i - 2] = args[i];
		//		}
		System.out.println("Projectname: " + CentralConstants.projectname);

		//in case that some crash happens, clean the project.
		SourceVersionControl.restoreDir(CentralConstants.projectdirectory);
		Copier.copyFileFromTo(CentralConstants.stubMonitorSrc(),
				CentralConstants.inUseMonitorSrc());
		System.out.println("clean completed!");

	}

}
