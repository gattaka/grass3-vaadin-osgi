package cz.gattserver.grass3.pages.template;

import java.util.Collection;
import java.util.List;

import com.vaadin.ui.Embedded;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.ServiceHolder;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.service.ContentService;
import cz.gattserver.grass3.template.DefaultContentOperations;
import cz.gattserver.grass3.ui.util.ComparableLink;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;

public class NewContentNodeTable extends Table {

	private static final long serialVersionUID = -2220485504407844582L;

	private final static String LINK_COLUMN = "link";
	private final static String ICON_COLUMN = "ikona";

	public NewContentNodeTable(AbstractGrassPage page, final NodeDTO node) {
		// inject na Table nefunguje kvůli něčemu v předkovi
		final ServiceHolder serviceHolder = (ServiceHolder) SpringContextHelper.getContext()
				.getBean(ServiceHolder.class);

		setHeight("200px");

		addGeneratedColumn(LINK_COLUMN, (Table source, Object itemId, Object columnId) -> {
			ContentService contentService = (ContentService) itemId;
			return new ComparableLink(contentService.getCreateNewContentLabel(),
					page.getPageResource(contentService.getContentEditorPageFactory(),
							DefaultContentOperations.NEW.toString(),
							URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
		});

		addGeneratedColumn(ICON_COLUMN, (Table source, Object itemId, Object columnId) -> {
			ContentService contentService = (ContentService) itemId;
			Embedded icon = new Embedded();
			icon.setSource(contentService.getContentIcon());
			return icon;
		});

		// jaké služby obsahů mám k dispozici ?
		List<ContentService> contentServices = serviceHolder.getContentServices();

		BeanItemContainer<ContentService> cont = new BeanItemContainer<>(ContentService.class, contentServices);
		setContainerDataSource(cont);

		setVisibleColumns(ICON_COLUMN, LINK_COLUMN);
		setColumnHeaders("", "Název");
		setColumnWidth(ICON_COLUMN, 16);
	}

	public void populateTable(Collection<NodeDTO> data) {
		throw new UnsupportedOperationException();
	}

}