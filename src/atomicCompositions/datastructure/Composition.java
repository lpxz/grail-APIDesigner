package atomicCompositions.datastructure;

import java.util.Stack;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.toolkits.scalar.Pair;
import utils.SootHelper;

public class Composition {
	static int	id_generator	= 0;
	int			id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	Stmt			first;
	Stmt			last;
	ClientMethod	client_method;
	LibraryModule	library_module;

	Object			miscInfo;		// can be the dep chain.

	public Composition(Stmt first, Stmt last, ClientMethod client_method_arg,
			LibraryModule library_module_arg, Object miscInfo) {
		this.id = id_generator++;

		this.first = first;
		this.last = last;
		this.client_method = client_method_arg;
		this.library_module = library_module_arg;
		this.miscInfo = miscInfo;
	}

	public void setMiscInfo(Object miscInfo) {
		this.miscInfo = miscInfo;
	}

	public ClientMethod getClient_method() {
		return client_method;
	}

	public void setClient_method(ClientMethod client_method) {
		this.client_method = client_method;
	}

	public LibraryModule getLibrary_module() {
		return library_module;
	}

	public void setLibrary_module(LibraryModule library_module) {
		this.library_module = library_module;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static class Factory {
		public static Composition formAC(Stmt first, Stmt last, Body body) //shortcut for hotspot.
		{
			LibraryModule library_module_arg = null;
			if (first.getInvokeExpr().getMethod().getDeclaringClass() == last
					.getInvokeExpr().getMethod().getDeclaringClass()) {
				library_module_arg = LibraryModule.Factory
						.singleClassAsLibrary(first.getInvokeExpr().getMethod()
								.getDeclaringClass());
			}
			return new Composition(first, last, new ClientMethod(body),
					library_module_arg, null);
		}
	}

	public void print() {
		System.out
				.println("\nAn atomic composition (AC) found by ICfinder-USE in method "
						+ client_method.getMethod().getName()
						+ " of class "
						+ client_method.getMethod().getDeclaringClass()
								.getName());

		System.out.println("invocation at line " + SootHelper.getLine(first)
				+ ": " + first);
		System.out.println("invocation at line " + SootHelper.getLine(last)
				+ ": " + last);
		System.out.println(" \n");

		if (miscInfo != null) {
			System.out.println("Dep chain:");
			Stack depChainStack = (Stack) miscInfo;
			for (int i = 0; i < depChainStack.size(); i++) {
				System.out.println(depChainStack.get(i));
			}
		}

	}

	public Stmt getFirst() {
		return first;
	}

	public Stmt getLast() {
		return last;
	}

	//========hashset-related:
	// two compositions that with the same <first,last> combination are treated equivalent.
	// The same <first,last> combination means that the two compositions involve the identical pair of invocation sites (site-sensitive).
	@Override
	public int hashCode() {
		int o1hash = 0;
		int o2hash = 0;
		if (first != null)
			o1hash = first.hashCode();
		else {
		}
		if (last != null)
			o2hash = last.hashCode();
		else {
		}

		return o1hash + o2hash;
	}

	@Override
	public boolean equals(Object otherobj) {
		if (otherobj instanceof Composition) {
			Composition other = (Composition) otherobj;
			return (first.equals(other.first) && last.equals(other.last))
					|| (first.equals(other.last) && last.equals(other.first));

		} else {
			return false;
		}

	}

}
