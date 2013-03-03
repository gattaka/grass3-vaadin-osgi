package org.myftp.gattserver.grass3.modtest;

import org.myftp.gattserver.grass3.pages.SettingsPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@org.springframework.stereotype.Component("modTestSettingsPage")
@Scope("prototype")
public class ModTestSettingsPage extends SettingsPage {

	private static final long serialVersionUID = -3310643769376755875L;

	public ModTestSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createRightColumnContent() {
		return new Label("ModTEST");
	}

}
