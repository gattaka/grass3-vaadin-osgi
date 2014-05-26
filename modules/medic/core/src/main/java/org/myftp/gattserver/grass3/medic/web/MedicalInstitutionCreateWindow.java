package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorWindow;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class MedicalInstitutionCreateWindow extends GrassWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public MedicalInstitutionCreateWindow(final Component... triggerComponent) {
		this(null, triggerComponent);
	}

	public MedicalInstitutionCreateWindow(
			MedicalInstitutionDTO modifiedMedicalInstitutionDTO,
			final Component... triggerComponent) {
		super(modifiedMedicalInstitutionDTO == null ? "Založení nové instituce"
				: "Úprava instituce", triggerComponent);

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		GridLayout winLayout = new GridLayout(2, 6);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("500px");

		final MedicalInstitutionDTO medicalInstitutionDTO = modifiedMedicalInstitutionDTO == null ? new MedicalInstitutionDTO()
				: modifiedMedicalInstitutionDTO;
		final BeanFieldGroup<MedicalInstitutionDTO> fieldGroup = new BeanFieldGroup<MedicalInstitutionDTO>(
				MedicalInstitutionDTO.class);
		fieldGroup.setItemDataSource(medicalInstitutionDTO);

		final TextField nameField = new TextField("Název");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		nameField.setImmediate(true);
		fieldGroup.bind(nameField, "name");

		final TextField addressField = new TextField("Adresa");
		winLayout.addComponent(addressField, 0, 1, 1, 1);
		addressField.setWidth("100%");
		addressField.setImmediate(true);
		fieldGroup.bind(addressField, "address");

		final TextField webField = new TextField("Webové stránky");
		winLayout.addComponent(webField, 0, 2, 1, 2);
		webField.setWidth("100%");
		fieldGroup.bind(webField, "web");

		final TextArea hoursField = new TextArea("Otevírací hodiny");
		winLayout.addComponent(hoursField, 0, 3, 1, 3);
		hoursField.setWidth("100%");
		hoursField.setHeight("200px");
		hoursField.setImmediate(true);
		fieldGroup.bind(hoursField, "hours");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 4);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(
				modifiedMedicalInstitutionDTO == null ? "Založit" : "Upravit",
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
												new ErrorWindow(
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
				}), 1, 5);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);
	}

	protected abstract void onSuccess();

}
