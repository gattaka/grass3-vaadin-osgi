package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OverviewTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private IHWFacade hwFacade;

	public OverviewTab(IHWFacade hwFacade) {
		this.hwFacade = hwFacade;
		addComponent(new Label("Jsem přehled typů zařízení"));
	}
}
