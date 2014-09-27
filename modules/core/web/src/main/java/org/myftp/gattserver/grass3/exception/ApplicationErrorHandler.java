package org.myftp.gattserver.grass3.exception;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.UI;

import org.myftp.gattserver.grass3.exception.ui.ExceptionWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationErrorHandler implements ErrorHandler {

	private static final long serialVersionUID = -7739910142600177544L;

	private static final Logger logger = LoggerFactory.getLogger(ApplicationErrorHandler.class);

	public void error(ErrorEvent event) {
		error(event.getThrowable());
	}

	public void error(Throwable throwable) {
		final String log = new SystemException("Chyba", "V aplikaci došlo k neočekávané chybě", throwable).toString();
		logger.error(log);
		UI.getCurrent().addWindow(new ExceptionWindow() {
			private static final long serialVersionUID = 1910421782322339390L;

			@Override
			protected String getStackTrace() {
				return log;
			}
		});
	}

}
