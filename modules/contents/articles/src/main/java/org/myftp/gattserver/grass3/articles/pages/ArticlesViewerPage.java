package org.myftp.gattserver.grass3.articles.pages;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.IArticleFacade;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.ContentViewerPage;
import org.myftp.gattserver.grass3.pages.template.JScriptItem;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubWindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.springframework.context.annotation.Scope;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("articlesViewerPage")
@Scope("prototype")
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

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "articlesEditorPageFactory")
	private IPageFactory articlesEditorPageFactory;

	private ArticleDTO article;

	public ArticlesViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(analyzer
				.getCurrentPathToken());
		if (identifier == null) {
			showError404();
			return;
		}

		article = articleFacade.getArticleForDetail(identifier.getId());
		if (article == null) {
			showError404();
			return;
		}

		if (article.getContentNode().isPublicated()
				|| (getUser() != null && (article.getContentNode().getAuthor().equals(getUser()) || getUser()
						.getRoles().contains(Role.ADMIN)))) {
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
					+ getRequest().getContextRoot() + "/VAADIN/themes/grass/img/tags/pencil_16.png\\\"/>&nbsp\");"
					+ "}" + ")";
			loadJS(new JScriptItem(script, true));
		}
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmSubWindow confirmSubwindow = new ConfirmSubWindow("Opravdu si přejete smazat tento článek ?") {

			private static final long serialVersionUID = -3214040983143363831L;

			@Override
			protected void onConfirm(ClickEvent event) {

				NodeDTO node = article.getContentNode().getParent();

				final String category = getPageURL(categoryPageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));

				// zdařilo se ? Pokud ano, otevři info okno a při
				// potvrzení jdi na kategorii
				if (articleFacade.deleteArticle(article)) {
					InfoSubwindow infoSubwindow = new InfoSubwindow("Smazání článku proběhlo úspěšně.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(category);
						};
					};
					getUI().addWindow(infoSubwindow);
				} else {
					// Pokud ne, otevři warn okno a při
					// potvrzení jdi na kategorii
					WarnSubwindow warnSubwindow = new WarnSubwindow("Smazání článku se nezdařilo.") {

						private static final long serialVersionUID = -6688396549852552674L;

						protected void onProceed(ClickEvent event) {
							redirect(category);
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
