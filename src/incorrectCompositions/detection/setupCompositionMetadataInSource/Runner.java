package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import utils.CentralConstants;

public class Runner {

	public static void main(String[] args) {
		//		javac("/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/src",
		//				"/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/bin",
		//				null);
		run("ClientClass", "",
				"/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test");
	}

	public static void run(String mainclass, String argline,
			String eclipseProject) {
		run(mainclass, argline,
				eclipseProject + System.getProperty("file.separator") + "bin",
				eclipseProject + System.getProperty("file.separator") + "lib");
	}

	public static void run(String mainclass, String argline, String bin,
			String libdir) {
		Java runner = new Java();

		runner.setProject(new Project());
		runner.setFork(true);
		runner.setClassname(mainclass);
		runner.setArgs(argline);
		runner.setClasspath(new Path(runner.getProject(), bin));
		runner.setClasspath(new Path(runner.getProject(), libdir));
		runner.setClasspath(new Path(runner.getProject(), System
				.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "bin"));// RuntimeLibrary is needed.
		runner.setOutput(new File(System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "outputs"
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname + "Out"));
		runner.setError(new File(System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "outputs"
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname + "Error"));

		try {
			runner.execute();
		} catch (BuildException e) {
			e.printStackTrace();
		}

	}
}
