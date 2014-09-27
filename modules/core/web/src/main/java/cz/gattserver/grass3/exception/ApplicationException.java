package cz.gattserver.grass3.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.GregorianCalendar;
import java.util.UUID;

public abstract class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -1504502969392647247L;

    protected String id;

    protected String timeStamp;

    protected String errorCode;

    protected String localizedErrorMessage;

    protected String originalExceptionStackTrace;

    protected Boolean alreadyLogged = Boolean.FALSE;

    protected ApplicationException(String errorCode, String localizedErrorMessage) {
        this(errorCode, localizedErrorMessage, null);
    }

    protected ApplicationException(String errorCode, String localizedErrorMessage, Throwable throwable) {
        super(throwable);
        this.id = UUID.randomUUID().toString();
        this.timeStamp = DateFormatUtils.format(GregorianCalendar.getInstance(), "yyyy-MM-dd HH:mm:ss");
        this.errorCode = errorCode;
        this.localizedErrorMessage = localizedErrorMessage;
        if (throwable != null)
            this.originalExceptionStackTrace = ExceptionUtils.getStackTrace(throwable);
    }

    public void setAlreadyLogged(Boolean alreadyLogged) {
        this.alreadyLogged = alreadyLogged;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getId() {
        return id;
    }

    public String getLocalizedErrorMessage() {
        return localizedErrorMessage;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Boolean getAlreadyLogged() {
        return alreadyLogged;
    }
}
