package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;

public class Remover {

	public static void removeFile(String filename) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("delete", target);
		Delete delete = new Delete();

		delete.setFile(new File(filename));

		delete.setProject(project);
		target.addTask(delete);
		target.execute();
	}

	public static void removeDir(String dirname) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("delete", target);
		Delete delete = new Delete();

		delete.setDir(new File(dirname));
		//		  delete.setVerbose(true);
		delete.setIncludeEmptyDirs(true);

		delete.setProject(project);
		target.addTask(delete);
		target.execute();

	}

	public static void main(String[] args) {
		removeFile(System.getProperty("user.dir")
				+ "/benchmarks/cache4j/runPeng.xml");
		removeDir(System.getProperty("user.dir") + "/benchmarks/cache4j");

	}

}
