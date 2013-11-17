package org.myftp.gattserver.grass3.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringInjector {

	private static volatile ApplicationContext applicationContext;

	public static ApplicationContext getContext() {
		if (applicationContext == null) {
			synchronized (SpringInjector.class) {
				if (applicationContext == null) {
					ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
							.currentRequestAttributes();
					HttpServletRequest request = requestAttributes.getRequest();
					HttpSession session = request.getSession(false);
					applicationContext = WebApplicationContextUtils
							.getRequiredWebApplicationContext(session
									.getServletContext());
				}
			}
		}
		return applicationContext;
	}

	public static void inject(Object object) {

		AutowireCapableBeanFactory beanFactory = getContext()
				.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(object,
				AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
	}

}