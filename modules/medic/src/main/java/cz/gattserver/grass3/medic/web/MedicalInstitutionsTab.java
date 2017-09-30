package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.ui.util.StringToPreviewConverter;

public class MedicalInstitutionsTab extends MedicPageTab<MedicalInstitutionDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	protected Collection<MedicalInstitutionDTO> getTableItems() {
		return medicFacade.getAllMedicalInstitutions();
	}

	@Override
	protected Window createCreateWindow() {
		return new MedicalInstitutionCreateWindow() {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				populateContainer();
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
				populateContainer();
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
	protected void customizeTable(Table table) {
		table.setColumnHeader("name", "Název");
		table.setColumnHeader("address", "Adresa");
		table.setColumnHeader("web", "Stránky");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		// table.setConverter("web", new StringToPreviewConverter(50));
		table.setVisibleColumns(new Object[] { "name", "address", "web" });
	}
}
