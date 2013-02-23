package org.myftp.gattserver.grass3.windows.template;

import org.myftp.gattserver.grass3.GrassUI;
import org.myftp.gattserver.grass3.security.CoreACL;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.CustomLayout;

public abstract class GrassPage extends CustomLayout {

	private static final long serialVersionUID = 604170960797872356L;

	public GrassPage(String template) {
		super(template);
	}

	/**
	 * Získá resource dle stránky
	 */
	protected Resource getPageResource(IPageFactory pageFactory) {
		return new ExternalResource("/" + pageFactory.getPageName());
	}

	/**
	 * Získá ACL
	 */
	protected CoreACL getUserACL() {
		return CoreACL.get(((GrassUI) getUI()).getUser());
	}

}
