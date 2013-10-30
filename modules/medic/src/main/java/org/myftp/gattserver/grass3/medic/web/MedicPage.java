package org.myftp.gattserver.grass3.medic.web;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("medicPage")
@Scope("prototype")
public class MedicPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Resource(name = "medicFacade")
	private IMedicFacade medicFacade;

	public MedicPage(GrassRequest request) {
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

		tabSheet.addTab(new ScheduledVisitsTab(medicFacade),
				"Plánované návštěvy");
		tabSheet.addTab(new MedicOverviewTab(), "Záznamy");
		tabSheet.addTab(new MedicalInstitutionsTab(medicFacade), "Instituce");
		tabSheet.addTab(new MedicamentsTab(medicFacade), "Medikamenty");

		return layout;
	}

}
