package org.myftp.gattserver.grass3.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class CoreConfiguration implements Serializable {

	private static final long serialVersionUID = 3251918420161616697L;

	@XmlTransient
	public static final String CONFIG_PATH = "grass_core.xml";
	
	private Double sessionTimeout = 30.0;

	// mají mít uživatelé možnost se zaregistrovat ?
	private boolean registrations = true;

	public boolean isRegistrations() {
		return registrations;
	}

	public void setRegistrations(boolean registrations) {
		this.registrations = registrations;
	}

	public Double getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Double sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}
