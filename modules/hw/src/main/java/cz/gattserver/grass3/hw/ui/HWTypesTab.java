package cz.gattserver.grass3.hw.ui;

import java.util.Set;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.hw.ui.windows.HWItemTypeCreateWindow;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;

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
		setMargin(new MarginInfo(true, false, false, false));

		final Button newTypeBtn = new Button("Založit nový typ");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newTypeBtn.setIcon(ImageIcon.PLUS_16_ICON.createResource());
		fixBtn.setIcon(ImageIcon.QUICKEDIT_16_ICON.createResource());
		deleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

		grid = new Grid<>(HWItemTypeTO.class);
		Set<HWItemTypeTO> data = getHWService().getAllHWTypes();
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
		newTypeBtn.addClickListener(e -> openNewTypeWindow(data, false));
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Úprava typu
		 */
		fixBtn.addClickListener(e -> openNewTypeWindow(data, true));
		buttonLayout.addComponent(fixBtn);

		/**
		 * Smazání typu
		 */
		deleteBtn.addClickListener(e -> openDeleteWindow(data));
		buttonLayout.addComponent(deleteBtn);
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
		Window win = new HWItemTypeCreateWindow(hwItemTypeDTO == null ? null : hwItemTypeDTO.getId()) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(HWItemTypeTO hwItemTypeDTO) {
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

	private void openDeleteWindow(final Set<HWItemTypeTO> data) {
		HWTypesTab.this.setEnabled(false);
		final HWItemTypeTO hwItemType = grid.getSelectedItems().iterator().next();
		UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItemType.getName() + "' (typ bude odebrán od všech označených položek HW)?",
				e -> {
					try {
						getHWService().deleteHWItemType(hwItemType.getId());
						data.remove(hwItemType);
						grid.getDataProvider().refreshAll();
					} catch (Exception ex) {
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
