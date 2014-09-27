package cz.gattserver.grass3.articles.pages;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.dto.ArticleDTO;

public class ArticleContentComponent extends CustomComponent {

	private static final long serialVersionUID = 8984705710416164767L;

	public ArticleContentComponent(ArticleDTO articleDTO) {

		VerticalLayout layout = new VerticalLayout();
		setCompositionRoot(layout);

		layout.addComponent(new Label(articleDTO.getOutputHTML(),
				ContentMode.HTML));

	}
}
