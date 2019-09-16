package cz.gattserver.grass3.articles.ui.windows;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Span;

import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.ui.util.GridUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class DraftMenuDialog extends WebDialog {

	private static final long serialVersionUID = 4105221381350726137L;

	private transient ArticleService articleFacade;

	private boolean continueFlag = false;

	protected abstract void onChoose(ArticleDraftOverviewTO draft);

	protected abstract void onCancel();

	private ArticleService getArticleService() {
		if (articleFacade == null)
			articleFacade = SpringContextHelper.getBean(ArticleService.class);
		return articleFacade;
	}

	private void innerChoose(ArticleDraftOverviewTO draft) {
		continueFlag = true;
		onChoose(draft);
	}

	public DraftMenuDialog(List<ArticleDraftOverviewTO> drafts) {
		super("Rozpracované obsahy");

		Span label = new Span("Byly nalezeny rozpracované obsahy -- přejete si pokračovat v jejich úpravách?");
		addComponent(label);

		String nameBind = "customName";
		String lastModificationDateBind = "customLastModificationDate";

		final Grid<ArticleDraftOverviewTO> grid = new Grid<>(ArticleDraftOverviewTO.class);
		grid.setItems(drafts);
		grid.setHeight(GridUtils.processHeight(drafts.size()) + "px");
		grid.setWidth("900px");
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addColumn(a -> a.getContentNode().getName(), new TextRenderer()).setCaption("Název").setId(nameBind)
				.setWidth(200);

		grid.getColumn("text").setCaption("Náhled").setWidth(550);

		grid.addColumn(a -> a.getContentNode().getLastModificationDate() == null ? a.getContentNode().getCreationDate()
				: a.getContentNode().getLastModificationDate(), new LocalDateTimeRenderer("d.MM.yyyy HH:mm"))
				.setCaption("Naposledy upraveno").setId(lastModificationDateBind)
				.setStyleGenerator(item -> "v-align-right");

		grid.setColumns(nameBind, "text", lastModificationDateBind);
		grid.addItemClickListener(e -> {
			if (e.getMouseEventDetails().isDoubleClick()) {
				innerChoose(e.getItem());
				close();
			}
		});

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

		Button delete = new Button("Smazat",
				ev -> UI.getCurrent().addWindow(new ConfirmDialog("Smazat rozpracovaný článek?", e -> {
					ArticleDraftOverviewTO to = grid.getSelectedItems().iterator().next();
					getArticleService().deleteArticle(to.getId());
					drafts.remove(to);
					grid.getDataProvider().refreshAll();
					grid.deselectAll();
				})));
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
