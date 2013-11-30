package org.myftp.gattserver.grass3.hw.web;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

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
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		tabSheet.addTab(new HWItemsTab(hwFacade), "Přehled");
		tabSheet.addTab(new HWTypesTab(hwFacade), "Typy zařízení");

		return layout;
	}

}
