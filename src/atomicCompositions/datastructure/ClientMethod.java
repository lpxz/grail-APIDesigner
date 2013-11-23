package atomicCompositions.datastructure;

import soot.Body;
import soot.SootMethod;

public class ClientMethod {

	SootMethod clientmethod;
	
	public ClientMethod(SootMethod clientmethod) {
	
		this.clientmethod = clientmethod;
	}
	
	public ClientMethod(Body body)
	{
		this.clientmethod=body.getMethod();
	}

	public SootMethod getMethod()
	{
		return  clientmethod;
	}
	public Body getBody()
	{
		return clientmethod.getActiveBody();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
