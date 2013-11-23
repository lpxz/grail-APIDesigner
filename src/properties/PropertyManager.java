package properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Properties;

public class PropertyManager {

	public static boolean		allAreInteresting	= true;
	static HashSet<String>		invokes				= null;
	public static boolean		lpxzonly			= false;
	// public static String origAnalyzedFolder =
	// props.getProperty("origAnalyzedFolder");
	// //"/home/lpxz/eclipse/workspace/app/bin";////"/home/lpxz/eclipse/workspace/openjms/bin";
	public static String		mainClassArg		= null;							// props.getProperty(projectname+"_monitor_mainClassArg");
																						// //"benchmarks.dstest.MTLinkedListInfiniteLoop";//System.getProperty("mainclass");//"driver.OpenJMSDriver";

	// must set the project name, always
	public static String		projectname			= null;
	public static Properties	props;

	public static boolean		reportTime			= true;

	// public static boolean useContext4InterestingMethod= true;// try it
	// ant cannot recognize the current project properly when being invoked in a
	// different project.
	public static String		SystemUserDir		= System.getProperty("user.dir");	// "/home/lpxz/eclipse/workspace/pecan/pecan-monitor";

	public static boolean		useasmStack			= true;

	//public static boolean		usePostStack		= false;
	static {
		props = loadProperties(System.getProperty("user.dir") + "/properties");

	}

	public static void initialize(String project) {
		PropertyManager.projectname = project;
		mainClassArg = props.getProperty(projectname + "_monitor_mainClassArg"); // "benchmarks.dstest.MTLinkedListInfiniteLoop";//System.getProperty("mainclass");//"driver.OpenJMSDriver";

	}

	public static boolean isInteresting(String sootmethodsig) {

		if (allAreInteresting) {
			return true;
		} else {
			if (invokes == null) {
				loadInterestingInvokedMethods();
				// for(String str: invokes)
				// System.out.println(str);
			}

			return invokes.contains(sootmethodsig);// exactly the same format,
													// no need to transform
		}

	}

	private static void loadInterestingInvokedMethods() {

		// save to the project's name! in the folder InvokedMethods/

		String filename = "/home/lpxz/eclipse/workspace/APIDesigner/InvokedMethods/"
				+ projectname + "_indicator";
		ObjectInputStream in = null;
		try {

			FileInputStream fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			invokes = (HashSet<String>) in.readObject();

			// for(String invoke : invokes)
			// {
			// System.err.println(invoke);
			// }
		} catch (Exception Exception) {
			System.out.println("dump error!");
		}

	}

	public static Properties loadProperties(String filename) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(filename));
		} catch (IOException e) {
		}
		return properties;
	}

	public static void main(String[] args) {

	}

}
