package atomicCompositions.datastructure;

import java.util.ArrayList;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;

public class ClientViewOfLibrary {

	ClientMethod	client_method;
	LibraryModule	library_moduleLibraryModule;

	//important
	List<Stmt>		invokes	= new ArrayList<Stmt>();

	public ClientViewOfLibrary(ClientMethod client_method,
			LibraryModule library_moduleLibraryModule) {

		this.client_method = client_method;
		this.library_moduleLibraryModule = library_moduleLibraryModule;
	}

	public ClientViewOfLibrary(SootMethod client_method, // hotspot constructor
			SootClass libClass) {
		this.client_method = new ClientMethod(client_method);
		this.library_moduleLibraryModule = LibraryModule.Factory
				.singleClassAsLibrary(libClass);
	}

	public void addStmt(Stmt invoke_arg) // uniqueness of items.
	{
		if (!invokes.contains(invoke_arg))
			invokes.add(invoke_arg);
	}

	public List<Stmt> getInvokes() {
		return invokes;
	}

	public void setInvokes(List<Stmt> invokes) {
		this.invokes = invokes;
	}

	public ClientMethod getClient_method() {
		return client_method;
	}

	public LibraryModule getLibrary_moduleLibraryModule() {
		return library_moduleLibraryModule;
	}

	public boolean equivalent(Body b, SootClass libClass) {
		if (client_method.getBody() == b
				&& library_moduleLibraryModule.include(libClass))
			return true;
		return false;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void print() {
		System.out.println("client method: "
				+ client_method.clientmethod.toString());
		System.out.println("lib APIs invoked: ");
		for (Stmt stmt : invokes) {
			System.out.println(stmt);
		}
	}

}
