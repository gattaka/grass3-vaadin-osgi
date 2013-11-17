package org.myftp.gattserver.grass3.medic.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.MedicalRecordDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.ui.util.StringToDateConverter;
import org.myftp.gattserver.grass3.ui.util.StringToPreviewConverter;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	public void populateContainer() {
		super.populateContainer();
		table.sort(new Object[] { "date" }, new boolean[] { false });
	}

	public MedicalRecordsTab(final IMedicFacade medicFacade) {
		super(MedicalRecordDTO.class);
	}

	@Override
	public void select() {
		populateContainer();
	}

	@Override
	protected Collection<MedicalRecordDTO> getTableItems() {
		return medicFacade.getAllMedicalRecords();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new MedicalRecordCreateWindow(MedicalRecordsTab.this) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return new MedicalRecordDetailWindow(id, triggerComponent);
	}

	@Override
	protected Window createModifyWindow(MedicalRecordDTO dto,
			Component... triggerComponent) {
		return new MedicalRecordCreateWindow(MedicalRecordsTab.this, dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalRecordDTO dto) {
		medicFacade.deleteMedicalRecord(dto);
	}

	@Override
	protected void customizeTable(Table table) {
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
	}

}
