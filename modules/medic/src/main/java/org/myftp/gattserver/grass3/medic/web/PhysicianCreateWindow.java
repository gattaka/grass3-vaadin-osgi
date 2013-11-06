package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class PhysicianCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public PhysicianCreateWindow(final Component triggerComponent) {
		this(triggerComponent, null);
	}

	public PhysicianCreateWindow(final Component triggerComponent,
			PhysicianDTO modifiedPhysicianDTO) {
		super(modifiedPhysicianDTO == null ? "Přidání doktora"
				: "Úprava doktora");

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 3);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("300px");

		final PhysicianDTO physicianDTO = modifiedPhysicianDTO == null ? new PhysicianDTO()
				: modifiedPhysicianDTO;
		final BeanFieldGroup<PhysicianDTO> fieldGroup = new BeanFieldGroup<PhysicianDTO>(
				PhysicianDTO.class);
		fieldGroup.setItemDataSource(physicianDTO);

		final TextField nameField = new TextField("Jméno");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		fieldGroup.bind(nameField, "name");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 1);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(modifiedPhysicianDTO == null ? "Přidat" : "Upravit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade.savePhysician(physicianDTO) == false) {
								UI.getCurrent()
										.addWindow(
												new ErrorSubwindow(
														"Nezdařilo se vytvořit nový záznam"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}
					}
				}), 1, 2);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				triggerComponent.setEnabled(true);
			}

		});

	}

	protected abstract void onSuccess();

}
