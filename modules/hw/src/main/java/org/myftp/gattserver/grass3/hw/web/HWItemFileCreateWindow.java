package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemFileDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class HWItemFileCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IHWFacade hwFacade;

	/**
	 * Otevře okno pro vytvoření nového linku na soubor
	 * 
	 * @param hwItem
	 *            hw ke kterému se link přidává
	 * @param document
	 *            {@code true} pokud se jedná o link na dokument, {@code false }
	 *            pokud jde o fotografi
	 */
	public HWItemFileCreateWindow(final HWItemDTO hwItemDTO, final boolean document) {
		super("Přidání nového souboru");

		setWidth("500px");
		
		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		HWItemFileDTO hwItemFileDTO = new HWItemFileDTO();
		hwItemFileDTO.setDescription("");
		hwItemFileDTO.setLink("");

		final BeanFieldGroup<HWItemFileDTO> fieldGroup = new BeanFieldGroup<HWItemFileDTO>(
				HWItemFileDTO.class);
		fieldGroup.setItemDataSource(hwItemFileDTO);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		TextField nameField = new TextField("URL");
		nameField.setImmediate(true);
		nameField.setWidth("100%");
		fieldGroup.bind(nameField, "link");
		layout.addComponent(nameField);

		TextField descriptionField = new TextField("Popis");
		fieldGroup.bind(descriptionField, "description");
		descriptionField.setSizeFull();
		layout.addComponent(descriptionField);

		Button createBtn;
		layout.addComponent(createBtn = new Button("Přidat",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {

						try {
							fieldGroup.commit();
							if (hwFacade.addHWItemFile(fieldGroup
									.getItemDataSource().getBean(), hwItemDTO,
									document)) {
								onSuccess();
							} else {
								UI.getCurrent().addWindow(
										new ErrorSubwindow(
												"Nezdařilo vložit nový link"));
							}
							close();
						} catch (FieldGroup.CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}

					}

				}));
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);

		setContent(layout);
	}

	protected abstract void onSuccess();

}
