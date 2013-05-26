package org.myftp.gattserver.jpatest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring/app-context.xml");
		context.getBean(FacadeService.class).testFunctions();
	}
}
