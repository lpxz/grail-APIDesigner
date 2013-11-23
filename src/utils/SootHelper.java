package utils;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;

public class SootHelper {

	public static int getLine(Stmt h) {

		if (h.hasTag("LineNumberTag")) {
			return ((LineNumberTag) h.getTag("LineNumberTag")).getLineNumber();
		}
		if (h.hasTag("SourceLineNumberTag")) {
			return ((SourceLineNumberTag) h.getTag("SourceLineNumberTag"))
					.getLineNumber();
		}
		if (h.hasTag("SourceLnPosTag")) {
			return ((SourceLnPosTag) h.getTag("SourceLnPosTag")).startLn();
		}
		return -1;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String getClassNameFromMethodSig(String clientMethodSig) {
		int leftbracket = clientMethodSig.indexOf("<");
		int maohao = clientMethodSig.indexOf(":");
		return clientMethodSig.substring(leftbracket + 1, maohao);

	}

	public static String hostClassOfInnerClass(String classname) {
		int dollar = classname.indexOf("$");
		if (dollar != -1) {
			return classname.substring(0, dollar);
		}

		return classname;
	}

	//<ClientClass: void clientMethod()>
	public static String getMethodNameFromMethodSig(String clientMethod2) {
		int leftBrace = clientMethod2.indexOf("(");
		String substring = clientMethod2.substring(0, leftBrace);
		int lastSpace = substring.lastIndexOf(" ");
		return substring.substring(lastSpace + 1);
	}

	public static int getArgCountFromMethodSig(String clientMethod2) {
		int leftBrace = clientMethod2.indexOf("(");
		int rightBrace = clientMethod2.indexOf(")");
		if (leftBrace + 1 == rightBrace)
			return 0;
		String argSeq = clientMethod2.substring(leftBrace + 1, rightBrace);

		return argSeq.split(",").length;
	}

	public static String[] getArgsFromMethodSig(String clientMethod2) {
		int leftBrace = clientMethod2.indexOf("(");
		int rightBrace = clientMethod2.indexOf(")");
		if (leftBrace + 1 == rightBrace)
			return null;
		String argSeq = clientMethod2.substring(leftBrace + 1, rightBrace);
		return argSeq.split(",");
	}

	public static boolean isConstructor(SootMethod sm) {
		if (sm.getName().equals(SootMethod.constructorName)
				|| sm.getName().equals(SootMethod.staticInitializerName))
			return true;
		return false;
	}

	public static String getSourceFileName(SootClass declaringClass) {
		if (declaringClass.hasTag("SourceFileTag")) {
			return ((SourceFileTag) declaringClass.getTag("SourceFileTag"))
					.getSourceFile(); // absolute path is not available.
		}
		return null;
	}

}
