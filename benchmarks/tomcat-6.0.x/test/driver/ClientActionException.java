package driver;


public class ClientActionException extends Exception {

	public ClientActionException() {
		super();
	}
	

	public ClientActionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ClientActionException(String arg0) {
		super(arg0);
	}

	public ClientActionException(Throwable arg0) {
		super(arg0);
	}

}
