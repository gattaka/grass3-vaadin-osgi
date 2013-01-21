package org.myftp.gattserver.grass3.articles.favlink.web;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.articles.favlink.config.FavlinkConfiguration;
import org.myftp.gattserver.grass3.config.ConfigurationFileError;
import org.myftp.gattserver.grass3.config.ConfigurationManager;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FavlinkSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private VerticalLayout settingsLayout = new VerticalLayout();

	public FavlinkSettingsWindow() {
		setName("favlink-settings");
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);
		layout.addComponent(settingsLayout);

	}

	private FavlinkConfiguration loadConfiguration() {
		try {
			return new ConfigurationUtils<FavlinkConfiguration>(
					new FavlinkConfiguration(),
					FavlinkConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
			return null;
		}
	}

	private void storeConfiguration(FavlinkConfiguration configuration) {
		try {
			ConfigurationManager.getInstance().storeConfiguration(
					FavlinkConfiguration.CONFIG_PATH, configuration);
		} catch (ConfigurationFileError e) {
			e.printStackTrace();
			showError500();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
		}
	}

	@Override
	protected void onShow() {

		final FavlinkConfiguration configuration = loadConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>",
				Label.CONTENT_XHTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Výstupní cesta
		 */
		final TextField outputPathField = new TextField(
				"Nastavení výstupní cesty");
		outputPathField.setValue(configuration.getOutputPath());
		settingsFieldsLayout.addComponent(outputPathField);

		/**
		 * Save tlačítko
		 */

		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				configuration.setOutputPath((String) outputPathField.getValue());
				storeConfiguration(configuration);
			}
		});

		settingsFieldsLayout.addComponent(saveButton);

		super.onShow();
	}
}
