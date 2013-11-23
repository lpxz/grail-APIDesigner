package incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context;

public class PreciseSTEContextHelper {
	public static String getArgList_sootsig(String sootsig) {
		int lastleftBrace = sootsig.lastIndexOf('(');
		int lastrightBrace = sootsig.lastIndexOf(')');
		return sootsig.substring(lastleftBrace + 1, lastrightBrace);
	}

	public static String getClass(String ste) {

		int leftBrace = ste.indexOf('(');
		String cm = ste.substring(0, leftBrace);
		int lastDotInCm = cm.lastIndexOf('.');
		return cm.substring(0, lastDotInCm);

	}

	public static String getClassName_sootsig(String sootsig) {
		int lbrace = sootsig.indexOf('<');
		int maohao = sootsig.indexOf(':');
		return sootsig.substring(lbrace + 1, maohao);
	}

	public static String getFullMethodArg_sootsig(String sootsig) {
		return getClassName_sootsig(sootsig) + "."
				+ getMethName_sootsig(sootsig) + "("
				+ getArgList_sootsig(sootsig) + ")";
	}

	public static int getInvokeLine(String steString) {
		if (steString.indexOf(':') == -1)
			throw new RuntimeException("no line no available");
		else {
			int maohao = steString.indexOf(':');
			int rightBrace = steString.lastIndexOf(')');
			String lineString = steString.substring(maohao + 1, rightBrace);
			return Integer.parseInt(lineString);
		}
	}

	public static String getMethName_sootsig(String sootsig) {
		;
		int lastleftBrace = sootsig.lastIndexOf('(');
		String beforeArg = sootsig.substring(0, lastleftBrace);
		int lastBlank = beforeArg.lastIndexOf(' ');
		return beforeArg.substring(lastBlank + 1);
	}

	public static String getMethod(String ste) {

		int leftBrace = ste.indexOf('(');
		String cm = ste.substring(0, leftBrace);
		int lastDotInCm = cm.lastIndexOf('.');
		return cm.substring(lastDotInCm + 1);
	}

	// CatchStackLPXZ.getFullStackTrace_lpxz()(CatchStackLPXZ.java:117)
	// MainThread.test(int,int)(MainThread.java:60)
	// MainThread.main(java.lang.String[])(MainThread.java:39)

	public static String getMethodFromSig(String sig) {
		int leftBrace = sig.indexOf('(');
		return sig.substring(0, leftBrace);
	}

	// ============================
	// <spec.jbb.JBButil: void setLog(java.util.logging.Logger)>

	public static String getMethodSig(String ste) {
		int leftbrace = ste.indexOf('(');
		int rightbrace = ste.indexOf(')');
		String arg = ste.substring(leftbrace + 1, rightbrace);

		return getMethod(ste) + "(" + arg + ")";

	}

	public static String getPartInLastBrace(String steString) {
		int lastleftBrace = steString.lastIndexOf('(');
		int lastrightBrace = steString.lastIndexOf(')');
		return steString.substring(lastleftBrace + 1, lastrightBrace);
	}

	public static void main(String[] args) {
		String sootsig = "<spec.jbb.JBButil: void setLog(java.util.logging.Logger)>";

		System.out.println(getMethName_sootsig(sootsig));
		System.out.println(getArgList_sootsig(sootsig));

	}

	public static String tryRemoveLineSpec(String steString) {
		if (steString.indexOf(':') == -1)
			return steString;// does not contain line NO at all
		else {
			int maohao = steString.indexOf(':');

			return steString.substring(0, maohao) + ")";
		}
	}

	public static String getSootMethodSig(String stelast) {
		return "<" + getClass(stelast) + ": ANYTYPE " + getMethodSig(stelast)+">";
	}
}
