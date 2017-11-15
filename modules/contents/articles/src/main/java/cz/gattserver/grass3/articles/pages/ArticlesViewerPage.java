package cz.gattserver.grass3.articles.pages;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.facade.ArticleFacade;
import cz.gattserver.grass3.components.DefaultContentOperations;
import cz.gattserver.grass3.js.JScriptItem;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.pages.template.ContentViewerPage;
import cz.gattserver.grass3.security.CoreACL;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.InfoWindow;
import cz.gattserver.web.common.window.WarnWindow;

public class ArticlesViewerPage extends ContentViewerPage {

	@Autowired
	private CoreACL coreACL;

	@Autowired
	private ArticleFacade articleFacade;

	@Autowired
	private PageFactory articlesViewerPageFactory;

	@Resource(name = "nodePageFactory")
	private PageFactory nodePageFactory;

	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	@Resource(name = "articlesEditorPageFactory")
	private PageFactory articlesEditorPageFactory;

	private ArticleDTO article;

	public ArticlesViewerPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Layout createPayload() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(analyzer.getCurrentPathToken());
		if (identifier == null) {
			UIUtils.showErrorPage404();
		}

		article = articleFacade.getArticleForDetail(identifier.getId());
		if (article == null) {
			UIUtils.showErrorPage404();
		}

		// RESCUE -- tohle by se normálně stát nemělo, ale umožňuje to aspoň
		// vyřešit stav, ve kterém existuje takovýto nezobrazitelný obsah
		if (article.getContentNode() == null) {
			articleFacade.deleteArticle(article.getId());
			UIUtils.redirect(getPageURL(homePageFactory.getPageName()));
		}

		if (article.getContentNode().isPublicated() || (UIUtils.getUser() != null
				&& (article.getContentNode().getAuthor().equals(UIUtils.getUser()) || UIUtils.getUser().isAdmin()))) {
		} else {
			UIUtils.showErrorPage403();
		}

		// CSS resources
		for (String css : article.getPluginCSSResources()) {

			// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
			// obejít problém se závislosí pluginů na úložišti theme apod. a
			// přitom umožnit aby se CSS odkazovali na externí zdroje
			if (!css.toLowerCase().startsWith("http://"))
				css = getRequest().getContextRoot() + "/VAADIN/themes/grass/" + css;
			loadCSS(css);
		}

		// JS resources
		int jsResourcesSize = article.getPluginJSResources().size();
		JScriptItem[] arr = new JScriptItem[jsResourcesSize];
		int i = 0;
		for (String resource : article.getPluginJSResources())
			arr[i++] = new JScriptItem(resource);
		loadJS(arr);

		return super.createPayload();
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
		return article.getContentNode();
	}

	@Override
	protected void createContent(VerticalLayout layout) {
		Label label = new Label(article.getOutputHTML(), ContentMode.HTML);
		label.setWidth("100%");
		layout.addComponent(label);
	}

	@Override
	protected PageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

	@Override
	protected void createContentOperations(CssLayout operationsListLayout) {
		super.createContentOperations(operationsListLayout);

		// Rychlé úpravy
		if (coreACL.canModifyContent(article.getContentNode(), UIUtils.getUser())) {
			String url = getPageURL(articlesEditorPageFactory, DefaultContentOperations.EDIT.toString(),
					URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName()));
			String script = "$(\".articles-basic-h-id\").each(" + "function(index){" + "$(this).attr(\"href\",\"" + url
					+ "/\" + $(this).attr(\"href\"));" + "$(this).html(\"<img alt=\\\" class=\\\"v-icon\\\" src=\\\""
					+ getRequest().getContextRoot() + "/VAADIN/themes/grass/" + ImageIcons.PENCIL_16_ICON
					+ "\\\"/>&nbsp\");" + "}" + ")";
			loadJS(new JScriptItem(script, true));
		}
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmWindow confirmSubwindow = new ConfirmWindow("Opravdu si přejete smazat tento článek ?", event -> {
			NodeOverviewDTO nodeDTO = article.getContentNode().getParent();
			final String nodeURL = getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

			// zdařilo se ? Pokud ano, otevři info okno a při
			// potvrzení jdi na kategorii
			try {
				articleFacade.deleteArticle(article.getId());
				InfoWindow infoSubwindow = new InfoWindow("Smazání článku proběhlo úspěšně.") {

					private static final long serialVersionUID = -6688396549852552674L;

					protected void onProceed(ClickEvent event) {
						UIUtils.redirect(nodeURL);
					};
				};
				UI.getCurrent().addWindow(infoSubwindow);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno a při
				// potvrzení jdi na kategorii
				WarnWindow warnSubwindow = new WarnWindow("Smazání článku se nezdařilo.") {

					private static final long serialVersionUID = -6688396549852552674L;

					protected void onProceed(ClickEvent event) {
						UIUtils.redirect(nodeURL);
					};
				};
				UI.getCurrent().addWindow(warnSubwindow);
			}
		});
		UI.getCurrent().addWindow(confirmSubwindow);
	}

	@Override
	protected void onEditOperation() {
		UIUtils.redirect(getPageURL(articlesEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName())));
	}
}
