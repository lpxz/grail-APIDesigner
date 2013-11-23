package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;

public class SourceVersionControl {

	public static void backupFile(String fromfile) {
		Copier.copyFileFromTo(fromfile, fromfile + "_copy");
	}

	public static void restoreFile(String tofile) {
		Copier.copyFileFromTo(tofile + "_copy", tofile);
	}

	public static void backupDir(String fromdir) {
		Copier.copyDirFromTo(fromdir, fromdir + "_copy");
	}

	public static void restoreDir(String todir) {
		Copier.copyDirFromTo(todir + "_copy", todir);
	}

	public static void main(String[] args) {
		//backupFile("/Users/charlesz/git/git4api/APIDesigner/notused/AddAtomIntentionDriver.java");
		//		restoreFile("/Users/charlesz/git/git4api/APIDesigner/notused/AddAtomIntentionDriver.java");
		checkoutOrigin("/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test");

	}

	public static void checkoutOrigin(String projectdirectory) {
		String originVersionFolder = projectdirectory + "_origin";
		File originVersion = new File(originVersionFolder);
		if (!originVersion.exists()) {
			Copier.copyDirFromTo(projectdirectory, originVersionFolder); // create the original copy.
		} else {
			Copier.copyDirFromTo(originVersionFolder, projectdirectory);// check out the original copy as the contents in current folder may be changed.
		}

	}

}
