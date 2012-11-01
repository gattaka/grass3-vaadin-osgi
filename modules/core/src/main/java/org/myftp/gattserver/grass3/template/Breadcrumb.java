package org.myftp.gattserver.grass3.template;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.myftp.gattserver.grass3.windows.HomeWindow;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;

public class Breadcrumb extends HorizontalLayout {

	private static final long serialVersionUID = 3874999284861747099L;

	private HorizontalLayout breadcrumbLayout;

	public static class BreadcrumbElement {
		private String caption;
		private Resource resource;

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public Resource getResource() {
			return resource;
		}

		public void setResource(Resource resource) {
			this.resource = resource;
		}
	}

	public Breadcrumb() {
		setStyleName("breadcrumb");
		setSizeFull();

		breadcrumbLayout = new HorizontalLayout();
		addComponent(breadcrumbLayout);
	}

	public Breadcrumb(Collection<BreadcrumbElement> breadcrumbElements) {
		this();
		initBreadcrumb(breadcrumbElements);
	}

	private Link createBreadcrumbElementLink(BreadcrumbElement element) {
		Link link = new Link(element.getCaption(), element.getResource());
		link.setStyleName("breadcrumb_element");
		return link;
	}

	private void initBreadcrumb(Collection<BreadcrumbElement> breadcrumbElements) {

		// konstrukce breadcrumb v opačném pořadí (správném)
		boolean first = true;
		for (BreadcrumbElement element : breadcrumbElements) {
			if (!first) {
				Embedded separator = new Embedded();
				separator.setSource(new ThemeResource("img/bullet.png"));
				breadcrumbLayout.addComponent(separator);
			}
			breadcrumbLayout.addComponent(createBreadcrumbElementLink(element));
			first = false;
		}
	}

	public void resetBreadcrumb(Collection<BreadcrumbElement> breadcrumbElements) {
		breadcrumbLayout.removeAllComponents();
		initBreadcrumb(breadcrumbElements);
	}

}
