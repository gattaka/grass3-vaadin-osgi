package org.myftp.gattserver.grass3;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContextHelper {

	private static class ContextHolder {

		private static volatile ApplicationContext applicationContext;

		public static ApplicationContext getContext() {
			if (applicationContext == null) {
				synchronized (SpringContextHelper.class) {
					if (applicationContext == null) {
						ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
								.currentRequestAttributes();
						HttpServletRequest request = requestAttributes
								.getRequest();
						HttpSession session = request.getSession(false);
						applicationContext = WebApplicationContextUtils
								.getRequiredWebApplicationContext(session
										.getServletContext());
					}
				}
			}
			return applicationContext;
		}
	}

	public static Object getBean(final String beanRef) {
		return ContextHolder.getContext().getBean(beanRef);
	}

	public static <T> T getBean(final Class<T> type) {
		return ContextHolder.getContext().getBean(type);
	}

}