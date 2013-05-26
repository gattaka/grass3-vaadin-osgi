package org.myftp.gattserver.grass3.template;

import java.util.List;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;

public class Breadcrumb extends HorizontalLayout {

	private static final long serialVersionUID = 3874999284861747099L;

	private HorizontalLayout breadcrumbLayout;

	public static class BreadcrumbElement {
		private String caption;
		private Resource resource;

		public BreadcrumbElement(String caption, Resource resource) {
			this.resource = resource;
			this.caption = caption;
		}

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

	public Breadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		this();
		initBreadcrumb(breadcrumbElements);
	}

	private Link createBreadcrumbElementLink(BreadcrumbElement element) {
		Link link = new Link(element.getCaption(), element.getResource());
		link.setStyleName("breadcrumb_element");
		return link;
	}

	private void initBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {

		// konstrukce breadcrumb v opačném pořadí (správném)
		BreadcrumbElement element = null;
		for (int i = breadcrumbElements.size() - 1; i >= 0; i--) {
			element = breadcrumbElements.get(i);
			if (i != breadcrumbElements.size() - 1) {
				Embedded separator = new Embedded();
				separator.setSource(new ThemeResource("img/bullet.png"));
				breadcrumbLayout.addComponent(separator);
			}
			breadcrumbLayout.addComponent(createBreadcrumbElementLink(element));
		}
	}

	public void resetBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		breadcrumbLayout.removeAllComponents();
		initBreadcrumb(breadcrumbElements);
	}

}
