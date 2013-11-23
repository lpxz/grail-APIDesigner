package atomicCompositions.serialization;

import java.io.Serializable;
import java.util.List;

import utils.SootHelper;
import atomicCompositions.datastructure.Composition;

public class SerializableComposition implements Serializable {

	int		id;
	//invokes
	String	firstInvokedAPI;
	String	lastInvokedAPI;
	int		firstInvokeSite;
	int		lastInvokeSite;

	//client and library
	String	libClass;

	//optional: pecan will fill into these fields to help the witness.
	String	rInvokeAPI;

	public String getrInvokeAPI() {
		return rInvokeAPI;
	}

	public void setrInvokeAPI(String rInvokeAPI) {
		this.rInvokeAPI = rInvokeAPI;
	}

	public int getrInokeSite() {
		return rInokeSite;
	}

	public void setrInokeSite(int rInokeSite) {
		this.rInokeSite = rInokeSite;
	}

	public String getrInvokeContainerMethodString() {
		return rInvokeContainerMethodString;
	}

	public void setrInvokeContainerMethodString(
			String rInvokeContainerMethodString) {
		this.rInvokeContainerMethodString = rInvokeContainerMethodString;
	}

	int		rInokeSite;
	String	rInvokeContainerMethodString;

	public int getId() {

		return id;
	}

	public String getFirstInvokedAPI() {
		return firstInvokedAPI;
	}

	public String getLastInvokedAPI() {
		return lastInvokedAPI;
	}

	public int getFirstInvokeSite() {
		return firstInvokeSite;
	}

	public int getLastInvokeSite() {
		return lastInvokeSite;
	}

	public String getLibClass() {
		return libClass;
	}

	public String getClientMethod() {
		return clientMethod;
	}

	String	clientMethod;

	public void print() {
		System.out.println("id: " + id + " clientMethod:" + clientMethod
				+ " firstInvoke: " + firstInvokedAPI + " lastInvoke: "
				+ lastInvokedAPI);
	}

	public static class Factory {
		public static SerializableComposition toSerializableComposition(
				Composition composition) {
			SerializableComposition serializableComposition = new SerializableComposition();
			serializableComposition.id = composition.getId();
			serializableComposition.firstInvokedAPI = composition.getFirst()
					.getInvokeExpr().getMethod().getSignature();
			serializableComposition.lastInvokedAPI = composition.getLast()
					.getInvokeExpr().getMethod().getSignature();
			serializableComposition.firstInvokeSite = SootHelper
					.getLine(composition.getFirst());
			serializableComposition.lastInvokeSite = SootHelper
					.getLine(composition.getLast());
			serializableComposition.clientMethod = composition
					.getClient_method().getMethod().getSignature();
			serializableComposition.libClass = composition.getFirst()
					.getInvokeExpr().getMethod().getDeclaringClass().getName();//composition.getLibrary_module(). this field is not  important. lpxz
			return serializableComposition;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
