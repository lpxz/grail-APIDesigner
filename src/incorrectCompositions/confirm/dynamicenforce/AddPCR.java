package incorrectCompositions.confirm.dynamicenforce;

import incorrectCompositions.detection.setupCompositionMetadataInSource.Recompiler;
import incorrectCompositions.detection.setupCompositionMetadataInSource.SourceVersionControl;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.test.Helper;

import java.io.File;
import java.util.List;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import utils.SootHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class AddPCR {

	// handle one composition at once!
	// do not be too agressive.
	public static void main(String[] args) {

		//		Test code
		//		Constants.projectname="Test";
		//		Constants.projectdirectory=  ProjectDirectoryHelper.findProjectDirGivenName(Constants.projectname);
		//		File ret =locateFile4ClassName("A$Inner");
		//		System.out.println(ret.getAbsolutePath());

		//		running code
		CentralConstants.projectname = "Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);
		List<SerializableComposition> deserializedCompositions = (List<SerializableComposition>) Serializer
				.deserialize(CentralConstants.getFile4CompositionSet());
		for (SerializableComposition composition : deserializedCompositions) {
			SourceVersionControl.backupDir(CentralConstants.projectdirectory);
			addPCR(composition);
			Recompiler.javac(CentralConstants.projectdirectory);
			// predictive analysis here.
			SourceVersionControl.restoreDir(CentralConstants.projectdirectory);
		}
	}

	// add synchronized("AC_id_deadlockproofcounter"){}. AC_id encodes the information of the composition.
	// if there are deadlocks, add synchronized(new Object()){}.
	public static void addPCR(SerializableComposition composition) {
		System.err.println("AddClientMethodAtomicity:");
		composition.print();

		String clientMethodSig = composition.getClientMethod();
		String classname = SootHelper
				.getClassNameFromMethodSig(clientMethodSig);
		File clientMethodFile = ProjectDirectoryHelper
				.locateFile4ClassName(classname);

		try {
			String source = Helper.readFile((clientMethodFile));
			CompilationUnit cu = Helper.parserString(source);
			AddPCRVisitor0 visitor0 = new AddPCRVisitor0();
			visitor0.setCompositionInProgress(composition);
			visitor0.visit(cu, null);

			AddPCRVisitor visitor = new AddPCRVisitor();
			visitor.setCompositionInProgress(composition);
			visitor.visit(cu, null);
			Helper.writeFile(visitor.getSource(), (clientMethodFile.getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
