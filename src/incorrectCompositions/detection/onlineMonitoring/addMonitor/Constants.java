package incorrectCompositions.detection.onlineMonitoring.addMonitor;



public class Constants {
// note that not all constants are included here.
	public static final String UTIL_CLASSNAME = "edu.hkust.clap.Util";
	public static final String CRASH_ANNOTATION="Crashed_with";
	public static final String CARE_ANNOTATION="Cared_by_CLAP_";
	
	public static final String CATCH_EXCEPTION_SIG = "<edu.hkust.clap.monitor.Monitor: void crashed(java.lang.Throwable)>";
	public static final String CARED_BY_CLAP_SIG = "<edu.hkust.clap.monitor.Monitor: void caredByClap(java.lang.String,java.lang.Object)>";

	public static String PHASE_RECORD ="runtime";
}
