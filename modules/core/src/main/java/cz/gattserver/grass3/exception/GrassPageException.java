package cz.gattserver.grass3.exception;

public class GrassPageException extends RuntimeException {

	private static final long serialVersionUID = -8947153927785372443L;

	private final int status;

	public GrassPageException(int status, Throwable e) {
		super("Error: " + status, e);
		this.status = status;
	}

	public GrassPageException(int status, String msg, Throwable e) {
		super("Error: " + status + ", " + msg, e);
		this.status = status;
	}

	public GrassPageException(int status) {
		super("Error: " + status);
		this.status = status;
	}

	public GrassPageException(int status, String msg) {
		super("Error: " + status + ", " + msg);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
