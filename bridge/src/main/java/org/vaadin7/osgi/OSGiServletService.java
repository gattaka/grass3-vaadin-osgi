package org.vaadin7.osgi;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

@SuppressWarnings("serial")
public class OSGiServletService extends VaadinServletService {

	private final IVaadinSessionManager sessionManager;

	public OSGiServletService(VaadinServlet servlet,
			DeploymentConfiguration deploymentConfiguration,
			IVaadinSessionManager sessionManager) {
		super(servlet, deploymentConfiguration);

		this.sessionManager = sessionManager;
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request)
			throws ServiceException {
		return sessionManager.createVaadinSession(request,
				getCurrentServletRequest());
	}

	/**
	 * Handles the lifecycle of vaadin sessions.<br>
	 * Not intended to be subclassed!
	 */
	interface IVaadinSessionManager {
		/**
		 * Returns a new instance of a vaadin session.
		 * 
		 * @param request
		 * @param httpServletRequest
		 * 
		 * @return
		 */
		VaadinSession createVaadinSession(VaadinRequest request,
				HttpServletRequest httpServletRequest);
	}
}
