package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;

public class PhysiciansTab extends MedicPageTab<PhysicianDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public PhysiciansTab() {
		super(PhysicianDTO.class);
	}

	@Override
	protected Collection<PhysicianDTO> getTableItems() {
		return medicFacade.getAllPhysicians();
	}

	@Override
	protected Window createCreateWindow() {
		return new PhysicianCreateWindow() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id) {
		return new PhysicianDetailWindow(id);
	}

	@Override
	protected Window createModifyWindow(PhysicianDTO dto) {
		return new PhysicianCreateWindow(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(PhysicianDTO dto) {
		medicFacade.deletePhysician(dto);
	}

	@Override
	protected void customizeTable(Table table) {
		table.setColumnHeader("name", "Jméno");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new Object[] { "name" });
	}

}
