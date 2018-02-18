package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

import cz.gattserver.grass3.medic.dto.MedicalRecordDTO;

public class MedicalRecordsTab extends MedicPageTab<MedicalRecordDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicalRecordsTab() {
		super(MedicalRecordDTO.class);
	}

	@Override
	protected Collection<MedicalRecordDTO> getItems() {
		return getMedicFacade().getAllMedicalRecords();
	}

	@Override
	protected Window createCreateWindow() {
		return new MedicalRecordCreateWindow() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id) {
		return new MedicalRecordDetailWindow(id);
	}

	@Override
	protected Window createModifyWindow(MedicalRecordDTO dto) {
		return new MedicalRecordCreateWindow(dto) {
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
		grid.addColumn(MedicalRecordDTO::getDate, new LocalDateTimeRenderer("dd.MM.yyyy")).setCaption("Datum")
				.setId(fdateID);
		grid.getColumn("institution").setCaption("Instituce");
		grid.getColumn("physician").setCaption("Ošetřující lékař");
		grid.getColumn("record").setCaption("Záznam");
		grid.setColumns(fdateID, "institution", "physician", "record");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.sort(fdateID);
	}

}
