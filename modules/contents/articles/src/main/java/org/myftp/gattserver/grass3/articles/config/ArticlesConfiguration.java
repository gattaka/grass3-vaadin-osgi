package org.myftp.gattserver.grass3.articles.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class ArticlesConfiguration {

	@XmlTransient
	public static final String CONFIG_PATH = "articles_config.xml";
		
	/**
	 * Kolik je timeout pro zálohu (default 2 minuty)
	 */
	private int backupTimeout = 2;

	/**
	 * Kolik je délka tabulátoru ve znacích ?
	 */
	private int tabLength = 2;

	public int getTabLength() {
		return tabLength;
	}

	public void setTabLength(int tabLength) {
		this.tabLength = tabLength;
	}

	public int getBackupTimeout() {
		return backupTimeout;
	}

	public void setBackupTimeout(int backupTimeout) {
		this.backupTimeout = backupTimeout;
	}

}
