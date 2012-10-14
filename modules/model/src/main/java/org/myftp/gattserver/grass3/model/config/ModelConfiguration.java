package org.myftp.gattserver.grass3.model.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class ModelConfiguration {

	@XmlTransient
	public static final String CONFIG_PATH = "db_config.xml";
	
	private String username = "sa";
	private String password = "";
	private String URL = "jdbc:h2:grass3H2DB";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

}
