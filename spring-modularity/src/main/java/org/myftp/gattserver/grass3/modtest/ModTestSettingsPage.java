package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.pages.SettingsPage;
import org.myftp.gattserver.grass3.tabs.template.SettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@org.springframework.stereotype.Component("modTestSettingsTab")
@Scope("prototype")
public class ModTestSettingsPage extends SettingsTab {

	private static final long serialVersionUID = -3310643769376755875L;

	public ModTestSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		return new Label("ModTEST");
	}

}
