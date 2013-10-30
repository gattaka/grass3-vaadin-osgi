package org.myftp.gattserver.grass3.medic.web;

import java.util.List;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class ScheduledVisitsCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IMedicFacade medicalFacade;

	public ScheduledVisitsCreateWindow(final Component triggerComponent) {
		super("Založení nové plánované návštěvy");

		medicalFacade = SpringContextHelper.getBean(IMedicFacade.class);

		triggerComponent.setEnabled(false);

		GridLayout winLayout = new GridLayout(2, 4);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		final ScheduledVisitDTO scheduledVisitDTO = new ScheduledVisitDTO();
		scheduledVisitDTO.setPurpose("");
		final BeanFieldGroup<ScheduledVisitDTO> fieldGroup = new BeanFieldGroup<ScheduledVisitDTO>(
				ScheduledVisitDTO.class);
		fieldGroup.setItemDataSource(scheduledVisitDTO);

		final TextField purposeField = new TextField("Účel návštěvy");
		winLayout.addComponent(purposeField, 0, 0);
		fieldGroup.bind(purposeField, "purpose");

		final TextField periodField = new TextField("Pravidelnost (měsíce)");
		winLayout.addComponent(periodField, 0, 1);
		fieldGroup.bind(periodField, "period");
		
		final DateField dateField = new DateField("Datum návštěvy");
		winLayout.addComponent(dateField, 1, 0);
		fieldGroup.bind(dateField, "date");

		List<MedicalRecordDTO> records = medicalFacade.getAllMedicalRecords();
		final ComboBox recordsComboBox = new ComboBox("Navazuje na kontrolu", records);
		recordsComboBox.setNullSelectionAllowed(true);
		winLayout.addComponent(recordsComboBox, 1, 1);
		fieldGroup.bind(recordsComboBox, "record");
		
		// TODO record, institution

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
									.saveScheduledVisit(scheduledVisitDTO) == false) {
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
