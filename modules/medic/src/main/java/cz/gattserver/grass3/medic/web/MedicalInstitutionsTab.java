package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;

public class MedicalInstitutionsTab extends MedicPageTab<MedicalInstitutionDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	protected Collection<MedicalInstitutionDTO> getItems() {
		return medicFacade.getAllMedicalInstitutions();
	}

	@Override
	protected Window createCreateWindow() {
		return new MedicalInstitutionCreateWindow() {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id) {
		return new MedicalInstitutionDetailWindow(id);
	}

	@Override
	protected Window createModifyWindow(MedicalInstitutionDTO dto) {
		return new MedicalInstitutionCreateWindow(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicalInstitutionDTO dto) {
		medicFacade.deleteMedicalInstitution(dto);
	}

	public MedicalInstitutionsTab() {
		super(MedicalInstitutionDTO.class);
	}

	@Override
	protected void customizeGrid(Grid<MedicalInstitutionDTO> grid) {
		grid.getColumn("name").setCaption("Název");
		grid.getColumn("address").setCaption("Adresa");
		grid.getColumn("web").setCaption("Stránky");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name", "address", "web");
	}
}
