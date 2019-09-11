package cz.gattserver.grass3.ui.components;

import java.util.List;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Breadcrumb extends HorizontalLayout {

	private static final long serialVersionUID = 3874999284861747099L;

	private HorizontalLayout breadcrumbLayout;

	public static class BreadcrumbElement {
		private String caption;
		private String url;

		public BreadcrumbElement(String caption, String url) {
			this.url = url;
			this.caption = caption;
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	public Breadcrumb() {
		setClassName("breadcrumb");
		setSizeFull();

		breadcrumbLayout = new HorizontalLayout();
		breadcrumbLayout.setHeight("18px");
		add(breadcrumbLayout);
	}

	public Breadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		this();
		initBreadcrumb(breadcrumbElements);
	}

	private Anchor createBreadcrumbElementLink(BreadcrumbElement element) {
		Anchor link = new Anchor(element.getCaption(), element.getUrl());
		link.setClassName("breadcrumb_element");
		return link;
	}

	private void initBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		// konstrukce breadcrumb v opačném pořadí (správném)
		BreadcrumbElement element = null;
		for (int i = breadcrumbElements.size() - 1; i >= 0; i--) {
			element = breadcrumbElements.get(i);
			if (i != breadcrumbElements.size() - 1) {
				Image separator = new Image("img/bullet.png", "bullet");
				breadcrumbLayout.add(separator);
			}
			Anchor link = createBreadcrumbElementLink(element);
			breadcrumbLayout.add(link);
		}
	}

	public void resetBreadcrumb(List<BreadcrumbElement> breadcrumbElements) {
		breadcrumbLayout.removeAll();
		initBreadcrumb(breadcrumbElements);
	}

}
