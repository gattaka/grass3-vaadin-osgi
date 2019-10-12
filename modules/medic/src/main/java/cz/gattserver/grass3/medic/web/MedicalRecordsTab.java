package cz.gattserver.grass3.medic.web;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordDTO, ArrayList<MedicalRecordDTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicalRecordsTab() {
		super(MedicalRecordDTO.class);
	}

	@Override
	protected ArrayList<MedicalRecordDTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllMedicalRecords());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicalRecordCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				refreshGrid();
			}
		};
	}

	public void refreshGrid() {
		data = getItems();
		grid.setItems(data);
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicalRecordDetailWindow(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicalRecordDTO dto) {
		return new MedicalRecordCreateDialog(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalRecordDTO dto) {
		getMedicFacade().deleteMedicalRecord(dto);
	}

	@Override
	protected void customizeGrid(Grid<MedicalRecordDTO> grid) {
		String fdateID = "fdate";
		grid.addColumn(new LocalDateTimeRenderer<>(MedicalRecordDTO::getDateTime, "dd.MM.yyyy HH:mm"))
				.setHeader("Datum").setId(fdateID);
		grid.getColumnByKey("institution").setHeader("Instituce");
		grid.getColumnByKey("physician").setHeader("Ošetřující lékař");
		grid.getColumnByKey("record").setHeader("Záznam");
		grid.setColumns(fdateID, "institution", "physician", "record");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

}
