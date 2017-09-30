package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;

public class MedicamentsTab extends MedicPageTab<MedicamentDTO> {

	private static final long serialVersionUID = -5013459007975657195L;

	public MedicamentsTab() {
		super(MedicamentDTO.class);
	}

	@Override
	protected Collection<MedicamentDTO> getTableItems() {
		return medicFacade.getAllMedicaments();
	}

	@Override
	protected Window createCreateWindow() {
		return new MedicamentCreateWindow() {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected Window createDetailWindow(Long id) {
		return new MedicamentDetailWindow(id);
	}

	@Override
	protected Window createModifyWindow(MedicamentDTO dto) {
		return new MedicamentCreateWindow(dto) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
	}

	@Override
	protected void deleteEntity(MedicamentDTO dto) {
		medicFacade.deleteMedicament(dto);
	}

	@Override
	protected void customizeTable(Table table) {
		table.setColumnHeader("name", "Název");
		table.setColumnHeader("tolerance", "Reakce, nežádoucí účinky");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new String[] { "name", "tolerance" });
	}

}
