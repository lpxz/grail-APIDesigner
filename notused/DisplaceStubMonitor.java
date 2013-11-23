package incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor;

import incorrectCompositions.detection.setupCompositionMetadataInSource.Copier;
import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.RenamePackage;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;

public class DisplaceStubMonitor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		displaceStubMonitor();
	}

	private static void displaceStubMonitor() {
		// TODO Auto-generated method stub
		//      1 backup stub monitor
		SourceVersionControl
				.backupFile("/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");

		//	2 copy src
		Copier.copyFileFromTo(
				"/Users/charlesz/git/git4api/APIDesigner/src/incorrectCompositions/detection/onlineMonitoring/runMonitor/monitor/Monitor.java",
				"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");
		//	    3 recompiler
		// 3.1 rename the package
		RenamePackage
				.renamePakcage(
						"/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java",
						"edu.hkust.clap.monitor");

		// 3.2 compile.
		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");

		//		4 recover
		SourceVersionControl
				.restoreFile("/Users/charlesz/git/git4api/APIDesigner/src/edu/hkust/clap/monitor/Monitor.java");

		// 		5 recompiler.
		Recompiler.javac("/Users/charlesz/git/git4api/APIDesigner");

	}
}
