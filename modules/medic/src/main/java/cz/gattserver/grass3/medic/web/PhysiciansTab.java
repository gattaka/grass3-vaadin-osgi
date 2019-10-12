package cz.gattserver.grass3.medic.web;

import java.util.ArrayList;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import cz.gattserver.grass3.medic.dto.PhysicianDTO;

public class PhysiciansTab extends MedicPageTab<PhysicianDTO, ArrayList<PhysicianDTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public PhysiciansTab() {
		super(PhysicianDTO.class);
	}

	@Override
	protected ArrayList<PhysicianDTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllPhysicians());
	}

	@Override
	protected Dialog createCreateDialog() {
		return new PhysicianCreateDialog() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Dialog createDetailDialog(Long id) {
		return new PhysicianDetailDialog(id);
	}

	@Override
	protected Dialog createModifyDialog(PhysicianDTO dto) {
		return new PhysicianCreateDialog(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(PhysicianDTO dto) {
		getMedicFacade().deletePhysician(dto);
	}

	@Override
	protected void customizeGrid(Grid<PhysicianDTO> grid) {
		grid.getColumnByKey("name").setHeader("Jméno");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name");
	}

}
