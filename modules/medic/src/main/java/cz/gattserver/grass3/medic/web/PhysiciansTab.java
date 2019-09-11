package cz.gattserver.grass3.medic.web;

import java.util.ArrayList;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Window;

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
	protected Window createCreateDialog() {
		return new PhysicianCreateWindow() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				data = getItems();
				grid.setItems(data);
			}
		};
	}

	@Override
	protected Window createDetailDialog(Long id) {
		return new PhysicianDetailWindow(id);
	}

	@Override
	protected Window createModifyDialog(PhysicianDTO dto) {
		return new PhysicianCreateWindow(dto) {
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
		grid.getColumn("name").setCaption("Jméno");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name");
	}

}
