package cz.gattserver.grass3.hw.ui;

import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.dialogs.HWItemTypeDialog;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class HWTypesTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient HWService hwService;

	private Grid<HWItemTypeTO> grid;

	private HWService getHWService() {
		if (hwService == null)
			hwService = SpringContextHelper.getBean(HWService.class);
		return hwService;
	}

	public HWTypesTab() {
		grid = new Grid<>(HWItemTypeTO.class);
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		Set<HWItemTypeTO> data = getHWService().getAllHWTypes();
		grid.setItems(data);

		grid.getColumnByKey("name").setHeader("Název");
		grid.setWidthFull();
		grid.setHeight("500px");
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
		Button deleteBtn = new DeleteGridButton<HWItemTypeTO>(set -> {
			HWItemTypeTO item = set.iterator().next();
			try {
				getHWService().deleteHWItemType(item.getId());
				data.remove(item);
				grid.getDataProvider().refreshAll();
			} catch (Exception ex) {
				new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
			}
		}, grid);
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
		new HWItemTypeDialog(hwItemTypeDTO == null ? null : hwItemTypeDTO) {
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

}
