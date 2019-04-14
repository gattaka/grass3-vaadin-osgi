package cz.gattserver.grass3.server;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.context.ApplicationContext;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import cz.gattserver.web.common.spring.SpringContextHelper;

public class GrassVaadinServlet extends VaadinServlet {

	private static final long serialVersionUID = -5411796850116104693L;

	private Map<VaadinSession, Boolean> populatedSessions = new WeakHashMap<>();

	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		VaadinServletService service = super.createServletService(deploymentConfiguration);
		service.addSessionInitListener(event -> {
			VaadinSession session = event.getSession();
			if (populatedSessions.containsKey(session))
				return;
			ApplicationContext context = SpringContextHelper.getContext();
			if (context != null) {
				Collection<GrassRequestHandler> grassRequestHandlers = context.getBeansOfType(GrassRequestHandler.class)
						.values();
				for (GrassRequestHandler requestHandler : grassRequestHandlers)
					event.getSession().addRequestHandler(requestHandler);
				populatedSessions.put(session, Boolean.TRUE);
			}
		});
		return service;
	}

}
