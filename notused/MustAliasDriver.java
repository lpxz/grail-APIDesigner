package atomicCompositions.analysis;

import java.io.BufferedWriter;
import java.io.IOException;

import soot.Pack;
import soot.Scene;
import soot.SootClass;
import soot.Transform;

public class MustAliasDriver {

	public static String			syncKeyWord	= "entermonitor";
	public static BufferedWriter	bWriter		= null;

	public static void main(String[] args) throws IOException {
	}

	// List entryPoints=EntryPoints.v().methodsOfApplicationClasses();
	// List mainEntries = new ArrayList();
	// for(int i=0;i< entryPoints.size(); i++)
	// {
	// if(entryPoints.get(i).toString().contains("main"))
	// {TestDominationOfCS
	// mainEntries.add(entryPoints.get(i));
	// }
	// }
	//
	// soot.Scene.v().setEntryPoints(mainEntries); // process : app and its
	// reachable methods.

	private static void addgetPDGPackToJtp(Pack jtp) {
		// the implementation is too fuzzy and the interface is really bad. switch to indus.
		// hope it works, I do not want to move to wala now!!
		jtp.add(new Transform("jtp.getDeepDDG", new MustAliasTransformer()));
	}

	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main)
			Scene.v().setMainClass(c);
		return c;
	}

	public void testGetShimpleBody() {
		// fail("Not yet implemented"); // TODO
	}

}
