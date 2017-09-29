package cz.gattserver.grass3.medic.web;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class PhysicianCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private MedicFacade medicalFacade;

	public PhysicianCreateWindow() {
		this(null);
	}

	public PhysicianCreateWindow(PhysicianDTO modifiedPhysicianDTO) {
		super(modifiedPhysicianDTO == null ? "Přidání doktora" : "Úprava doktora");

		medicalFacade = SpringContextHelper.getBean(MedicFacade.class);

		GridLayout winLayout = new GridLayout(2, 3);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("300px");

		final PhysicianDTO physicianDTO = modifiedPhysicianDTO == null ? new PhysicianDTO() : modifiedPhysicianDTO;
		final BeanFieldGroup<PhysicianDTO> fieldGroup = new BeanFieldGroup<PhysicianDTO>(PhysicianDTO.class);
		fieldGroup.setItemDataSource(physicianDTO);

		final TextField nameField = new TextField("Jméno");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		nameField.setImmediate(true);
		fieldGroup.bind(nameField, "name");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 1);

		Button saveBtn;
		winLayout.addComponent(
				saveBtn = new Button(modifiedPhysicianDTO == null ? "Přidat" : "Upravit", new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (medicalFacade.savePhysician(physicianDTO) == false) {
								UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se vytvořit nový záznam"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}
					}
				}), 1, 2);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);
	}

	protected abstract void onSuccess();

}
