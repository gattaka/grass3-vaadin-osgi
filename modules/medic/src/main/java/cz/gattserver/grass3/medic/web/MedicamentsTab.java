package cz.gattserver.grass3.medic.web;

import java.util.ArrayList;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import com.vaadin.ui.Grid.SelectionMode;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;

public class MedicamentsTab extends MedicPageTab<MedicamentDTO, ArrayList<MedicamentDTO>> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicamentsTab() {
		super(MedicamentDTO.class);
	}

	@Override
	protected ArrayList<MedicamentDTO> getItems() {
		return new ArrayList<>(getMedicFacade().getAllMedicaments());
	}

	@Override
	protected Window createCreateDialog() {
		return new MedicamentCreateWindow() {
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
		return new MedicamentDetailWindow(id);
	}

	@Override
	protected Window createModifyDialog(MedicamentDTO dto) {
		return new MedicamentCreateWindow(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				grid.getDataProvider().refreshItem(dto);
			}
		};
	}

	@Override
	protected void deleteEntity(MedicamentDTO dto) {
		getMedicFacade().deleteMedicament(dto);
	}

	@Override
	protected void customizeGrid(Grid<MedicamentDTO> grid) {
		grid.getColumn("name").setCaption("Název");
		grid.getColumn("tolerance").setCaption("Reakce, nežádoucí účinky");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name", "tolerance");
	}

}
