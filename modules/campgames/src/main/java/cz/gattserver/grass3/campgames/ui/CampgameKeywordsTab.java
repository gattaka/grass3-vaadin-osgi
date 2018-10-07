package cz.gattserver.grass3.campgames.ui;

import java.util.Set;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.campgames.CampgamesRole;
import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.campgames.ui.windows.CampgameKeywordWindow;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;

public class CampgameKeywordsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient CampgamesService campgamesService;

	private Grid<CampgameKeywordTO> grid;

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	public CampgameKeywordsTab() {
		setSpacing(true);
		setMargin(new MarginInfo(true, false, false, false));

		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		fixBtn.setIcon(ImageIcon.QUICKEDIT_16_ICON.createResource());
		deleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

		grid = new Grid<>(CampgameKeywordTO.class);
		Set<CampgameKeywordTO> data = getCampgamesService().getAllCampgameKeywords();
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

		buttonLayout.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(CampgamesRole.CAMPGAME_EDITOR));

		/**
		 * Úprava klíčového slova
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

	private void openNewTypeWindow(final Set<CampgameKeywordTO> data, boolean fix) {
		CampgameKeywordTO campgameKeywordTO = null;
		if (fix)
			campgameKeywordTO = grid.getSelectedItems().iterator().next();
		Window win = new CampgameKeywordWindow(campgameKeywordTO == null ? null : campgameKeywordTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess(CampgameKeywordTO campgameKeywordTO) {
				if (fix) {
					grid.getDataProvider().refreshItem(campgameKeywordTO);
				} else {
					data.add(campgameKeywordTO);
					grid.getDataProvider().refreshAll();
				}
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow(final Set<CampgameKeywordTO> data) {
		CampgameKeywordsTab.this.setEnabled(false);
		final CampgameKeywordTO campgameKeywordTO = grid.getSelectedItems().iterator().next();
		UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + campgameKeywordTO.getName()
				+ "' (klíčové slovo bude odebráno od všech označených her)?", e -> {
					try {
						getCampgamesService().deleteCampgameKeyword(campgameKeywordTO.getId());
						data.remove(campgameKeywordTO);
						grid.getDataProvider().refreshAll();
					} catch (Exception ex) {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				}) {
			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				CampgameKeywordsTab.this.setEnabled(true);
				super.close();
			}
		});
	}
}
