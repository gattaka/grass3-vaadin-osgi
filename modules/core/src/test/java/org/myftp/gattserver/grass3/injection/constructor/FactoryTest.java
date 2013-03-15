package org.myftp.gattserver.grass3.injection.constructor;

import org.junit.Test;
import org.myftp.gattserver.grass3.test.BaseSpringTest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class FactoryTest extends BaseSpringTest implements
		ApplicationContextAware {

	private ApplicationContext context;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	@Test
	public void testContruction() {
		Constructed constructed = (Constructed) context.getBean("constructed",
				new Integer(785));
		constructed.printInteger();
	}

}
