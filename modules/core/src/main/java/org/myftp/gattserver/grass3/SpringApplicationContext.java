package org.myftp.gattserver.grass3;

import org.springframework.context.ApplicationContext;

import java.io.Serializable;

public class SpringApplicationContext implements Serializable {
	
	private static final long serialVersionUID = -1387464003353385640L;
	
	private static transient ApplicationContext applicationContext;

	public static void setApplicationContext(
			ApplicationContext applicationContext) {
		SpringApplicationContext.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
