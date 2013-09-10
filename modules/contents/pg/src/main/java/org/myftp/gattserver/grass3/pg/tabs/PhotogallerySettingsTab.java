package org.myftp.gattserver.grass3.pg.tabs;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import org.myftp.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import org.myftp.gattserver.grass3.tabs.template.AbstractSettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("photogallerySettingsTab")
@Scope("prototype")
public class PhotogallerySettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "photogalleryFacade")
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	public PhotogallerySettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final PhotogalleryConfiguration configuration = photogalleryFacade
				.getConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>",
				ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Název adresářů miniatur
		 */
		final TextField miniaturesDirField = new TextField(
				"Název adresářů miniatur");
		miniaturesDirField.setValue(String.valueOf(configuration
				.getMiniaturesDir()));
		miniaturesDirField.addValidator(new StringLengthValidator(
				"Nesmí být prázdné", 1, 1024, false));
		settingsFieldsLayout.addComponent(miniaturesDirField);

		/**
		 * Kořenový adresář fotogalerií
		 */
		final TextField rootDirField = new TextField(
				"Kořenový adresář fotogalerií");
		rootDirField.setValue(String.valueOf(configuration.getRootDir()));
		rootDirField.addValidator(new StringLengthValidator(
				"Nesmí být prázdné", 1, 1024, false));
		settingsFieldsLayout.addComponent(rootDirField);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {

				if (rootDirField.isValid() && miniaturesDirField.isValid())

					configuration.setRootDir(rootDirField.getValue());
				configuration.setMiniaturesDir(miniaturesDirField.getValue());
				photogalleryFacade.storeConfiguration(configuration);
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

}
