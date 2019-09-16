package cz.gattserver.grass3.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass3.exception.GrassPageException;
import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.services.ContentTagService;
import cz.gattserver.grass3.ui.components.ContentsLazyGrid;
import cz.gattserver.grass3.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

@Route("tag")
public class TagPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = -2716406706042922900L;

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	private String tagParameter;

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		tagParameter = parameter;
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {

		FetchItemsCallback<String> fetchItemsCallback = (filter, offset, limit) -> contentTagFacade
				.findByFilter(filter, offset, limit).stream();
		SerializableFunction<String, Integer> serializableFunction = filter -> contentTagFacade.countByFilter(filter);
		TokenField tokenField = new TokenField(fetchItemsCallback, serializableFunction);
		layout.add(tokenField);

		// ---

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagParameter);
		if (identifier == null)
			throw new GrassPageException(404);

		ContentTagOverviewTO tag = contentTagFacade.getTagById(identifier.getId());

		if (tag == null)
			throw new GrassPageException(404);

		// Obsahy
		layout.add(new H2("Obsahy označené tagem: " + tag.getName()));

		ContentsLazyGrid tagContentsTable = new ContentsLazyGrid();
		tagContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getByTag(tag.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByTag(tag.getId()));
		tagContentsTable.setWidth("100%");
		layout.add(tagContentsTable);
	}

}
