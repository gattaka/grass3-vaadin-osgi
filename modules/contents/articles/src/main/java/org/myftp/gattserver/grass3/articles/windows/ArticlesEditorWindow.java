package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;

import org.myftp.gattserver.grass3.articles.PluginServiceHolder;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.util.CategoryUtils;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ArticlesEditorWindow extends TwoColumnWindow {

	private static final long serialVersionUID = -5148523174527532785L;

	private NodeDTO category;
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;
	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;

	private HorizontalLayout toolsLayout = new HorizontalLayout();
	private VerticalLayout editorTextLayout;
	private final TextArea articleTextArea = new TextArea();

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
		layout.setSpacing(true);
		layout.setMargin(true);

		VerticalLayout toolsPartLayout = new VerticalLayout();
		layout.addComponent(toolsPartLayout);
		toolsPartLayout.addComponent(new Label("<h2>Nástroje</h2>",
				Label.CONTENT_XHTML));
		toolsPartLayout.addComponent(toolsLayout);
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {
		editorTextLayout = layout;
		editorTextLayout.setSpacing(true);
		editorTextLayout.setMargin(true);
	}

	private void updateToolsMenu() {

		toolsLayout.removeAllComponents();

		/**
		 * Projdi zaregistrované pluginy a vytvoř menu nástrojů
		 */
		for (String group : PluginServiceHolder.getInstance()
				.getRegisteredGroups()) {

			VerticalLayout groupLayout = new VerticalLayout();
			if (group != null)
				groupLayout.addComponent(new Label("<h3>" + group + "</h3>",
						Label.CONTENT_XHTML));
			toolsLayout.addComponent(groupLayout);
			HorizontalLayout groupToolsLayout = new HorizontalLayout();
			groupLayout.addComponent(groupToolsLayout);

			final ReferenceHolder<EditorButtonResources> holder = new ReferenceHolder<EditorButtonResources>();
			for (EditorButtonResources resources : PluginServiceHolder
					.getInstance().getGroupTags(group)) {

				holder.setValue(resources);

				Button button = new Button(resources.getDescription());
				button.setIcon(resources.getImage());
				button.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 607422393151282918L;

					public void buttonClick(ClickEvent event) {
						executeJavaScript("insert('"
								+ holder.getValue().getPrefix() + "','"
								+ holder.getValue().getSuffix() + "');");
					}
				});
				groupToolsLayout.addComponent(button);
			}
		}

	}

	private void updateEditorTextPart() {

		editorTextLayout.removeAllComponents();

		VerticalLayout articleNameLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleNameLayout);
		articleNameLayout.addComponent(new Label("<h2>Název článku</h2>",
				Label.CONTENT_XHTML));
		TextField articleNameField = new TextField();
		articleNameLayout.addComponent(articleNameField);

		VerticalLayout articleKeywordsLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleKeywordsLayout);
		articleKeywordsLayout.addComponent(new Label("<h2>Klíčová slova</h2>",
				Label.CONTENT_XHTML));
		TextField articleKeyWords = new TextField();
		articleKeywordsLayout.addComponent(articleKeyWords);

		VerticalLayout articleContentLayout = new VerticalLayout();
		editorTextLayout.addComponent(articleContentLayout);
		articleContentLayout.addComponent(new Label("<h2>Obsah článku</h2>",
				Label.CONTENT_XHTML));
		articleContentLayout.addComponent(articleTextArea);

		// tlačítka
		Button previewButton = new Button("Náhled");
		previewButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 607422393151282918L;

			public void buttonClick(ClickEvent event) {

				ArticleDTO articleDTO = articleFacade.processPreview(String
						.valueOf(articleTextArea.getValue()));

				PreviewWindow previewWindow = new PreviewWindow(articleDTO);
				addWindow(previewWindow);
			}

		});
		editorTextLayout.addComponent(previewButton);
	}

	@Override
	protected void onShow() {

		updateToolsMenu();
		updateEditorTextPart();

		// editor.js
		StringBuilder loadScript = new StringBuilder();
		loadScript
				.append("var head= document.getElementsByTagName('head')[0];")
				.append("var script= document.createElement('script');")
				.append("script.type= 'text/javascript';")
				.append("script.src= '/VAADIN/themes/grass/articles/js/editor.js';")
				.append("head.appendChild(script);");
		executeJavaScript(loadScript.toString());

		articleTextArea.setValue("");

		super.onShow();
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {

		category = CategoryUtils.parseURLIdentifier(relativeUri);
		if (category == null)
			showError404();

		category = nodeFacade.getNodeById(category.getId());

		return super.handleURI(context, relativeUri);
	}

}
