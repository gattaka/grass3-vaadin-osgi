package org.myftp.gattserver.grass3.windows;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.config.ConfigurationFileError;
import org.myftp.gattserver.grass3.config.ConfigurationManager;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.config.CoreConfiguration;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.windows.ifces.SettingsPageFactory;
import org.myftp.gattserver.grass3.windows.template.SettingsPage;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class ApplicationSettingsPage extends SettingsPage {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final SettingsPageFactory FACTORY = new SettingsPageFactory(
			"application-settings") {
		@Override
		public SettingsPage createSettingsPage(GrassRequest request) {
			return new ApplicationSettingsPage(request);
		}
	};

	public ApplicationSettingsPage(GrassRequest request) {
		super(request);
	}

	private static final Double MIN_SESSION_TIMEOUT = 5.0;
	private static final Double MAX_SESSION_TIMEOUT = 60.0;

	@Override
	protected Component createRightColumnContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final CoreConfiguration configuration = loadConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení aplikace</h2>",
				ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
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
		slider.setImmediate(true);
		slider.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 2805427800313140023L;

			public void valueChange(ValueChangeEvent event) {
				Double value = (Double) event.getProperty().getValue();
				valueLabel.setValue(String.valueOf(value));
				configuration.setSessionTimeout(value);
			}
		});

		sessionTimeoutLayout.addComponent(slider);
		sessionTimeoutLayout.setExpandRatio(slider, 1);
		sessionTimeoutLayout.addComponent(valueLabel);
		sessionTimeoutLayout.setComponentAlignment(valueLabel,
				Alignment.BOTTOM_LEFT);

		settingsFieldsLayout.addComponent(sessionTimeoutLayout);

		/**
		 * Povolení registrací
		 */
		final CheckBox allowRegistrationsBox = new CheckBox(
				"Povolit registrace");
		allowRegistrationsBox.setValue(configuration.isRegistrations());
		allowRegistrationsBox.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -4168795771060533842L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				configuration.setRegistrations(allowRegistrationsBox.getValue());
			}
		});

		settingsFieldsLayout.addComponent(allowRegistrationsBox);

		/**
		 * Save tlačítko
		 */

		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				storeConfiguration(configuration);
			}
		});

		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

	private CoreConfiguration loadConfiguration() {
		try {
			return new ConfigurationUtils<CoreConfiguration>(
					new CoreConfiguration(), CoreConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
			return null;
		}
	}

	private void storeConfiguration(CoreConfiguration configuration) {
		try {
			ConfigurationManager.getInstance().storeConfiguration(
					CoreConfiguration.CONFIG_PATH, configuration);
		} catch (ConfigurationFileError e) {
			e.printStackTrace();
			showError500();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
		}
	}
}
