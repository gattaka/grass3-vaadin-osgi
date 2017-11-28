package cz.gattserver.grass3.articles.ui.windows;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class DraftMenuWindow extends WebWindow {

	private static final long serialVersionUID = 4105221381350726137L;

	@Autowired
	private ArticleService articleFacade;

	private boolean continueFlag = false;

	protected abstract void onChoose(ArticleDraftOverviewTO draft);

	protected abstract void onCancel();

	private void innerChoose(ArticleDraftOverviewTO draft) {
		continueFlag = true;
		onChoose(draft);
	}

	public DraftMenuWindow(List<ArticleDraftOverviewTO> drafts) {
		super("Rozpracované obsahy");

		Label label = new Label("Byly nalezeny rozpracované obsahy -- přejete si pokračovat v jejich úpravách?");
		addComponent(label);

		String nameBind = "customName";
		String lastModificationDateBind = "customLastModificationDate";

		final Grid<ArticleDraftOverviewTO> grid = new Grid<>(ArticleDraftOverviewTO.class);
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
		}, new LocalDateTimeRenderer("d.MM.yyyy HH:mm")).setCaption("Naposledy upraveno")
				.setId(lastModificationDateBind).setStyleGenerator(item -> "v-align-right");

		grid.setColumns(nameBind, "text", lastModificationDateBind);

		addComponent(grid);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setMargin(false);
		addComponent(btnLayout);
		setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);

		final Button confirm = new Button("Vybrat", e -> {
			innerChoose(grid.getSelectedItems().iterator().next());
			close();
		});
		confirm.setEnabled(false);
		btnLayout.addComponent(confirm);
		btnLayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		Button close = new Button("Zrušit", e -> close());
		btnLayout.addComponent(close);
		btnLayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

		Button delete = new Button("Smazat", ev -> {
			UI.getCurrent().addWindow(new ConfirmWindow("Smazat rozpracovaný článek?", e -> {
				ArticleDraftOverviewTO to = grid.getSelectedItems().iterator().next();
				articleFacade.deleteArticle(to.getId());
				drafts.remove(to);
				grid.getDataProvider().refreshAll();
				grid.deselectAll();
			}));
		});
		btnLayout.addComponent(delete);
		btnLayout.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
		delete.setEnabled(false);

		grid.addSelectionListener(e -> {
			confirm.setEnabled(!e.getAllSelectedItems().isEmpty());
			delete.setEnabled(!e.getAllSelectedItems().isEmpty());
		});
	}

	@Override
	public void close() {
		super.close();
		if (!continueFlag)
			onCancel();
	}

}