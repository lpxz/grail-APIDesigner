package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

public class Copier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void copyDirFromTo(String fromdir, String todir) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("copy", target);
		Copy scp = new Copy();

		FileSet fromset = new FileSet();
		fromset.setDir(new File(fromdir));
		scp.addFileset(fromset);

		scp.setTodir(new File(todir));

		scp.setOverwrite(true);
		//		  scp.setForce(true);

		scp.setProject(project);
		target.addTask(scp);
		target.execute();

	}

	public static void copyFileFromTo(String oldfilepath, String newfilepath) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("copy", target);
		Copy scp = new Copy();

		scp.setFile(new File(oldfilepath));

		scp.setTofile(new File(newfilepath));

		scp.setOverwrite(true);
		//		  scp.setForce(true);

		scp.setProject(project);
		target.addTask(scp);
		target.execute();

	}

}
