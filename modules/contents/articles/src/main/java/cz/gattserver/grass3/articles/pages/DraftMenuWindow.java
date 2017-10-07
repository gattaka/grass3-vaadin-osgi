package cz.gattserver.grass3.articles.pages;

import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.articles.dto.ArticleDraftOverviewDTO;
import cz.gattserver.grass3.pages.template.GridUtils;
import cz.gattserver.web.common.window.WebWindow;

public abstract class DraftMenuWindow extends WebWindow {

	private static final long serialVersionUID = 4105221381350726137L;

	private boolean continueFlag = false;

	protected abstract void onChoose(ArticleDraftOverviewDTO draft);

	protected abstract void onCancel();

	private void innerChoose(ArticleDraftOverviewDTO draft) {
		continueFlag = true;
		onChoose(draft);
	}

	public DraftMenuWindow(List<ArticleDraftOverviewDTO> drafts) {
		super("Rozpracované obsahy");

		Label label = new Label("Byly nalezeny rozpracované obsahy -- přejete si pokračovat v jejich úpravách?");
		addComponent(label);

		String nameBind = "customName";
		String lastModificationDateBind = "customLastModificationDate";

		final Grid<ArticleDraftOverviewDTO> grid = new Grid<>(ArticleDraftOverviewDTO.class);
		grid.setItems(drafts);
		grid.setHeight(GridUtils.processHeight(drafts.size()) + "px");
		grid.setWidth("900px");
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addColumn(a -> {
			return a.getContentNode().getName();
		}, new TextRenderer()).setCaption("Název").setId(nameBind).setWidth(200);

		grid.getColumn("text").setCaption("Náhled").setWidth(550);

		grid.addColumn(a -> {
			return a.getContentNode().getLastModificationDate() == null ? a.getContentNode().getCreationDate()
					: a.getContentNode().getLastModificationDate();
		}, new DateRenderer("%1$te.%1$tm.%1$tY %1$tH:%1$tM")).setCaption("Naposledy upraveno")
				.setId(lastModificationDateBind).setStyleGenerator(item -> "v-align-right");

		grid.setColumns(nameBind, "text", lastModificationDateBind);

		addComponent(grid);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setMargin(false);
		addComponent(btnLayout);
		setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);

		final Button confirm = new Button("Vybrat", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				innerChoose(grid.getSelectedItems().iterator().next());
				close();
			}
		});
		confirm.setEnabled(false);
		btnLayout.addComponent(confirm);
		btnLayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		grid.addSelectionListener(e -> confirm.setEnabled(!e.getAllSelectedItems().isEmpty()));

		Button close = new Button("Zrušit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		btnLayout.addComponent(close);
		btnLayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

	}

	@Override
	public void close() {
		super.close();
		if (!continueFlag)
			onCancel();
	}

}
