package incorrectCompositions.detection.setupCompositionMetadataInSource;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.test.Helper;

import java.io.File;
import java.util.List;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import utils.SootHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class RenamePackage {

	// handle one composition at once!
	// do not be too agressive.
	public static void main(String[] args) {
		renamePakcage(
				"/Users/charlesz/git/git4api/APIDesigner/notused/AddAtomIntentionDriver.java",
				"edu.hkust.clap.monitor");
	}

	// add synchronized("AC_id_deadlockproofcounter"){}. AC_id encodes the information of the composition.
	// if there are deadlocks, add synchronized(new Object()){}.
	public static void renamePakcage(String classfilename, String newPackageName) {

		File file = new File(classfilename);
		try {
			String source = Helper.readFile(file);
			CompilationUnit cu = Helper.parserString(source);
			RenamePackageVisitor visitor = new RenamePackageVisitor();
			visitor.setNewPakcageName(newPackageName);
			visitor.visit(cu, null);
			Helper.writeFile(visitor.getSource(), (file.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
