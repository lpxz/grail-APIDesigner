package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectDirectoryHelper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CentralConstants.projectname = "Test";
		findSmallestTodoComposition();
		//		String givenArg = "-i org.apache -f J -pp -cp "
		//				+ "/Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene/lib/junit-3.8.2.jar:"
		//				+ "/Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene/lib/servlet-api-2.4.jar:"
		//				+ "/Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene/lib/commons-cli-1.1.jar:"
		//				+ "/Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene/bin"
		//				+ " -process-dir /Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene/bin";
		//		String[] finalStrings = givenArg.split(" ");
		//		String foundDir = findProjectDir(finalStrings);
		//		System.out.println(foundDir);
	}

	public static void setProject_Dir_Name(String[] finalArgs) {
		CentralConstants.projectdirectory = findProjectDir(finalArgs);
		CentralConstants.projectname = findProjectName(CentralConstants.projectdirectory);
		System.out.println("project directory: "
				+ CentralConstants.projectdirectory);
		System.out.println("project name: " + CentralConstants.projectname);

	}

	//	/Users/charlesz/git/git4api/APIDesigner/benchmarks/lucene
	private static String findProjectName(String projectdirectory) {
		int lastSlash = projectdirectory.lastIndexOf("/");
		if (lastSlash != -1)
			return projectdirectory.substring(lastSlash + 1);
		else {
			throw new RuntimeException(
					"Is it an eclipse project in the benchmarks folder?");
		}
	}

	//   <sysproperty key="projectdirctory" value="${user.dir}/benchmarks/lucene"/>   
	//  I assume the subject project is an eclipse project, i.e., the bin and src and in the same project root. 
	public static String findProjectDir(String[] finalArgs) {
		for (int i = finalArgs.length - 1; i >= 0; i--) {
			String sootArg = finalArgs[i];
			if (sootArg.contains(CentralConstants.BENCHMARKS)) {
				int benchmarkpos = sootArg.indexOf(CentralConstants.BENCHMARKS);
				int slashAfterProject = sootArg.indexOf("/", benchmarkpos
						+ CentralConstants.BENCHMARKS.length() + 1);
				int lastMaohao = sootArg.lastIndexOf(":");
				if (lastMaohao != -1) {
					return sootArg.substring(lastMaohao, slashAfterProject);
				} else {
					return sootArg.substring(0, slashAfterProject);
				}
			}
		}

		throw new RuntimeException(
				"I did not find the project directory!! Is it an eclipse project?");
	}

	public static String findProjectDirGivenName(String projectname) {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.BENCHMARKS
				+ System.getProperty("file.separator") + projectname;
	}

	public static Set<String>	hitJars	= new HashSet<String>();

	public static Set<String> searchForJarsInProject() {
		hitJars.clear();
		if (CentralConstants.projectdirectory == null)
			throw new RuntimeException("set the project directory first.");
		searchForJars(new File(CentralConstants.projectdirectory));
		return hitJars;
	}

	public static Set<String> searchForJarsInFolder(String folder) {
		hitJars.clear();
		searchForJars(new File(folder));
		return hitJars;
	}

	private static void searchForJars(File directory) {
		if (directory.isDirectory()) {
			if (directory.canRead()) {
				for (File temp : directory.listFiles()) {
					if (temp.isDirectory()) {
						searchForJars(temp);
					} else {
						if (temp.getName().contains(".jar")) {//.toLowerCase()
							hitJars.add(temp.getAbsolutePath());
						}
					}
				}
			} else {
				System.out.println(directory.getAbsoluteFile()
						+ "Permission Denied");
			}
		}
	}

	public static File	hit	= null;

	public static File searchForFile(String target) {
		if (CentralConstants.projectdirectory == null)
			throw new RuntimeException("set the project directory first.");
		search(new File(CentralConstants.projectdirectory), target);
		return hit;
	}

	private static void search(File directory, String target) {
		if (directory.isDirectory()) {
			if (directory.canRead()) {
				for (File temp : directory.listFiles()) {
					if (temp.isDirectory()) {
						search(temp, target);
					} else {
						if (target.equals(temp.getName())) {//.toLowerCase()
							hit = temp;
							//break;
						}
					}
				}
			} else {
				System.out.println(directory.getAbsoluteFile()
						+ "Permission Denied");
			}
		}
	}

	//	no need  to search, just  locate.
	//	class A$B: 1) A$B.java exists. 2) A.java exists and B is an inner class.
	public static File locateFile4ClassName(String classname) {
		String classFileName = CentralConstants.projectdirectory
				+ System.getProperty("file.separator") + "src"
				+ System.getProperty("file.separator")
				+ classname.replace(".", System.getProperty("file.separator"))
				+ ".java";
		File found = new File(classFileName);
		if (found.exists())
			return found;
		else {
			classFileName = CentralConstants.projectdirectory
					+ System.getProperty("file.separator")
					+ "src"
					+ System.getProperty("file.separator")
					+ SootHelper.hostClassOfInnerClass(classname).replace(".",
							System.getProperty("file.separator")) + ".java";
			found = new File(classFileName);
			if (found.exists())
				return found;
			else {
				throw new RuntimeException(
						"cannot find the source for the class: " + classname);
			}
		}
	}

	static List	list4findSmallest	= new ArrayList();

	public static String findSmallestTodoComposition() {

		list4findSmallest.clear();
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.atomicCompositionTodoQueueFolder
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
		File containerFolder = new File(containerFolderName);

		if (containerFolder.canRead()) {
			for (File temp : containerFolder.listFiles()) {
				list4findSmallest.add(temp.getAbsolutePath());
			}
		}

		if (list4findSmallest.size() == 0)
			return null;
		Collections.sort(list4findSmallest);// dir/0 < dir/1
		return (String) list4findSmallest.get(0);

	}

	public static String findSmallestIncorrectCompositionDetected() {

		list4findSmallest.clear();
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.incorrectCompositionsDetected
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
		File containerFolder = new File(containerFolderName);

		if (containerFolder.canRead()) {
			for (File temp : containerFolder.listFiles()) {
				list4findSmallest.add(temp.getAbsolutePath());
			}
		}

		if (list4findSmallest.size() == 0)
			return null;
		Collections.sort(list4findSmallest);// dir/0 < dir/1
		return (String) list4findSmallest.get(0);

	}

	public static Set<String> allJavaFiles(String folder) {
		hitJars.clear();
		allJavaFiles(new File(folder));
		return hitJars;
	}

	private static void allJavaFiles(File directory) {
		if (directory.isDirectory()) {
			if (directory.canRead()) {
				for (File temp : directory.listFiles()) {
					if (temp.isDirectory()) {
						allJavaFiles(temp);
					} else {
						if (temp.getName().endsWith(".java")) {//.toLowerCase()
							hitJars.add(temp.getAbsolutePath());
						}
					}
				}
			} else {
				System.out.println(directory.getAbsoluteFile()
						+ "Permission Denied");
			}
		}
	}

}
