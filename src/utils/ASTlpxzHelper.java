package utils;


import java.util.Stack;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

public class ASTlpxzHelper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	//soot:    public static void main(java.lang.String[])
	//ast:     public static void main(String[] args) 
	public static boolean checkMethod(MethodDeclaration n, String clientMethod2) {
		if (n.getName().equals(
				SootHelper.getMethodNameFromMethodSig(clientMethod2))) {
			if (n.getParameters() == null
					&& SootHelper.getArgCountFromMethodSig(clientMethod2) == 0)
				return true;
			if (n.getParameters().size() == SootHelper
					.getArgCountFromMethodSig(clientMethod2)) {
				if (n.getParameters().size() == 0)
					return true;
				else {
					String[] sootargs = SootHelper
							.getArgsFromMethodSig(clientMethod2);
					if (n.getParameters() == null || sootargs == null)
						throw new RuntimeException("check arglist size!");
					for (int i = 0; i < n.getParameters().size(); i++) {
						Parameter paraI = n.getParameters().get(i);
						String sootargI = sootargs[i];
						if (!ASTlpxzHelper.typeEquivalent(paraI.getType().toString(),
								sootargI))
							return false;
					}
					return true; // great.
				}
	
			}
	
		}
	
		return false;
	}

	// about inner class.
	//soot: Employee$Internal
	//ast: Employee.Internal
	public static boolean typeEquivalent(String asttype, String sootargI) {
		sootargI = sootargI.replace("$", ".");
		asttype = asttype.replace("$", "."); // ignore the $ issues.
		if (sootargI.contains(asttype))
			return true;
		else {
			return false;
		}
	}

	public static boolean checkClass(Stack innerClassStack2, String libClass) {
		String astClass = ASTlpxzHelper.getCurrentClass(innerClassStack2);
		astClass = astClass.replace("$", ".");
		libClass = libClass.replace("$", ".");
		if (astClass.equals(libClass))
			return true;
		else
			return false;
	}

	public static String getCurrentClass(Stack innerClassStack2) {
		String toret = "";
		Object[] array = innerClassStack2.toArray();
		for (int i = 0; i < array.length; i++) {
			toret += array[i].toString() + ".";
		}
		return toret.substring(0, toret.length() - 1);// remove the last "."
	}

}
