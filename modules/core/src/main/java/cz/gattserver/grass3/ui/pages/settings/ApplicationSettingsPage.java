package cz.gattserver.grass3.ui.pages.settings;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.config.CoreConfiguration;
import cz.gattserver.grass3.services.ConfigurationService;

public class ApplicationSettingsPage extends AbstractSettingsPage {

	private static final long serialVersionUID = 6536724991295498082L;
	
	private static final Double MIN_SESSION_TIMEOUT = 5.0;
	private static final Double MAX_SESSION_TIMEOUT = 60.0;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();

		layout.setPadding(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.add(settingsLayout);

		final CoreConfiguration configuration = loadConfiguration();

		settingsLayout.removeAll();
		settingsLayout.add(new H2("Nastavení aplikace"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.add(settingsFieldsLayout);
		settingsFieldsLayout.setPadding(false);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Session Timeout
		 */
		HorizontalLayout sessionTimeoutLayout = new HorizontalLayout();
		sessionTimeoutLayout.setSizeFull();
		sessionTimeoutLayout.setSpacing(true);

		Double initValue = configuration.getSessionTimeout();
		if (initValue > MAX_SESSION_TIMEOUT) {
			initValue = MAX_SESSION_TIMEOUT;
			configuration.setSessionTimeout(initValue);
			storeConfiguration(configuration);
		}
		if (initValue < MIN_SESSION_TIMEOUT) {
			initValue = MIN_SESSION_TIMEOUT;
			configuration.setSessionTimeout(initValue);
			storeConfiguration(configuration);
		}

		final Span valueSpan = new Span(initValue.toString());
		valueSpan.setWidth("3em");

		// TODO
		// Span sliderCaption = new Span("Session timeout (5-60 min.)");
		// final PaperRangeSlider slider = new PaperRangeSlider();
		// slider.setWidth("100%");
		// slider.setMin(MIN_SESSION_TIMEOUT);
		// slider.setMax(MAX_SESSION_TIMEOUT);
		//
		// slider.setValueMax(initValue);
		// slider.addMaxValueChangeListener(event -> {
		// valueSpan.setText(String.valueOf(event.getValueMax()));
		// configuration.setSessionTimeout(event.getValueMax());
		// });
		//
		// sessionTimeoutLayout.add(sliderCaption);
		// sessionTimeoutLayout.add(slider);
		// sessionTimeoutLayout.expand(slider);
		// sessionTimeoutLayout.add(valueSpan);
		// sessionTimeoutLayout.setVerticalComponentAlignment(Alignment.START,
		// valueSpan);
		//
		// settingsFieldsLayout.add(sessionTimeoutLayout);

		/**
		 * Povolení registrací
		 */
		final Checkbox allowRegistrationsBox = new Checkbox("Povolit registrace");
		allowRegistrationsBox.setValue(configuration.isRegistrations());
		allowRegistrationsBox
				.addValueChangeListener(event -> configuration.setRegistrations(allowRegistrationsBox.getValue()));

		settingsFieldsLayout.add(allowRegistrationsBox);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", event -> storeConfiguration(configuration));
		settingsFieldsLayout.add(saveButton);

		return layout;
	}

	private CoreConfiguration loadConfiguration() {
		CoreConfiguration configuration = new CoreConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(CoreConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

}
