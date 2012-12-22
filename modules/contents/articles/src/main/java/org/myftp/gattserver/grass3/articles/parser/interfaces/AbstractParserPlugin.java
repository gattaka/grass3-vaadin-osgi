package org.myftp.gattserver.grass3.articles.parser.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gatt
 */
public abstract class AbstractParserPlugin extends AbstractParser {

	private Logger logger;

	/**
	 * Zjistí od pluginu jestli bere konce řádků
	 * 
	 * @return {@code true} pokud je povolen v pluginu <br/>
	 */
	public abstract boolean canHoldBreakline();

	protected Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(this.getClass());
		}
		return logger;
	}

	protected void log(String msg, Object... args) {
		getLogger().info(msg, args);
	}

}
