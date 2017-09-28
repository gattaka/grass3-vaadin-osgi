package cz.gattserver.grass3.subwindows;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.web.common.window.WebWindow;

public abstract class ContentMoveWindow extends WebWindow {

	private static final long serialVersionUID = -2550619983411515006L;

	@Autowired
	private NodeFacade nodeFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	private Button moveBtn;
	private Tree tree;

	private HashMap<Long, NodeDTO> cache;

	private enum TreePropertyID {
		NÁZEV, IKONA
	}

	public ContentMoveWindow(final ContentNodeDTO contentNodeDTO) {
		super("Přesunout obsah");

		cache = new HashMap<>();
		VerticalLayout layout = (VerticalLayout) getContent();

		setWidth("500px");

		Panel panel = new Panel();
		layout.addComponent(panel);
		panel.setHeight("300px");

		tree = new Tree();
		panel.setContent(tree);
		tree.setImmediate(true);
		tree.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 191011037696709486L;

			public void valueChange(ValueChangeEvent event) {
				moveBtn.setEnabled(event.getProperty().getValue() != null);
			}
		});
		tree.setContainerDataSource(getCategoriesContainer());
		tree.setItemCaptionPropertyId(TreePropertyID.NÁZEV);
		tree.setItemIconPropertyId(TreePropertyID.IKONA);

		moveBtn = new Button("Přesunout");
		moveBtn.setEnabled(false);
		moveBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 5609432440775504923L;

			@Override
			public void buttonClick(ClickEvent event) {
				NodeDTO nodeDTO = (NodeDTO) tree.getValue();
				contentNodeFacade.moveContent(nodeDTO.getId(), contentNodeDTO.getId());
				close();
				onMove();
			}
		});

		layout.addComponent(moveBtn);
		layout.setComponentAlignment(moveBtn, Alignment.MIDDLE_RIGHT);

		NodeBreadcrumbDTO to = contentNodeDTO.getParent();
		tree.select(cache.get(to.getId()));
		while (to != null) {
			tree.expandItem(cache.get(to.getId()));
			to = to.getParent();
		}

	}

	private HierarchicalContainer getCategoriesContainer() {

		// Create new container
		HierarchicalContainer container = new HierarchicalContainer();
		// Create containerproperty for name
		container.addContainerProperty(TreePropertyID.NÁZEV, String.class, null);
		// Create containerproperty for icon
		container.addContainerProperty(TreePropertyID.IKONA, ThemeResource.class,
				new ThemeResource("../runo/icons/16/folder.png"));

		List<NodeDTO> rootNodes = nodeFacade.getRootNodes();
		populateContainer(container, rootNodes, null);

		return container;
	}

	@SuppressWarnings("unchecked")
	private void populateContainer(HierarchicalContainer container, List<NodeDTO> nodes, NodeDTO parent) {

		for (NodeDTO node : nodes) {
			cache.put(node.getId(), node);
			Item item = container.addItem(node);
			item.getItemProperty(TreePropertyID.NÁZEV).setValue(node.getName());
			if (parent != null)
				container.setParent(node, parent);

			List<NodeDTO> childrenNodes = nodeFacade.getNodesByParentNode(node);
			if (childrenNodes != null && !childrenNodes.isEmpty()) {
				container.setChildrenAllowed(node, true);
				populateContainer(container, childrenNodes, node);
			} else {
				container.setChildrenAllowed(node, false);
			}
		}
	}

	protected abstract void onMove();

}
