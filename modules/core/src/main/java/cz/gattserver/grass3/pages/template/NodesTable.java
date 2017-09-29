package cz.gattserver.grass3.pages.template;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.pages.factories.template.PageFactory;
import cz.gattserver.grass3.ui.util.ComparableLink;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.URLIdentifierUtils;
import cz.gattserver.web.common.ui.ImageIcons;

public class NodesTable extends Table {

	private static final long serialVersionUID = -2220485504407844582L;

	private final static String LINK_COLUMN = "link";
	private final static String ICON_COLUMN = "ikona";

	public NodesTable(AbstractGrassPage page) {
		// inject na Table nefunguje kvůli něčemu v předkovi
		final PageFactory nodePageFactory = (PageFactory) SpringContextHelper.getBean("nodePageFactory");

		setHeight("200px");

		addGeneratedColumn(LINK_COLUMN, new ColumnGenerator() {
			private static final long serialVersionUID = 1655758548572223217L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				NodeDTO node = (NodeDTO) itemId;
				return new ComparableLink(node.getName(), page.getPageResource(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
			}
		});

		addGeneratedColumn(ICON_COLUMN, new ColumnGenerator() {
			private static final long serialVersionUID = 3984587246251871002L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Embedded icon = new Embedded();
				icon.setSource(new ThemeResource(ImageIcons.BRIEFCASE_16_ICON));
				return icon;
			}
		});

	}

	public void populateTable(Collection<NodeDTO> data) {
		BeanItemContainer<NodeDTO> cont = new BeanItemContainer<>(NodeDTO.class, data);
		setContainerDataSource(cont);

		setVisibleColumns(ICON_COLUMN, LINK_COLUMN);
		setColumnHeaders("", "Název");
		setColumnWidth(ICON_COLUMN, 16);

		int min = 50;
		int element = 25;
		int max = 200;
		int header = 25;

		int size = data.size() * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		setHeight(size + "px");
	}

}