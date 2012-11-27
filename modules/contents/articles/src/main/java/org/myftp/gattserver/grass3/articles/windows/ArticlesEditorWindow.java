package org.myftp.gattserver.grass3.articles.windows;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.myftp.gattserver.grass3.articles.service.IPluginService;
import org.myftp.gattserver.grass3.articles.service.ISelectionDecorator;
import org.myftp.gattserver.grass3.articles.service.demo.DemoPluginService;
import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.util.CategoryUtils;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ArticlesEditorWindow extends TwoColumnWindow {

	private static final long serialVersionUID = -5148523174527532785L;

	private NodeDTO category;
	private NodeFacade nodeFacade = NodeFacade.INSTANCE;

	private HorizontalLayout toolsLayout = new HorizontalLayout();
	private final TextArea textArea = new TextArea();

	@Override
	protected void createLeftColumnContent(VerticalLayout layout) {
		layout.addComponent(new Label("<h2>Nástroje</h2>", Label.CONTENT_XHTML));
		layout.addComponent(toolsLayout);
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {
		layout.addComponent(new Label("<h2>Článek</h2>", Label.CONTENT_XHTML));
	}

	@Override
	protected void onShow() {

		toolsLayout.removeAllComponents();

		/**
		 * DEMO seznam pluginů
		 */
		List<IPluginService> iPluginServices = new ArrayList<IPluginService>();
		iPluginServices.add(new DemoPluginService());

		/**
		 * Projdi zaregistrované pluginy a vytvoř menu nástrojů
		 */
		final ReferenceHolder<ISelectionDecorator> decorator = new ReferenceHolder<ISelectionDecorator>();
		for (IPluginService iPluginService : iPluginServices) {

			decorator.setValue(iPluginService.getPluginSelectionDecorator());

			Button button = new Button(iPluginService.getPluginButtonCaption());
			button.setIcon(iPluginService.getPluginButtonImageResource());
			button.addListener(new Button.ClickListener() {

				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {

					textArea.setValue(decorator.getValue().decorate(
							(String) textArea.getValue()));

				}
			});
			toolsLayout.addComponent(button);
		}

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
