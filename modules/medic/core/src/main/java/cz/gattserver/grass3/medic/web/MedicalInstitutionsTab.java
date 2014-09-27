package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import cz.gattserver.grass3.ui.util.StringToPreviewConverter;

public class MedicalInstitutionsTab extends MedicPageTab<MedicalInstitutionDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	@Override
	protected Collection<MedicalInstitutionDTO> getTableItems() {
		return medicFacade.getAllMedicalInstitutions();
	}

	@Override
	protected Window createCreateWindow(Component... triggerComponent) {
		return new MedicalInstitutionCreateWindow(triggerComponent) {
			private static final long serialVersionUID = 5711665262096833291L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return new MedicalInstitutionDetailWindow(id, triggerComponent);
	}

	@Override
	protected Window createModifyWindow(MedicalInstitutionDTO dto,
			Component... triggerComponent) {
		return new MedicalInstitutionCreateWindow(dto, triggerComponent) {
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
		table.setConverter("web", new StringToPreviewConverter(50));
		table.setVisibleColumns(new String[] { "name", "address", "web" });
	}
}
