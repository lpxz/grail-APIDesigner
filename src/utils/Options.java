package utils;

public class Options {
	public static boolean recordDepChain =false;
	public static int muvi_threshold = 2;
	public static boolean print_AC_USE_Depchain = false;
	public static boolean print_AC_MUVI = false;
	public static boolean print_AC_COMP = true;
	// ===========AEC options:
	public static boolean print_AC_USE = true;
	public static boolean interPDataDependent = true;
	static boolean checkForCandidateCorrelation = true;
	protected static boolean reportDepChain = false;
	public static boolean removeAlreadyAtom = true;
	public static void setOptions()// (String project)
	{
		//lpxz: set your wanted option values at will.
		print_AC_USE = true;// options.getProperty("print_AC_USE").equals("true");
		print_AC_COMP = true; // options.getProperty("print_AC_COMP").equals("true");
		print_AC_MUVI = true; // options.getProperty("print_AC_MUVI").equals("true");
		print_AC_USE_Depchain = true; // options.getProperty("print_AC_USE_Depchain").equals("true");
		muvi_threshold = 2;// Integer.parseInt(options.getProperty("print_AC_MUVI_minSupport"));
		System.out.println("The options used in this run are: ");
		System.out.println("print ACs found by ICFinder-USE: " + print_AC_USE);
		System.out
				.println("print ACs found by ICFinder-COMP: " + print_AC_COMP);
		System.out.println("print ACs found by MUVI: " + print_AC_MUVI);
		System.out
				.println("For ICfinder-USE, print the dependency chain involved in each AC: "
						+ print_AC_USE_Depchain);
		System.out.println("Set the MUVI parameter minSupport to: "
				+ muvi_threshold);
	
	}
	public static boolean	useasmStack	= true;

}
