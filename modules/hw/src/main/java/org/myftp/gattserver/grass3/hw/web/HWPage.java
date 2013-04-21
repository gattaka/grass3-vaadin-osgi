package org.myftp.gattserver.grass3.hw.web;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

@org.springframework.stereotype.Component("hwPage")
@Scope("prototype")
public class HWPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Resource(name = "hwFacade")
	private IHWFacade hwFacade;

	public HWPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {

		super.init();
	}

	@Override
	protected Component createContent() {
		return new Label("funguje");
	}

}
