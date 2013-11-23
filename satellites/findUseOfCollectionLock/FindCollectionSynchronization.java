package findUseOfCollectionLock;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.test.Helper;

import java.io.File;
import java.util.List;
import java.util.Set;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import utils.SootHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class FindCollectionSynchronization {

	// handle one composition at once!
	// do not be too agressive.
	public static void main(String[] args) {
		Set<String> files = ProjectDirectoryHelper
				.allJavaFiles("/Users/charlesz/eclipse/workspace/luceneCopy/src");//TODO fill in the source folder.
		for (String filename : files) {
			try {
				findCollectionLock(filename);
			} catch (Throwable e) {
				// TODO: handle exception
			}

		}

	}

	public static void findCollectionLock(String filename) {

		File clientMethodFile = new File(filename);
		try {
			String source = Helper.readFile((clientMethodFile));
			CompilationUnit cu = Helper.parserString(source);
			FindCollectionSynchronizationVisitor visitor = new FindCollectionSynchronizationVisitor();
			visitor.visit(cu, null);
			Helper.writeFile(visitor.getSource(), (clientMethodFile.getPath()));
		} catch (Throwable e) {
		}
	}

}
