package org.myftp.gattserver.grass3.monitor.web;

import org.myftp.gattserver.grass3.monitor.facade.IMonitorFacade;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class MonitorPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Autowired
	private IMonitorFacade medicFacade;

	public MonitorPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		// TODO

		return layout;
	}

}
