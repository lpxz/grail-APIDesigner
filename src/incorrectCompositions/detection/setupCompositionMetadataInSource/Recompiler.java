package incorrectCompositions.detection.setupCompositionMetadataInSource;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;

public class Recompiler {

	public static void main(String[] args) {
		javac("/Users/charlesz/git/git4api/APIDesigner");
		//		javac("/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/src",
		//				"/Users/charlesz/git/git4api/APIDesigner/benchmarks/Test/bin",
		//				null);
	}

	public static void javac(String eclipseProject) {
		javac(eclipseProject + System.getProperty("file.separator") + "src",
				eclipseProject + System.getProperty("file.separator") + "bin",
				eclipseProject + System.getProperty("file.separator") + "lib");
	}

	public static Set<String>	tmp4classpath	= new HashSet<String>();

	// please update your eclipse project accordingly, if they do not conform to these conventional names.
	// please remove the conflicting jars or the jars which are not used.
	public static String libjarCP(String libfolder) {
		tmp4classpath.clear();
		//		tmp4classpath.add(CentralConstants.projectdirectory
		//				+ System.getProperty("file.separator") + "bin");

		tmp4classpath.addAll(ProjectDirectoryHelper
				.searchForJarsInFolder(libfolder));
		StringBuilder ret = new StringBuilder();
		for (String tmp : tmp4classpath) {
			// soot always use : as the deliminator for its cp.
			ret.append(tmp + ":");
		}
		if (ret.length() == 0)
			return "";// special case.
		return ret.toString().substring(0, ret.length() - 1);// remove the last :. else case.
	}

	public static void javac(String src, String bin, String libdir) {
		Javac compiler = new Javac();

		compiler.setProject(new Project());
		Path srcPath = new Path(compiler.getProject(), src);
		compiler.setFork(false);
		compiler.setSrcdir(srcPath);

		//++++++++++++++++++
		// stupid javac ant task does not know to overwrite existing class, so I have to remove old classes manually.
		Remover.removeDir(bin);

		File binFolder = new File(bin);
		if (binFolder.exists())
			throw new RuntimeException(
					" the old bin folder should have been removed");
		binFolder.mkdirs();
		//++++++++++++++++++

		System.out.println(libdir); // LPXZ

		String libclasspath = libjarCP(libdir);
		if (!libclasspath.isEmpty()) {
			compiler.setClasspath(new Path(compiler.getProject(), libclasspath));

		}

		compiler.setDestdir(new File(bin));
		compiler.setSource("1.6");
		compiler.setTarget("1.6");
		compiler.setDebug(true);
		compiler.setDebugLevel("lines,vars,source");

		//		compiler.setVerbose(true);

		try {
			compiler.execute();
		} catch (BuildException e) {
			e.printStackTrace();
		}

	}
}
