package org.myftp.gattserver.grass3.articles.pages;

import java.util.Set;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.IArticleFacade;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.ContentViewerPage;
import org.myftp.gattserver.grass3.security.CoreACL;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("articlesViewerPage")
@Scope("prototype")
public class ArticlesViewerPage extends ContentViewerPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource(name = "articlesViewerPageFactory")
	private IPageFactory articlesViewerPageFactory;

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
		if (identifier == null)
			showError404();

		article = articleFacade.getArticleById(identifier.getId());
		if (article == null)
			showError404();

		// CSS resources
		for (String css : article.getPluginCSSResources()) {

			// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
			// obejít problém se závislosí pluginů na úložišti theme apod. a
			// přitom umožnit aby se CSS odkazovali na externí zdroje
			if (!css.toLowerCase().startsWith("http://"))
				css = getRequest().getContextRoot() + "/VAADIN/themes/grass/"
						+ css;

			StringBuilder loadStylesheet = new StringBuilder();
			loadStylesheet
					.append("var head= document.getElementsByTagName('head')[0];")
					.append("var link= document.createElement('link');")
					.append("link.type= 'text/css';")
					.append("link.rel= 'stylesheet';")
					.append("link.href= '" + css + "';")
					.append("head.appendChild(link);");
			JavaScript.getCurrent().execute(loadStylesheet.toString());
		}

		super.init();
	}

	@Override
	protected void submitInitJS(Set<String> initJS) {
		super.submitInitJS(initJS);

		// JS resources
		for (String js : article.getPluginJSResources()) {

			// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
			// obejít problém se závislosí pluginů na úložišti theme apod. a
			// přitom umožnit aby se JS odkazovali na externí zdroje
			if (!js.toLowerCase().startsWith("http://"))
				js = getRequest().getContextRoot() + "/VAADIN/themes/grass/"
						+ js;

			initJS.add(js);

		}

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
	protected void updateOperationsList(CssLayout operationsListLayout) {

		CoreACL acl = getUserACL();

		// Upravit
		if (acl.canModifyContent(article.getContentNode())) {
			Button saveButton = new Button("Upravit");
			saveButton.setIcon((com.vaadin.server.Resource) new ThemeResource(
					"img/tags/pencil_16.png"));
			saveButton.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {

					redirect(getPageURL(articlesEditorPageFactory,
							DefaultContentOperations.EDIT.toString(),
							URLIdentifierUtils.createURLIdentifier(article
									.getId(), article.getContentNode()
									.getName())));

				}

			});
			operationsListLayout.addComponent(saveButton);
		}

		// Smazat
		if (acl.canDeleteContent(article.getContentNode())) {
			Button deleteButton = new Button("Smazat");
			deleteButton
					.setIcon((com.vaadin.server.Resource) new ThemeResource(
							"img/tags/delete_16.png"));
			deleteButton.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {

					ConfirmSubwindow confirmSubwindow = new ConfirmSubwindow(
							"Opravdu si přejete smazat tento článek ?") {

						private static final long serialVersionUID = -3214040983143363831L;

						@Override
						protected void onConfirm(ClickEvent event) {

							NodeDTO node = nodeFacade.getNodeById(article
									.getContentNode().getParentID());

							final String category = getPageURL(
									articlesViewerPageFactory,
									URLIdentifierUtils.createURLIdentifier(
											node.getId(), node.getName()));

							// zdařilo se ? Pokud ano, otevři info okno a při
							// potvrzení jdi na kategorii
							if (articleFacade.deleteArticle(article)) {
								InfoSubwindow infoSubwindow = new InfoSubwindow(
										"Smazání článku proběhlo úspěšně.") {

									private static final long serialVersionUID = -6688396549852552674L;

									protected void onProceed(ClickEvent event) {
										redirect(category);
									};
								};
								getUI().addWindow(infoSubwindow);
							} else {
								// Pokud ne, otevři warn okno a při
								// potvrzení jdi na kategorii
								WarnSubwindow warnSubwindow = new WarnSubwindow(
										"Smazání článku se nezdařilo.") {

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

			});
			operationsListLayout.addComponent(deleteButton);
		}
	}

	@Override
	protected IPageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}
}
