package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.FileSet;

public class Mover {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void moveDirFromTo(String fromdir, String todir) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("move", target);
		Move mover = new Move();

		FileSet fromset = new FileSet();
		fromset.setDir(new File(fromdir));
		mover.addFileset(fromset);

		mover.setTodir(new File(todir));

		mover.setOverwrite(true);
		//		  scp.setForce(true);

		mover.setProject(project);
		target.addTask(mover);
		target.execute();

	}

	public static void moveFileFromTo(String oldfilepath, String newfilepath) {

		Project project = new Project();
		Target target = new Target();
		project.addTarget("move", target);
		Move mover = new Move();

		mover.setFile(new File(oldfilepath));

		mover.setTofile(new File(newfilepath));

		mover.setOverwrite(true);
		//		  scp.setForce(true);

		mover.setProject(project);
		target.addTask(mover);
		target.execute();

	}

}
