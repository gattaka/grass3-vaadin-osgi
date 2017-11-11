package cz.gattserver.grass3.exception;

public class GrassPageException extends RuntimeException {

	private static final long serialVersionUID = -8947153927785372443L;

	private int status;

	public GrassPageException(int status) {
		super("Error: " + status);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
