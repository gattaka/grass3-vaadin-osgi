package org.myftp.gattserver.grass3.articles.service.demo;

import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.myftp.gattserver.grass3.articles.service.ISelectionDecorator;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class DemoPluginService implements IPluginService {

	public String getPluginName() {
		return "Demo plugin";
	}

	public String getPluginButtonCaption() {
		return "Demo";
	}

	public Resource getPluginButtonImageResource() {
		return new ThemeResource("img/tags/document_16.png");
	}

	public ISelectionDecorator getPluginSelectionDecorator() {
		return new ISelectionDecorator() {

			public String decorate(String selection) {
				return "[DEMO]" + selection + "[/DEMO]";
			}
		};
	}

}
