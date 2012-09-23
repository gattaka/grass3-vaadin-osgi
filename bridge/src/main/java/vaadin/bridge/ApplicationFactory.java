package vaadin.bridge;

import java.util.Dictionary;

import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;

public interface ApplicationFactory {

	static final String ALIAS_NAME = "alias";

	Application newInstance();

	String getApplicationCSSClassName();

	/**
	 * Return system messages for the Application type, or {@code null} if there
	 * are no Application-specific system messages.
	 */
	SystemMessages getSystemMessages();

	Dictionary<String, String> getInitParams();

}