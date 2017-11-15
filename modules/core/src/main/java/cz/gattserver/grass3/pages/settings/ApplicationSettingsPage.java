package cz.gattserver.grass3.pages.settings;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component; 
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.config.CoreConfiguration;
import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.window.InfoWindow;
import cz.gattserver.web.common.window.WarnWindow;

public class ApplicationSettingsPage extends ModuleSettingsPage {

	@Autowired
	private ContentTagFacade contentTagFacade;

	@Autowired
	private ConfigurationService configurationService;

	private static final Double MIN_SESSION_TIMEOUT = 5.0;
	private static final Double MAX_SESSION_TIMEOUT = 60.0;

	public ApplicationSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final CoreConfiguration configuration = loadConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení aplikace"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setMargin(false);
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

		final Label valueLabel = new Label(initValue.toString());
		valueLabel.setWidth("3em");

		final Slider slider = new Slider("Session timeout (5-60 min.)");
		slider.setWidth("100%");
		slider.setMin(MIN_SESSION_TIMEOUT);
		slider.setMax(MAX_SESSION_TIMEOUT);

		try {
			slider.setValue(initValue);
		} catch (ValueOutOfBoundsException e) {
			e.printStackTrace();
		}
		slider.addValueChangeListener(event -> {
			valueLabel.setValue(String.valueOf(event.getValue()));
			configuration.setSessionTimeout(event.getValue());
		});

		sessionTimeoutLayout.addComponent(slider);
		sessionTimeoutLayout.setExpandRatio(slider, 1);
		sessionTimeoutLayout.addComponent(valueLabel);
		sessionTimeoutLayout.setComponentAlignment(valueLabel, Alignment.BOTTOM_LEFT);

		settingsFieldsLayout.addComponent(sessionTimeoutLayout);

		/**
		 * Povolení registrací
		 */
		final CheckBox allowRegistrationsBox = new CheckBox("Povolit registrace");
		allowRegistrationsBox.setValue(configuration.isRegistrations());
		allowRegistrationsBox
				.addValueChangeListener(event -> configuration.setRegistrations(allowRegistrationsBox.getValue()));

		settingsFieldsLayout.addComponent(allowRegistrationsBox);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", event -> storeConfiguration(configuration));
		settingsFieldsLayout.addComponent(saveButton);

		/**
		 * Reset tagů
		 */
		Button contentTagsCountsResetBtn = new Button("Přepočítat údaje ContentTagů", event -> {
			try {
				contentTagFacade.processContentNodesCounts();
				InfoWindow infoSubwindow = new InfoWindow("Počty obsahů tagů byly úspěšně přepočítány");
				UI.getCurrent().addWindow(infoSubwindow);
			} catch (Exception e) {
				WarnWindow warnSubwindow = new WarnWindow("Nezdařilo se přepočítat počty obsahů tagů");
				UI.getCurrent().addWindow(warnSubwindow);
			}
		});
		settingsFieldsLayout.addComponent(contentTagsCountsResetBtn);

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
