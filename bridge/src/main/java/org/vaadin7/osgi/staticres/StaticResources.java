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
 * @author brindy, zdilla
 * 
 * Contribution: 
 * 		Florian Pirchner - migrating to vaadin 7
 */
package org.vaadin7.osgi.staticres;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * This class runs as an OSGi component and serves the themes and widgetsets
 * directly from the core Vaadin bundle.
 * <p/>
 * 
 * To add your own theme or widget set create a fragment which contains your
 * theme/widgetset files and export those as packages. The
 * <code>Fragment-Host</code> should be set to the Vaadin core bundle. The
 * fragment containing your theme/widgetset resources will be added to the core
 * Vaadin bundle dynamically.
 * <p/>
 * 
 * Of course static resources should really be deployed separately to a web
 * server that proxies servlet requests on to the container.
 * <p/>
 * 
 * Implementation updated to suggestion by zdilla in <a
 * href="https://github.com/brindy/VaadinOSGi/issues/1">Issue 1</a>
 * <p/>
 * 
 */
@Component(properties = { "http.alias=/VAADIN" }, immediate = true)
public class StaticResources implements HttpContext, BundleListener {

	private static final String RESOURCE_BASE = "/VAADIN";

	private Set<Bundle> resourceBundles = new HashSet<Bundle>();

	@Reference(unbind = "unsetHttpService", dynamic = true, optional = true, multiple = true)
	private void setHttpService(final HttpService httpService)
			throws NamespaceException {
		httpService.registerResources(RESOURCE_BASE, RESOURCE_BASE, this);
	}

	@SuppressWarnings("unused")
	private void unsetHttpService(final HttpService httpService) {
		httpService.unregister(RESOURCE_BASE);
	}

	@Override
	public boolean handleSecurity(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		return true;
	}

	@Override
	public URL getResource(final String name) {
		URL resource = null;
		String uri = name.startsWith("/") ? name : "/" + name;
		// iterate the server bundle, client bundle and fragments
		for (Bundle bundle : resourceBundles) {
			String root = (String) bundle.getHeaders().get("Vaadin-Resources");
			if (root != null && !".".equals(root)) {
				uri = "/" + root + uri;
			}
			if (null != (resource = bundle.getResource(uri))) {
				break;
			}
		}
		return resource;
	}

	@Override
	public String getMimeType(final String name) {
		URL resource = getResource(name);
		if (null != resource) {
			try {
				return resource.openConnection().getContentType();
			} catch (final IOException e) {
				return null;
			}
		}
		return null;
	}

	private void checkBundleForResources(Bundle bundle) {
		if (isClientBundle(bundle) || isServerBundle(bundle)
				|| isThemesBundle(bundle)) {
			resourceBundles.add(bundle);
		} else if (null != bundle.getHeaders().get("Vaadin-Resources")) {
			resourceBundles.add(bundle);
		} else {
			resourceBundles.remove(bundle);
		}
	}

	/**
	 * Returns true if the bundle is the vaadin server bundle.
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean isServerBundle(Bundle bundle) {
		return bundle.getSymbolicName().equals("com.vaadin.vaadin-server");
	}

	/**
	 * Returns true, if the bundle is the vaadin client bundle.
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean isClientBundle(Bundle bundle) {
		return bundle.getSymbolicName().equals("com.vaadin.vaadin-client");
	}

	/**
	 * Returns true, if the bundle is the vaadin themes bundle.
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean isThemesBundle(Bundle bundle) {
		return bundle.getSymbolicName().equals("com.vaadin.themes");
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.UNINSTALLED) {
			resourceBundles.remove(event.getBundle());
		} else {
			checkBundleForResources(event.getBundle());
		}
	}

	@Activate
	public void start(BundleContext ctx) {
		ctx.addBundleListener(this);
		for (Bundle bundle : ctx.getBundles()) {
			checkBundleForResources(bundle);
		}
	}

}
