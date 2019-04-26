package cz.gattserver.grass3.songs.web;

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.FetchItemsCallback;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import cz.gattserver.grass3.model.util.QuerydslUtil;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.services.SecurityService;
import cz.gattserver.grass3.songs.SongsRole;
import cz.gattserver.grass3.songs.facades.SongsService;
import cz.gattserver.grass3.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass3.songs.model.interfaces.SongTO;
import cz.gattserver.grass3.ui.components.CreateGridButton;
import cz.gattserver.grass3.ui.components.DeleteGridButton;
import cz.gattserver.grass3.ui.components.ModifyGridButton;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.MultiUpload;

public class ListTab extends VerticalLayout {

	private static final long serialVersionUID = 594189301140808163L;

	@Autowired
	private SecurityService securityService;

	private transient SongsService songsService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<SongOverviewTO> grid;

	private SongTO choosenSong;
	private SongOverviewTO filterTO;

	private TabSheet tabSheet;
	private SongTab songTab;

	public ListTab(GrassRequest request, TabSheet tabSheet) {
		SpringContextHelper.inject(this);
		filterTO = new SongOverviewTO();
		this.tabSheet = tabSheet;
	}

	public SongTab getSongTab() {
		return songTab;
	}

	public ListTab setSongTab(SongTab songTab) {
		this.songTab = songTab;
		return this;
	}

	public ListTab init() {
		if (songTab == null)
			throw new IllegalStateException();

		setMargin(new MarginInfo(true, false, false, false));

		grid = new Grid<>();
		Column<SongOverviewTO, String> nazevColumn = grid.addColumn(SongOverviewTO::getName).setCaption("Název");
		Column<SongOverviewTO, String> authorColumn = grid.addColumn(SongOverviewTO::getAuthor).setCaption("Autor")
				.setWidth(250);
		Column<SongOverviewTO, Integer> yearColumn = grid.addColumn(SongOverviewTO::getYear).setCaption("Rok")
				.setWidth(60);
		grid.setWidth("100%");
		grid.setHeight("600px");
		addComponent(grid);

		HeaderRow filteringHeader = grid.appendHeaderRow();

		// Název
		TextField nazevColumnField = new TextField();
		nazevColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		nazevColumnField.setWidth("100%");
		nazevColumnField.addValueChangeListener(e -> {
			filterTO.setName(e.getValue());
			populate();
		});
		filteringHeader.getCell(nazevColumn).setComponent(nazevColumnField);

		// Autor
		TextField authorColumnField = new TextField();
		authorColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		authorColumnField.setWidth("100%");
		authorColumnField.addValueChangeListener(e -> {
			filterTO.setAuthor(e.getValue());
			populate();
		});
		filteringHeader.getCell(authorColumn).setComponent(authorColumnField);

		// Rok
		TextField yearColumnField = new TextField();
		yearColumnField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		yearColumnField.setWidth("100%");
		yearColumnField.addValueChangeListener(e -> {
			filterTO.setYear(StringUtils.isBlank(e.getValue()) ? null : Integer.valueOf(e.getValue()));
			populate();
		});
		filteringHeader.getCell(yearColumn).setComponent(yearColumnField);

		populate();

		grid.addItemClickListener((e) -> {
			if (e.getMouseEventDetails().isDoubleClick())
				chooseSong(e.getItem().getId(), false);
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.addComponent(new CreateGridButton("Přidat", event -> {
			UI.getCurrent().addWindow(new SongWindow() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = getSongsService().saveSong(to);
					populate();
					chooseSong(to.getId(), true);
				}
			});
		}));

		final TextField importedAuthorField = new TextField();
		importedAuthorField.setPlaceholder("Autor importovaných písní");
		importedAuthorField.setWidth("200px");
		btnLayout.addComponent(importedAuthorField);

		MultiUpload multiFileUpload = new MultiUpload("Import") {
			private static final long serialVersionUID = -415832652157894459L;

			public void fileUploadFinished(InputStream in, String fileName, String mime, long size,
					int filesLeftInQueue) {
				SongTO to = getSongsService().importSong(importedAuthorField.getValue(), in, fileName, mime, size,
						filesLeftInQueue);
				populate();
				chooseSong(to.getId(), true);
			}
		};
		multiFileUpload.setEnabled(false);
		btnLayout.addComponent(multiFileUpload);

		importedAuthorField
				.addValueChangeListener(e -> multiFileUpload.setEnabled(StringUtils.isNotBlank(e.getValue())));

		btnLayout.addComponent(new ModifyGridButton<SongOverviewTO>("Upravit", event -> {
			UI.getCurrent().addWindow(
					new SongWindow(getSongsService().getSongById(grid.getSelectedItems().iterator().next().getId())) {

						private static final long serialVersionUID = 5264621441522056786L;

						@Override
						protected void onSave(SongTO to) {
							to = getSongsService().saveSong(to);
							songTab.showDetail(to);
							populate();
							selectSong(to.getId());
						}
					});
		}, grid));

		btnLayout.addComponent(new DeleteGridButton<SongOverviewTO>("Smazat", items -> {
			for (SongOverviewTO s : items)
				getSongsService().deleteSong(s.getId());
			populate();
			songTab.showDetail(null);
		}, grid));

		return this;
	}

	public void selectSong(Long id) {
		SongOverviewTO to = new SongOverviewTO();
		to.setId(id);
		grid.deselectAll();
		grid.select(to);
	}

	public void chooseSong(Long id, boolean selectSong) {
		choosenSong = getSongsService().getSongById(id);
		songTab.showDetail(choosenSong);
		tabSheet.setSelectedTab(songTab);
		if (selectSong)
			selectSong(id);
	}

	public SongTO getChoosenSong() {
		return choosenSong;
	}

	private SongsService getSongsService() {
		if (songsService == null)
			songsService = SpringContextHelper.getBean(SongsService.class);
		return songsService;
	}

	public void populate() {
		FetchItemsCallback<SongOverviewTO> fetchItems = (sortOrder, offset, limit) -> getSongsService()
				.getSongs(filterTO, QuerydslUtil.transformOffsetLimit(offset, limit)).stream();
		SerializableSupplier<Integer> sizeCallback = () -> getSongsService().getSongsCount(filterTO);
		CallbackDataProvider<SongOverviewTO, Long> provider = new CallbackDataProvider<>(
				q -> fetchItems.fetchItems(q.getSortOrders(), q.getOffset(), q.getLimit()), q -> sizeCallback.get(),
				SongOverviewTO::getId);
		grid.setDataProvider(provider);
	}

}
