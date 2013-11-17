package org.myftp.gattserver.grass3.medic.web;

import java.util.Collection;

import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

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
	protected Window createCreateWindow(Component... triggerComponent) {
		return new PhysicianCreateWindow(triggerComponent) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id, Component... triggerComponent) {
		return new PhysicianDetailWindow(id, triggerComponent);
	}

	@Override
	protected Window createModifyWindow(PhysicianDTO dto,
			Component... triggerComponent) {
		return new PhysicianCreateWindow(dto, triggerComponent) {
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
