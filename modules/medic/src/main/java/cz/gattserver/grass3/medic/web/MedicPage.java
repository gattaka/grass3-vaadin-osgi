package cz.gattserver.grass3.medic.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

import cz.gattserver.grass3.pages.template.OneColumnPage;
import cz.gattserver.grass3.template.Selectable;
import cz.gattserver.grass3.ui.util.GrassRequest;

public class MedicPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

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

		tabSheet.addTab(new ScheduledVisitsTab(), "Plánované návštěvy");
		tabSheet.addTab(new MedicalRecordsTab(), "Záznamy");
		tabSheet.addTab(new MedicalInstitutionsTab(), "Instituce");
		tabSheet.addTab(new MedicamentsTab(), "Medikamenty");
		tabSheet.addTab(new PhysiciansTab(), "Doktoři");

		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {

			private static final long serialVersionUID = 6943259268778916110L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = (TabSheet) event.getComponent();
				Component component = tabSheet.getSelectedTab();
				if (component != null && component instanceof Selectable) {
					((Selectable) component).select();
				}
			}
		});

		return layout;
	}

}
