package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;
import cz.gattserver.grass3.ui.util.StringToDateConverter;
import cz.gattserver.grass3.ui.util.StringToPreviewConverter;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	public void populateContainer() {
		super.populateContainer();
		Table table = (Table) this.table;
		table.sort(new Object[] { "date" }, new boolean[] { false });
		setTableColumns(table);
	}

	public MedicalRecordsTab() {
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
	protected Window createCreateWindow() {
		return new MedicalRecordCreateWindow(MedicalRecordsTab.this) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id) {
		return new MedicalRecordDetailWindow(id);
	}

	@Override
	protected Window createModifyWindow(MedicalRecordDTO dto) {
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

	private void setTableColumns(Table table) {
		table.setVisibleColumns(new Object[] { "date", "institution", "physician", "record" });
		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("institution", "Instituce");
		table.setColumnHeader("physician", "Ošetřující lékař");
		table.setColumnHeader("record", "Záznam");
	}

	@Override
	protected void customizeTable(Table table) {
		table.setWidth("100%");
		// table.setConverter("date", new StringToDateConverter("d. MMMMM
		// yyyy"));
		// table.setConverter("record", new StringToPreviewConverter(50));
		table.setSelectable(true);
		table.setImmediate(true);
		setTableColumns(table);
	}

}
