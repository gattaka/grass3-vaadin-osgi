package cz.gattserver.grass3;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/*
 * https://www.baeldung.com/spring-xml-vs-java-config
 * https://www.baeldung.com/register-servlet
 * https://github.com/exacode/spring-vaadin-example/blob/master/src/main/java/net/exacode/vaadin/ApplicationInitializer.java
 * https://stackoverflow.com/questions/31450508/vaadin-and-spring-integration-what-replaces-vaadinservletconfiguration
 */
public class GrassWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {

		// Spring context
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation("cz.gattserver.grass3.spring.config");
		servletContext.addListener(new ContextLoaderListener(context));
		servletContext.addListener(new RequestContextListener());

		// Spring security
		servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"))
				.addMappingForUrlPatterns(null, false, "/*");

		// Spring MVC Controllers (REST)
		ServletRegistration.Dynamic springRegistration = servletContext.addServlet("ws-dispatcher",
				new DispatcherServlet(context));
		springRegistration.addMapping("/ws/*");
		springRegistration.setLoadOnStartup(0);
	}
}