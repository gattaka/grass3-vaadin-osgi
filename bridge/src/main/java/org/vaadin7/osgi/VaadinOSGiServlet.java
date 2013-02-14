/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor:
 * 		Florian Pirchner - migrating to vaadin 7
 */
package org.vaadin7.osgi;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

/**
 * Used to create instances of applications that have been registered with the
 * container via a component factory.
 * 
 * @author brindy
 */
class VaadinOSGiServlet extends VaadinServlet implements
		OSGiServletService.IVaadinSessionManager {

	private static final long serialVersionUID = 1L;

	private final ComponentFactory factory;
	private final Map<String, Object> properties;

	private Set<VaadinSessionInfo> sessions = new HashSet<VaadinSessionInfo>();

	public VaadinOSGiServlet(ComponentFactory factory,
			Map<String, Object> properties) {
		this.factory = factory;
		this.properties = properties;
	}

	@Override
	protected OSGiServletService createServletService(
			DeploymentConfiguration deploymentConfiguration) {
		return new OSGiServletService(this, deploymentConfiguration, this);
	}

	public VaadinSession createVaadinSession(VaadinRequest request,
			HttpServletRequest httpServletRequest) {
		@SuppressWarnings("rawtypes")
		final VaadinSessionInfo info = new VaadinSessionInfo(
				factory.newInstance((Dictionary) properties),
				httpServletRequest.getSession());

		info.session.setAttribute(VaadinOSGiServlet.class.getName(),
				new HttpSessionListener() {
					public void sessionDestroyed(HttpSessionEvent arg0) {
						info.dispose();
					}

					public void sessionCreated(HttpSessionEvent arg0) {

					}
				});
		System.out.println("Ready: " + info); //$NON-NLS-1$
		return (VaadinSession) info.instance.getInstance();
	}

	@Override
	public void destroy() {
		super.destroy();

		synchronized (this) {
			HashSet<VaadinSessionInfo> sessions = new HashSet<VaadinSessionInfo>();
			sessions.addAll(this.sessions);
			this.sessions.clear();
			for (VaadinSessionInfo info : sessions) {
				info.dispose();
			}
		}
	}

	/**
	 * Track the component instance and session. If this is disposed the entire
	 * associated http session is also disposed.
	 */
	class VaadinSessionInfo {

		final ComponentInstance instance;
		final HttpSession session;

		public VaadinSessionInfo(ComponentInstance instance, HttpSession session) {
			this.instance = instance;
			this.session = session;
			sessions.add(this);
		}

		public void dispose() {
			VaadinSessionInfo app = (VaadinSessionInfo) instance.getInstance();
			if (app != null) {
				app.dispose();
			}

			instance.dispose();

			session.removeAttribute(VaadinOSGiServlet.class.getName());
			sessions.remove(this);
		}
	}

}
