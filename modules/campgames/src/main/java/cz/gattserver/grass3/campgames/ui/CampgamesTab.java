package cz.gattserver.grass3.campgames.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FetchItemsCallback;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.campgames.ui.windows.CampgameDetailWindow;
import cz.gattserver.grass3.campgames.ui.windows.CampgameCreateWindow;
import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.ErrorWindow;

public class CampgamesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private static final String NAME_BIND = "nameBind";
	private static final String PLAYERS_BIND = "playersBind";
	private static final String PREPARATIONTIME_BIND = "preparationTimeBind";
	private static final String PLAYTIME_BIND = "playTimeBind";

	private transient CampgamesService campgamesService;

	private Grid<CampgameOverviewTO> grid;
	private AdvancedTokenField keywordsFilter;

	private GrassRequest grassRequest;
	private CampgameFilterTO filterDTO;

	public CampgamesTab(GrassRequest grassRequest) {
		filterDTO = new CampgameFilterTO();
		this.grassRequest = grassRequest;

		setSpacing(true);
		setMargin(new MarginInfo(true, false, false, false));

		final Button newCampgameBtn = new Button("Založit novou hru");
		final Button detailsBtn = new Button("Detail");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		detailsBtn.setEnabled(false);
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newCampgameBtn.setIcon(ImageIcon.PLUS_16_ICON.createResource());
		detailsBtn.setIcon(ImageIcon.CLIPBOARD_16_ICON.createResource());
		fixBtn.setIcon(ImageIcon.QUICKEDIT_16_ICON.createResource());
		deleteBtn.setIcon(ImageIcon.DELETE_16_ICON.createResource());

		// Filtr na klíčová slova
		keywordsFilter = new AdvancedTokenField();
		keywordsFilter.getInputField().setWidth("200px");
		keywordsFilter.getInputField().addValueChangeListener(e -> {
			if (e.getValue() != null)
				keywordsFilter.addToken(e.getValue());
		});
		keywordsFilter.addTokenAddListener(token -> populate());
		keywordsFilter.addTokenRemoveListener(e -> populate());
		HorizontalLayout keywordsFilterLayout = new HorizontalLayout();
		keywordsFilterLayout.setSpacing(true);
		addComponent(keywordsFilterLayout);

		keywordsFilterLayout.addComponent(keywordsFilter);

		Set<CampgameKeywordTO> keywords = getCampgamesService().getAllCampgameKeywords();
		keywords.forEach(t -> {
			Token to = new Token(t.getName());
			keywordsFilter.addTokenToInputField(to);
		});
		keywordsFilter.setAllowNewItems(false);
		keywordsFilter.getInputField().setPlaceholder("Filtrovat dle klíčových slov");
		keywordsFilter.isEnabled();

		// Tabulka her
		grid = new Grid<>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");

		grid.addColumn(CampgameOverviewTO::getName).setId(NAME_BIND).setCaption("Název").setWidth(200);
		grid.addColumn(CampgameOverviewTO::getPlayers).setId(PLAYERS_BIND).setCaption("Hráčů").setWidth(220);
		grid.addColumn(CampgameOverviewTO::getPlayTime).setId(PLAYTIME_BIND).setCaption("Délka hry").setWidth(220);
		grid.addColumn(CampgameOverviewTO::getPreparationTime).setId(PREPARATIONTIME_BIND).setCaption("Délka přípravy");
		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterDTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(NAME_BIND).setComponent(nazevColumnField);

		// Hráčů
		TextField playersColumnField = new TextField();
		playersColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		playersColumnField.setWidth("100%");
		playersColumnField.addValueChangeListener(e -> {
			filterDTO.setPlayers(e.getValue());
			populate();
		});
		filteringHeader.getCell(PLAYERS_BIND).setComponent(playersColumnField);

		// Délka hry
		TextField playtimeColumnField = new TextField();
		playtimeColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		playtimeColumnField.setWidth("100%");
		playtimeColumnField.addValueChangeListener(e -> {
			filterDTO.setPlayTime(e.getValue());
			populate();
		});
		filteringHeader.getCell(PLAYTIME_BIND).setComponent(playtimeColumnField);

		// Délka přípravy
		TextField preparationTimeColumnField = new TextField();
		preparationTimeColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		preparationTimeColumnField.setWidth("100%");
		preparationTimeColumnField.addValueChangeListener(e -> {
			filterDTO.setPreparationTime(e.getValue());
			populate();
		});
		filteringHeader.getCell(PREPARATIONTIME_BIND).setComponent(preparationTimeColumnField);

		populate();
		grid.sort(NAME_BIND);

		grid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick()) {
				openDetailWindow(event.getItem().getId());
			}
		});

		grid.addSelectionListener(e -> {
			boolean enabled = e.getFirstSelectedItem().isPresent();
			deleteBtn.setEnabled(enabled);
			detailsBtn.setEnabled(enabled);
			fixBtn.setEnabled(enabled);
		});

		addComponent(grid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		boolean admin = SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
				.contains(Role.ADMIN);

		// Založení nové hry
		newCampgameBtn.addClickListener(e -> openItemWindow(false));
		buttonLayout.addComponent(newCampgameBtn);
		newCampgameBtn.setVisible(admin);

		// Zobrazení detailů hry
		detailsBtn.addClickListener(e -> openDetailWindow(grid.getSelectedItems().iterator().next().getId()));
		buttonLayout.addComponent(detailsBtn);

		// Oprava údajů existující hry
		fixBtn.addClickListener(e -> openItemWindow(true));
		buttonLayout.addComponent(fixBtn);
		fixBtn.setVisible(admin);

		// Smazání hry
		deleteBtn.addClickListener(e -> openDeleteWindow());
		buttonLayout.addComponent(deleteBtn);
		deleteBtn.setVisible(admin);

	}

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	private void populate() {
		List<Token> collection = keywordsFilter.getTokens();
		List<String> types = new ArrayList<>();
		collection.forEach(t -> types.add(t.getValue()));
		filterDTO.setKeywords(types);

		FetchItemsCallback<CampgameOverviewTO> fetchItems = (sortOrder, offset, limit) -> getCampgamesService()
				.getCampgames(filterDTO, QuerydslUtil.transformOffsetLimit(offset, limit),
						QuerydslUtil.transformOrdering(sortOrder, column -> {
							switch (column) {
							case NAME_BIND:
								return "name";
							case PLAYERS_BIND:
								return "players";
							case PLAYTIME_BIND:
								return "playTime";
							case PREPARATIONTIME_BIND:
								return "preparationTime";
							default:
								return column;
							}
						}))
				.stream();
		SerializableSupplier<Integer> sizeCallback = () -> getCampgamesService().countCampgames(filterDTO);
		CallbackDataProvider<CampgameOverviewTO, Long> provider = new CallbackDataProvider<>(
				q -> fetchItems.fetchItems(q.getSortOrders(), q.getOffset(), q.getLimit()), q -> sizeCallback.get(),
				CampgameOverviewTO::getId);
		grid.setDataProvider(provider);
	}

	private void addWindow(Window win) {
		UI.getCurrent().addWindow(win);
	}

	private void openItemWindow(boolean fix) {
		CampgameTO campgame = null;
		if (fix) {
			if (grid.getSelectedItems().isEmpty())
				return;
			Long id = grid.getSelectedItems().iterator().next().getId();
			campgame = getCampgamesService().getCampgame(id);
		}
		addWindow(new CampgameCreateWindow(campgame == null ? null : campgame.getId()) {
			private static final long serialVersionUID = -1397391593801030584L;

			@Override
			protected void onSuccess(CampgameTO dto) {
				populate();
				CampgameOverviewTO filterTO = new CampgameOverviewTO();
				filterTO.setId(dto.getId());
				grid.select(filterTO);
			}
		});
	}

	private void openDetailWindow(Long id) {
		addWindow(new CampgameDetailWindow(id, grassRequest).setChangeListener(this::populate));
	}

	private void openDeleteWindow() {
		if (grid.getSelectedItems().isEmpty())
			return;
		CampgamesTab.this.setEnabled(false);
		CampgameOverviewTO to = grid.getSelectedItems().iterator().next();
		addWindow(new ConfirmWindow("Opravdu smazat '" + to.getName() + "' ?", e -> {
			try {
				getCampgamesService().deleteCampgame(to.getId());
				populate();
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
			}
		}) {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				CampgamesTab.this.setEnabled(true);
				super.close();
			}

		});
	}

}
