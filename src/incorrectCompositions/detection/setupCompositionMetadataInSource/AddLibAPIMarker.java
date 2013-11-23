package incorrectCompositions.detection.setupCompositionMetadataInSource;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.test.Helper;

import java.io.File;
import java.util.List;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class AddLibAPIMarker {

	public static void main(String[] args) {
		CentralConstants.projectname = "Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);
		List<SerializableComposition> deserializedCompositions = (List<SerializableComposition>) Serializer
				.deserialize(CentralConstants.getFile4CompositionSet());
		for (SerializableComposition composition : deserializedCompositions) {
			SourceVersionControl.backupDir(CentralConstants.projectdirectory);
			addLibAPIMarker(composition);
			Recompiler.javac(CentralConstants.projectdirectory);
			// predictive analysis here.
			SourceVersionControl.restoreDir(CentralConstants.projectdirectory);

		}

	}

	// the general rule is, the API is treated as a primitive write.
	// To achieve this, we do the follwoing steps:
	// 1) add the marker field access.
	// 2) remove the synchronized keywords. because the synchronized keyword may prevent the predictive analysis from permutating the trace.

	// after confirmation, predictive analysis is not affected by the sync keyword.
	// in details: given the trace, sync{a;}  sync{b;}  sync{c;}, where a; and b; are in a composition,  sync{c;} can be moved before sync{b;}.
	// so, the 2) is not necessary.
	public static void addLibAPIMarker(SerializableComposition composition) {
		System.err.println("addLibAPIMarker:");
		composition.print();

		String libClassName = composition.getLibClass();
		File clientMethodFile = ProjectDirectoryHelper
				.locateFile4ClassName(libClassName);

		try {
			// do not reorder the two steps in the following. 
			// 1 depends on the sync keywords which are removed by 2.
			// add marker field access.
			String source = Helper.readFile((clientMethodFile)); //visitor0.getSource();
			CompilationUnit cu = Helper.parserString(source);
			AddLibAPIMarkerVisitor visitor = new AddLibAPIMarkerVisitor();//process it.
			visitor.setCompositionInProgress(composition);
			visitor.visit(cu, null);
			Helper.writeFile(visitor.getSource(), (clientMethodFile.getPath()));

			// remove synchronized keywords.
			//			String source0 = Helper.readFile((clientMethodFile));
			//			CompilationUnit cu0 = Helper.parserString(source0);
			//			RemoveSynchronizationVisitor visitor0 = new RemoveSynchronizationVisitor();//process it.
			//			visitor0.setCompositionInProgress(composition);
			//			visitor0.visit(cu0, null);
			//			Helper.writeFile(visitor0.getSource(), (clientMethodFile.getPath()));

			//			System.out.println(visitor.getSource());

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

}
