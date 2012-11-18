package org.myftp.gattserver.grass3.articles.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Configuration {

	@XmlTransient
	public static final String CONFIG_PATH = "articles_config.xml";
	
	/**
	 * Mají se provádět výpisy z Lexeru ?
	 */
	private boolean lexerDebugOutput = false;

	/**
	 * Mají se provádět výpisy z Parseru ?
	 */
	private boolean parserDebugOutput = false;

	/**
	 * Mají se provádět výpisy ze základního překladu ?
	 */
	private boolean mainDebugOutput = false;

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

	public boolean isMainDebugOutput() {
		return mainDebugOutput;
	}

	public void setMainDebugOutput(boolean mainDebugOutput) {
		this.mainDebugOutput = mainDebugOutput;
	}

	public boolean isLexerDebugOutput() {
		return lexerDebugOutput;
	}

	public void setLexerDebugOutput(boolean lexerDebugOutput) {
		this.lexerDebugOutput = lexerDebugOutput;
	}

	public boolean isParserDebugOutput() {
		return parserDebugOutput;
	}

	public void setParserDebugOutput(boolean parserDebugOutput) {
		this.parserDebugOutput = parserDebugOutput;
	}

}
