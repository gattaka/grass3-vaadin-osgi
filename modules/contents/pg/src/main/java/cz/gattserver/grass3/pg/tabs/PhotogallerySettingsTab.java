package cz.gattserver.grass3.pg.tabs;

import javax.annotation.Resource;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.facades.ContentTagFacade;
import cz.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import cz.gattserver.grass3.pg.facade.PhotogalleryFacade;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.H2Label;

public class PhotogallerySettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "photogalleryFacade")
	private PhotogalleryFacade photogalleryFacade;

	@Resource(name = "contentTagFacade")
	private ContentTagFacade contentTagFacade;

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

		final PhotogalleryConfiguration configuration = photogalleryFacade.getConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Název adresářů miniatur
		 */
		final TextField miniaturesDirField = new TextField("Název adresářů miniatur");
		miniaturesDirField.setValue(String.valueOf(configuration.getMiniaturesDir()));
		FieldUtils.addValidator(miniaturesDirField, new StringLengthValidator("Nesmí být prázdné", 1, 1024));
		settingsFieldsLayout.addComponent(miniaturesDirField);

		/**
		 * Kořenový adresář fotogalerií
		 */
		final TextField rootDirField = new TextField("Kořenový adresář fotogalerií");
		rootDirField.setValue(String.valueOf(configuration.getRootDir()));
		FieldUtils.addValidator(rootDirField, new StringLengthValidator("Nesmí být prázdné", 1, 1024));
		settingsFieldsLayout.addComponent(rootDirField);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				// TODO
				if (rootDirField.getComponentError() == null && miniaturesDirField.getComponentError() == null) {
					configuration.setRootDir(rootDirField.getValue());
					configuration.setMiniaturesDir(miniaturesDirField.getValue());
					photogalleryFacade.storeConfiguration(configuration);
				}
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		return layout;

	}

}
