package atomicCompositions.datastructure;

import java.util.ArrayList;
import java.util.List;

import soot.SootClass;

public class LibraryModule {
	// lpxz:
	// it contains the classes which host the fields in an atomic set.
	// The naive case is that, every class forms a library module, where the fields of the class form an atomic set.
	// A more advanced case is that, one could infer, from existing atomic blocks, the atomic set and library module.
	// we follow the naive case in this implementation currently.

    List<SootClass> libClasses ;
    
    
	public LibraryModule(List<SootClass> libClasses) {
		this.libClasses = libClasses;
	}

	public boolean include(SootClass libclass_arg)
	{
		return libClasses.contains(libclass_arg);
	}
	
	

	public static void main(String[] args) {
	 LibraryModule.Factory.singleClassAsLibrary(null);

	}
	
	public static class Factory
	{
		public static LibraryModule singleClassAsLibrary(SootClass sc)
		{
			List<SootClass> list= new ArrayList<SootClass>();
			list.add(sc);
			return new LibraryModule(list);
			
		}
	}

}
