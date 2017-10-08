package cz.gattserver.grass3.hw.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private HWFacade hwFacade;

	private Grid<HWItemTypeDTO> grid;
	private Set<HWItemTypeDTO> data;

	public HWTypesTab() {
		SpringContextHelper.inject(this);

		setSpacing(true);
		setMargin(new MarginInfo(true, false, false, false));

		final Button newTypeBtn = new Button("Založit nový typ");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
		fixBtn.setIcon(new ThemeResource(ImageIcons.QUICKEDIT_16_ICON));
		deleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));

		grid = new Grid<>(HWItemTypeDTO.class);
		data = hwFacade.getAllHWTypes();
		grid.setItems(data);

		grid.getColumn("name").setCaption("Název");
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("name");
		grid.addSelectionListener(e -> {
			boolean enabled = !e.getAllSelectedItems().isEmpty();
			deleteBtn.setEnabled(enabled);
			fixBtn.setEnabled(enabled);
		});

		addComponent(grid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nového typu
		 */
		newTypeBtn.addClickListener(e -> openNewTypeWindow(false));
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Úprava typu
		 */
		fixBtn.addClickListener(e -> openNewTypeWindow(true));
		buttonLayout.addComponent(fixBtn);

		/**
		 * Smazání typu
		 */
		deleteBtn.addClickListener(e -> openDeleteWindow());
		buttonLayout.addComponent(deleteBtn);
	}

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		grid.setEnabled(enabled);
	}

	private void openNewTypeWindow(boolean fix) {
		HWItemTypeDTO hwItemTypeDTO = null;
		if (fix)
			hwItemTypeDTO = grid.getSelectedItems().iterator().next();
		Window win = new HWItemTypeCreateWindow(HWTypesTab.this, hwItemTypeDTO == null ? null : hwItemTypeDTO.getId()) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(HWItemTypeDTO hwItemTypeDTO) {
				if (fix)
					grid.getDataProvider().refreshItem(hwItemTypeDTO);
				else {
					data.add(hwItemTypeDTO);
					grid.getDataProvider().refreshAll();
				}
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow() {
		HWTypesTab.this.setEnabled(false);
		final HWItemTypeDTO hwItemType = grid.getSelectedItems().iterator().next();
		UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItemType.getName() + "' (typ bude odebrán od všech označených položek HW)?",
				e -> {
					if (hwFacade.deleteHWItemType(hwItemType.getId())) {
						data.remove(hwItemType);
						grid.getDataProvider().refreshAll();
					} else {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				}) {
			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				HWTypesTab.this.setEnabled(true);
				super.close();
			}
		});
	}
}
