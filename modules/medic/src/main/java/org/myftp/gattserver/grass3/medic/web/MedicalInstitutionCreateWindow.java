package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class MedicalInstitutionCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public MedicalInstitutionCreateWindow(final Component triggerComponent) {
		super("Založení nového instituce");

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 4);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		final MedicalInstitutionDTO medicalInstitutionDTO = new MedicalInstitutionDTO();
		medicalInstitutionDTO.setName("");
		final BeanFieldGroup<MedicalInstitutionDTO> fieldGroup = new BeanFieldGroup<MedicalInstitutionDTO>(
				MedicalInstitutionDTO.class);
		fieldGroup.setItemDataSource(medicalInstitutionDTO);

		final TextField nameField = new TextField();
		winLayout.addComponent(nameField, 0, 0);
		fieldGroup.bind(nameField, "name");

		final TextField addressField = new TextField();
		winLayout.addComponent(addressField, 0, 1);
		fieldGroup.bind(addressField, "address");

		final TextField webField = new TextField();
		winLayout.addComponent(webField, 1, 0);
		fieldGroup.bind(webField, "web");

		final TextArea hoursField = new TextArea();
		winLayout.addComponent(hoursField, 1, 1);
		fieldGroup.bind(hoursField, "hours");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 2);

		winLayout.addComponent(new Button("Založit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade
									.saveMedicalInstitution(medicalInstitutionDTO) == false) {
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
				}), 1, 3);

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
