package incorrectCompositions.detection.onlineMonitoring.addMonitor;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.parser.node.TMinus;
import utils.CentralConstants;
import utils.ProjectDirectoryHelper;

public class AddMonitorUtil {

	static String[]	unInstruClasses	= { "org.apache.log4j.", "jrockit.",
			"java.", "javax.",
			"xjava.",
			"COM.",
			"com.",
			"cryptix.",
			"sun.",
			"sunw.",
			"junit.",
			"org.junit.",
			"org.xmlpull.",
			"edu.hkust.clap.",
			// the following are copied frpm the transforming options. (commandLine)
			"org.apache.commons.logging.",// annoying, ban it
			"org.apache.xalan.", "org.apache.xpath.", "org.springframework.",
			"org.jboss.", "jrockit.", "edu.", "checkers.",
			"org.codehaus.spice.jndikit.",
			"EDU.oswego.cs.dl.util.concurrent.WaiterPreferenceSemaphore",
			"soot.", "aTSE.", "pldi.", "popl.", "beaver.", "org.jgrapht",
			"ca.pfv.spmf.", "japa.parser.", "polyglot."

									// org.w3c. is the including option			
									};

	public static String makeArgumentName(int argOrder) {
		if (argOrder == 0) {
			return "this";
		}

		return "arg_" + argOrder;
	}

	public static String transClassNameDotToSlash(String name) {
		return name.replace('.', '/');
	}

	public static String transClassNameSlashToDot(String name) {
		return name.replace('/', '.');
	}

	public static boolean isRunnableSubType(SootClass c) {
		if (c.implementsInterface("java.lang.Runnable"))
			return true;
		if (c.hasSuperclass())
			return isRunnableSubType(c.getSuperclass());
		return false;
	}

	public static boolean shouldInstruThisClass(String scname) {
		for (int k = 0; k < unInstruClasses.length; k++) {
			if (scname.startsWith(unInstruClasses[k])) {
				return false;
			}
		}

		return true;
	}

	public static boolean shouldInstruThisMethod(String smname) {
		if (smname.contains("<clinit>")) {
			return false;
		}

		return true;
	}

	public static void resetParameters() {
		JTPTransformer.isMethodRunnable = false;
		JTPTransformer.isMethodMain = false;
		JTPTransformer.isMethodSynchronized = false;
		JTPTransformer.isMethodStaticNonPara = false;

	}

	public static boolean instruThisType(Type type) {
		if (type instanceof RefType) {
			if (AddMonitorUtil.shouldInstruThisClass(type.toString()))
				//return true;
				return false;
		}
		return false;
	}

	public static String getAncestorClassName(SootClass sc1) {
		SootClass sc2 = sc1.getSuperclass();
		while (shouldInstruThisClass(sc2.getName())) {
			sc1 = sc2;
			sc2 = sc1.getSuperclass();
		}
		return sc1.getName();
	}

	public static void storeObject(Object o, ObjectOutputStream out) {
		try {
			out.writeObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String fullnameOfMethod(SootMethod sm) {
		return sm.getDeclaringClass().getName() + "." + sm.getName();

	}

	public static String getTmpRuntimeProjectFolder() {
		String ret = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "tmp"
				+ System.getProperty("file.separator") + Constants.PHASE_RECORD
				+ System.getProperty("file.separator")
				+ CentralConstants.projectname;//Util.getTmpDirectory();//.replace("\\", "\\\\")
		File file = new File(ret);
		if (!file.exists())
			file.mkdirs();
		return ret;
	}

	// please set it accordingly in the CentralConstants.class
	public static String includeArgString() {
		String includeArgString = "";
		String[] includes = CentralConstants.includes;
		for (String includeItem : includes) {
			includeArgString += " -i " + includeItem;
		}
		return includeArgString;
	}

	public static String excludeArgString() {
		String excludeArgString = "";
		String[] excludes = CentralConstants.excludes;
		for (String excludeItem : excludes) {
			excludeArgString += " -x " + excludeItem;
		}
		return excludeArgString;

	}

	// automatically compute it: bin:lib/*.jar
	public static Set<String>	tmp4classpath	= new HashSet<String>();

	// please update your eclipse project accordingly, if they do not conform to these conventional names.
	// please remove the conflicting jars or the jars which are not used.
	public static String classpath() {
		tmp4classpath.add(CentralConstants.projectdirectory
				+ System.getProperty("file.separator") + "bin");
		String libfolder = CentralConstants.projectdirectory
				+ System.getProperty("file.separator") + "lib";

		tmp4classpath.addAll(ProjectDirectoryHelper
				.searchForJarsInFolder(libfolder));
		StringBuilder ret = new StringBuilder();
		for (String tmp : tmp4classpath) {
			// soot always use : as the deliminator for its cp.
			ret.append(tmp + ":");
		}

		return ret.toString().substring(0, ret.length() - 1);// remove the last :.
	}

	public static void main(String[] args) {
		CentralConstants.projectdirectory = "/Users/charlesz/git/git4api/APIDesigner";
		System.out.println(classpath());
	}
}
