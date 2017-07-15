package cz.gattserver.grass3.articles.pages;

import javax.annotation.Resource;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.dto.ArticleDTO;
import cz.gattserver.grass3.articles.facade.IArticleFacade;
import cz.gattserver.grass3.facades.IContentNodeFacade;
import cz.gattserver.grass3.facades.INodeFacade;
import cz.gattserver.grass3.facades.IUserFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.pages.factories.template.IPageFactory;
import cz.gattserver.grass3.pages.template.ContentViewerPage;
import cz.gattserver.grass3.pages.template.JScriptItem;
import cz.gattserver.grass3.security.ICoreACL;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.URLPathAnalyzer;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.InfoWindow;
import cz.gattserver.web.common.window.WarnWindow;

public class ArticlesViewerPage extends ContentViewerPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	@Resource(name = "nodePageFactory")
	private IPageFactory nodePageFactory;

	@Resource(name = "homePageFactory")
	private IPageFactory homePageFactory;

	@Resource(name = "articlesEditorPageFactory")
	private IPageFactory articlesEditorPageFactory;

	private ArticleDTO article;

	public ArticlesViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(analyzer.getCurrentPathToken());
		if (identifier == null) {
			showError404();
			return;
		}

		article = articleFacade.getArticleForDetail(identifier.getId());
		if (article == null) {
			showError404();
			return;
		}

		// RESCUE -- tohle by se normálně stát nemělo, ale umožňuje to aspoň
		// vyřešit stav, ve kterém existuje takovýto nezobrazitelný obsah
		if (article.getContentNode() == null) {
			articleFacade.deleteArticle(article);
			redirect(getPageURL(homePageFactory.getPageName()));
		}

		if (article.getContentNode().isPublicated()
				|| (getUser() != null && (article.getContentNode().getAuthor().equals(getUser())
						|| getUser().getRoles().contains(Role.ADMIN)))) {
		} else {
			showError403();
			return;
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
		for (String resource : article.getPluginJSResources()) {
			arr[i++] = new JScriptItem(resource);
		}
		loadJS(arr);

		super.init();
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
		return article.getContentNode();
	}

	@Override
	protected void createContent(VerticalLayout layout) {
		layout.addComponent(new Label(article.getOutputHTML(), ContentMode.HTML));
	}

	@Override
	protected IPageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

	@Override
	protected void createContentOperations(CssLayout operationsListLayout) {
		super.createContentOperations(operationsListLayout);

		// Rychlé úpravy
		if (coreACL.canModifyContent(article.getContentNode(), getUser())) {
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
		ConfirmWindow confirmSubwindow = new ConfirmWindow("Opravdu si přejete smazat tento článek ?") {

			private static final long serialVersionUID = -3214040983143363831L;

			@Override
			protected void onConfirm(ClickEvent event) {

				NodeBreadcrumbDTO nodeDTO = article.getContentNode().getParent();

				final String nodeURL = getPageURL(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

				// zdařilo se ? Pokud ano, otevři info okno a při
				// potvrzení jdi na kategorii
				try {
					articleFacade.deleteArticle(article);
					InfoWindow infoSubwindow = new InfoWindow("Smazání článku proběhlo úspěšně.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(nodeURL);
						};
					};
					getUI().addWindow(infoSubwindow);
				} catch (Exception e) {
					// Pokud ne, otevři warn okno a při
					// potvrzení jdi na kategorii
					WarnWindow warnSubwindow = new WarnWindow("Smazání článku se nezdařilo.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(nodeURL);
						};
					};
					getUI().addWindow(warnSubwindow);
				}

				// zavři původní confirm okno
				getUI().removeWindow(this);

			}
		};
		getUI().addWindow(confirmSubwindow);

	}

	@Override
	protected void onEditOperation() {
		redirect(getPageURL(articlesEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName())));
	}
}
