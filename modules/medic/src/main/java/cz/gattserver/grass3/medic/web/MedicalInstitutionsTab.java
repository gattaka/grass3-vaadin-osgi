package cz.gattserver.grass3.medic.web;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;

public class MedicalInstitutionsTab extends MedicPageTab<MedicalInstitutionDTO, ArrayList<MedicalInstitutionDTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	protected ArrayList<MedicalInstitutionDTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllMedicalInstitutions());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new MedicalInstitutionCreateDialog() {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new MedicalInstitutionDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(MedicalInstitutionDTO dto) {
		return new MedicalInstitutionCreateDialog(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalInstitutionDTO dto) {
		getMedicFacade().deleteMedicalInstitution(dto);
	}

	public MedicalInstitutionsTab() {
		super(MedicalInstitutionDTO.class);
	}

	@Override
	protected void customizeGrid(Grid<MedicalInstitutionDTO> grid) {
		grid.getColumnByKey("name").setHeader("Název");
		grid.getColumnByKey("address").setHeader("Adresa");
		grid.getColumnByKey("web").setHeader("Stránky");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name", "address", "web");
	}
}
