package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.ui.util.StringToDateConverter;
import org.myftp.gattserver.grass3.ui.util.StringToPreviewConverter;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@org.springframework.stereotype.Component()
@Scope("prototype")
public class MedicalRecordsTab extends VerticalLayout implements ISelectable {

	private static final long serialVersionUID = -5013459007975657195L;

	private IMedicFacade medicFacade;

	final Table table = new Table();
	private BeanItemContainer<MedicalRecordDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openCreateWindow(MedicalRecordDTO recordDTO) {
		Window win = new MedicalRecordCreateWindow(MedicalRecordsTab.this,
				recordDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow(final MedicalRecordDTO medicalRecord) {
		MedicalRecordsTab.this.setEnabled(false);
		UI.getCurrent().addWindow(
				new ConfirmSubwindow("Opravdu smazat '"
						+ medicalRecord.toString() + "' ?") {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							medicFacade.deleteMedicalRecord(medicalRecord);
							populateContainer();
						} catch (Exception e) {
							UI.getCurrent()
									.addWindow(
											new ErrorSubwindow(
													"Nezdařilo se smazat vybranou položku"));
						}
					}

					@Override
					protected void onClose(CloseEvent e) {
						MedicalRecordsTab.this.setEnabled(true);
					}
				});
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(medicFacade.getAllMedicalRecords());
		table.sort(new Object[] { "date" }, new boolean[] { false });
	}

	public MedicalRecordsTab(final IMedicFacade medicFacade) {

		this.medicFacade = medicFacade;

		setSpacing(true);
		setMargin(true);

		final Button newRecordBtn = new Button("Založit nový záznam");
		final Button modifyRecordBtn = new Button("Upravit záznam");
		final Button deleteBtn = new Button("Smazat");
		modifyRecordBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newRecordBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		modifyRecordBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));

		/**
		 * Přehled
		 */
		container = new BeanItemContainer<MedicalRecordDTO>(
				MedicalRecordDTO.class);
		table.setContainerDataSource(container);

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("institution", "Instituce");
		table.setColumnHeader("physician", "Ošetřující lékař");
		table.setColumnHeader("record", "Záznam");
		table.setWidth("100%");
		table.setConverter("date", new StringToDateConverter("d. MMMMM yyyy"));
		table.setConverter("record", new StringToPreviewConverter(50));
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new String[] { "date", "institution",
				"physician", "record" });
		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				deleteBtn.setEnabled(enabled);
				modifyRecordBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nového záznamu
		 */
		newRecordBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(null);
			}

		});
		buttonLayout.addComponent(newRecordBtn);

		/**
		 * Úprava záznamu
		 */
		modifyRecordBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow((MedicalRecordDTO) table.getValue());
			}

		});
		buttonLayout.addComponent(modifyRecordBtn);

		/**
		 * Smazání záznamu
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow((MedicalRecordDTO) table.getValue());
			}
		});
		buttonLayout.addComponent(deleteBtn);

		populateContainer();
	}

	@Override
	public void select() {
		populateContainer();
	}

}
