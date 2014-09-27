package cz.gattserver.grass3.articles.config;

import cz.gattserver.grass3.config.AbstractConfiguration;

public class ArticlesConfiguration extends AbstractConfiguration {

	public ArticlesConfiguration() {
		super("cz.gattserver.grass3.articles");
	}

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
