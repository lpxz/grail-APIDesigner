package utils;

import java.io.File;

import atomicCompositions.serialization.SerializableComposition;

public class CentralConstants {

	static final String		BENCHMARKS							= "benchmarks";
	public static boolean	predictive_locksetonly				= true;
	public static String	ACprefix							= "AC_";

	public static String	projectdirectory;															// to be set during the initialization.
	public static String	projectname;																// to be set during the initialization

	public static String	atomicCompositionsFolderName		= "atomic_compositions";
	public static String	atomicCompositionTodoQueueFolder	= "atomic_compositions_todoQueue";
	public static String	incorrectCompositionsDetected		= "incorrect_compositions_detected";
	public static String	incorrectCompsoitionsConfirmed		= "incorrect_compositions_confirmed";

	public static String	monitorClass						= "edu.hkust.clap.monitor.Monitor";

	public static String	mainclass							= "";
	public static String[]	args								= null;
	// for soot transform
	public static String[]	includes							= {};
	public static String[]	excludes							= {};
	public static boolean	printStack4ConfirmedBug				= false;

	public static String implMonitorSrc() {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "src"
				+ System.getProperty("file.separator")
				+ monitorClass.replace(".",
						System.getProperty("file.separator")) + ".java_impl";
	}

	public static String inUseMonitorSrc() {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "src"
				+ System.getProperty("file.separator")
				+ monitorClass.replace(".",
						System.getProperty("file.separator")) + ".java";
	}

	public static String stubMonitorSrc() {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "src"
				+ System.getProperty("file.separator")
				+ monitorClass.replace(".",
						System.getProperty("file.separator")) + ".java_stub";
	}

	public static String getFile4CompositionSet() {
		return System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.atomicCompositionsFolderName
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
	}

	public static String getFolder4TodoComposition() {
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.atomicCompositionTodoQueueFolder
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;

		return containerFolderName;

	}

	public static String getFile4TodoComposition(SerializableComposition ith) {
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.atomicCompositionTodoQueueFolder
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
		File containerFolder = new File(containerFolderName);
		try {
			if (!containerFolder.exists())
				containerFolder.mkdirs();
		} catch (Exception e) {
			throw new RuntimeException("check the write permission.");
		}

		return containerFolderName + System.getProperty("file.separator")
				+ ith.getId();

	}

	public static String getFile4CompositionDetected(SerializableComposition ith) {
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.incorrectCompositionsDetected
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
		File containerFolder = new File(containerFolderName);
		try {
			if (!containerFolder.exists())
				containerFolder.mkdirs();
		} catch (Exception e) {
			throw new RuntimeException("check the write permission.");
		}

		return containerFolderName + System.getProperty("file.separator")
				+ ith.getId();

	}

	public static String getFile4CompositionConfirmed(
			SerializableComposition ith) {
		String containerFolderName = System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ CentralConstants.incorrectCompsoitionsConfirmed
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;
		File containerFolder = new File(containerFolderName);
		try {
			if (!containerFolder.exists())
				containerFolder.mkdirs();
		} catch (Exception e) {
			throw new RuntimeException("check the write permission.");
		}

		return containerFolderName + System.getProperty("file.separator")
				+ ith.getId();

	}

}
