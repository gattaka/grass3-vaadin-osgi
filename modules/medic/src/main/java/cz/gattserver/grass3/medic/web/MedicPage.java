package cz.gattserver.grass3.medic.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class MedicPage extends OneColumnPage {

	public MedicPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(true);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		marginLayout.addComponent(layout);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		tabSheet.addTab(new ScheduledVisitsTab(), "Plánované návštěvy");
		tabSheet.addTab(new MedicalRecordsTab(), "Záznamy");
		tabSheet.addTab(new MedicalInstitutionsTab(), "Instituce");
		tabSheet.addTab(new MedicamentsTab(), "Medikamenty");
		tabSheet.addTab(new PhysiciansTab(), "Doktoři");

		return marginLayout;
	}

}
