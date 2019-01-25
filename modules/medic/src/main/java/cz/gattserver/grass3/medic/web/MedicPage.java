package cz.gattserver.grass3.medic.web;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;

public class MedicPage extends OneColumnPage {

	public MedicPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout marginLayout = new VerticalLayout();
		marginLayout.setMargin(new MarginInfo(false, true, true, true));

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		marginLayout.addComponent(layout);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		MedicalRecordsTab medicalRecordsTab = new MedicalRecordsTab();
		
		tabSheet.addTab(new ScheduledVisitsTab(medicalRecordsTab), "Plánované návštěvy");
		tabSheet.addTab(medicalRecordsTab, "Záznamy");
		tabSheet.addTab(new MedicalInstitutionsTab(), "Instituce");
		tabSheet.addTab(new MedicamentsTab(), "Medikamenty");
		tabSheet.addTab(new PhysiciansTab(), "Doktoři");

		return marginLayout;
	}

}
