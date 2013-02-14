package org.vaadin7.osgi;

public class Constants {

	/**
	 * OSGi property component.factory for the vaadin session (vaadin
	 * application in vaadin 6)
	 */
	public static final String OSGI_PROP__VAADIN_SESSION = "org.vaadin.Session";

	/**
	 * OSGi property component.factory for the vaadin UI (tab sheet). The
	 * vaadin.ui.class name is part of the factory name and putted after the /.
	 * The class name is required for lazy loading issues.
	 * <p>
	 * Example:
	 * 
	 * @Component (factory="org.vaadin.UI/uk.org.brindy.guessit.GuessItUI")
	 */
	public static final String OSGI_PROP__VAADIN_UI = "org.vaadin.UI";

	/**
	 * The prefix of the factory component name before the ui class name starts. <br>
	 * Uiclass name: uk.org.brindy.guessit.GuessItUI<br>
	 * Factory name: org.vaadin.UI/uk.org.brindy.guessit.GuessItUI
	 */
	public static final String PREFIX__UI_CLASS = OSGI_PROP__VAADIN_UI + "/";

}
