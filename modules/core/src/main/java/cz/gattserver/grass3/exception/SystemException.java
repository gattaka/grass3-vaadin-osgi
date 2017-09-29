package cz.gattserver.grass3.exception;

public class SystemException extends ApplicationException {

	private static final long serialVersionUID = -4145772650410085612L;

	public SystemException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public SystemException(String errorCode, String errorMessage, Throwable originalException) {
		super(errorCode, errorMessage, originalException);
	}

	@Override
	public String toString() {
		return "\nSystemException {\n" + "  id:                           " + id + "\n"
				+ "  timestamp:                    " + timeStamp + "\n" + "  errorCode:                    "
				+ errorCode + "\n" + "  localizedErrorMessage:        " + localizedErrorMessage + "\n"
				+ "  originalExceptionStackTrace:  " + "{\n" + "    " + originalExceptionStackTrace + "  }\n" + "}";
	}
}
