package cz.gattserver.grass3.hw.ui;

import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.windows.HWItemTypeCreateDialog;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient HWService hwService;

	private Grid<HWItemTypeTO> grid;

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public HWTypesTab() {
		setSpacing(true);

		grid = new Grid<>(HWItemTypeTO.class);
		Set<HWItemTypeTO> data = getHWService().getAllHWTypes();
		grid.setItems(data);

		grid.getColumnByKey("name").setHeader("Název");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name");

		add(grid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Založení nového typu
		 */
		Button newTypeBtn = new CreateButton("Založit nový typ", e -> openNewTypeWindow(data, false));
		buttonLayout.add(newTypeBtn);

		/**
		 * Úprava typu
		 */
		Button fixBtn = new ModifyGridButton<HWItemTypeTO>(set -> openNewTypeWindow(data, true), grid);
		buttonLayout.add(fixBtn);

		/**
		 * Smazání typu
		 */
		Button deleteBtn = new DeleteGridButton<HWItemTypeTO>(e -> openDeleteWindow(data), grid);
		buttonLayout.add(deleteBtn);
	}

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		grid.setEnabled(enabled);
	}

	private void openNewTypeWindow(final Set<HWItemTypeTO> data, boolean fix) {
		HWItemTypeTO hwItemTypeDTO = null;
		if (fix)
			hwItemTypeDTO = grid.getSelectedItems().iterator().next();
		new HWItemTypeCreateDialog(hwItemTypeDTO == null ? null : hwItemTypeDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(HWItemTypeTO hwItemTypeDTO) {
				if (fix) {
					grid.getDataProvider().refreshItem(hwItemTypeDTO);
				} else {
					data.add(hwItemTypeDTO);
					grid.getDataProvider().refreshAll();
				}
			}
		}.open();
	}

	private void openDeleteWindow(final Set<HWItemTypeTO> data) {
		HWTypesTab.this.setEnabled(false);
		final HWItemTypeTO hwItemType = grid.getSelectedItems().iterator().next();
		new ConfirmDialog(
				"Opravdu smazat '" + hwItemType.getName() + "' (typ bude odebrán od všech označených položek HW)?",
				e -> {
					try {
						getHWService().deleteHWItemType(hwItemType.getId());
						data.remove(hwItemType);
						grid.getDataProvider().refreshAll();
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
					}
				}) {
			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				HWTypesTab.this.setEnabled(true);
				super.close();
			}
		}.open();
	}
}
