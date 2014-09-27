package cz.gattserver.grass3;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
						HttpServletRequest request = requestAttributes.getRequest();
						HttpSession session = request.getSession(false);
						applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(session
								.getServletContext());
					}
				}
			}
			return applicationContext;
		}
	}

	public static ApplicationContext getContext() {
		return ContextHolder.getContext();
	}

	public static Object getBean(final String beanRef) {
		return getContext().getBean(beanRef);
	}

	public static <T> T getBean(final Class<T> type) {
		return getContext().getBean(type);
	}

	public static void inject(Object target) {
		getContext().getAutowireCapableBeanFactory().autowireBeanProperties(target,
				AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
	}

}