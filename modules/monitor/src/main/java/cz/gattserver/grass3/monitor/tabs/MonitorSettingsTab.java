package cz.gattserver.grass3.monitor.tabs;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.monitor.config.MonitorConfiguration;
import cz.gattserver.grass3.monitor.facade.MonitorFacade;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.FieldUtils;

public class MonitorSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private MonitorFacade monitorFacade;

	public MonitorSettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final MonitorConfiguration configuration = monitorFacade.getConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>", ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Adresář skriptů
		 */
		final TextField scriptsDirField = new TextField("Adresář skriptů");
		scriptsDirField.setValue(String.valueOf(configuration.getScriptsDir()));
		FieldUtils.addValidator(scriptsDirField, new StringLengthValidator("Nesmí být prázdné", 1, 1024));
		settingsFieldsLayout.addComponent(scriptsDirField);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				if (scriptsDirField.getComponentError() == null) {
					configuration.setScriptsDir(scriptsDirField.getValue());
					monitorFacade.storeConfiguration(configuration);
				}
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

}
