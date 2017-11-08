package cz.gattserver.grass3.subwindows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeBreadcrumbDTO;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.WebWindow;

public abstract class ContentMoveWindow extends WebWindow {

	private static final long serialVersionUID = -2550619983411515006L;

	@Autowired
	private NodeFacade nodeFacade;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	private Button moveBtn;
	private Tree<NodeTreeDTO> tree;

	private Map<Long, NodeTreeDTO> cache;
	private Set<Long> visited;

	public ContentMoveWindow(final ContentNodeDTO contentNodeDTO) {
		super("Přesunout obsah");

		cache = new HashMap<>();
		visited = new HashSet<>();
		VerticalLayout layout = (VerticalLayout) getContent();

		setWidth("500px");

		Panel panel = new Panel();
		layout.addComponent(panel);
		panel.setHeight("300px");

		tree = new Tree<>();
		tree.setSelectionMode(SelectionMode.SINGLE);
		panel.setContent(tree);
		tree.addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));
		tree.setDataProvider(new TreeDataProvider<>(getTreeData()));
		tree.setItemIconGenerator(i -> new ThemeResource(ImageIcons.FOLDER_16_ICON));
		tree.setItemCaptionGenerator(NodeTreeDTO::getName);

		moveBtn = new Button("Přesunout");
		moveBtn.setEnabled(false);
		moveBtn.addClickListener(event -> {
			NodeTreeDTO nodeDTO = tree.getSelectedItems().iterator().next();
			contentNodeFacade.moveContent(nodeDTO.getId(), contentNodeDTO.getId());
			close();
			onMove();
		});

		layout.addComponent(moveBtn);
		layout.setComponentAlignment(moveBtn, Alignment.MIDDLE_RIGHT);

		NodeBreadcrumbDTO to = contentNodeDTO.getParent();
		Long parent = to.getParent().getId();
		while (parent != null) {
			NodeTreeDTO n = cache.get(parent);
			tree.expand(n);
			parent = n.getParentId();
		}
		tree.select(cache.get(to.getId()));

	}

	private TreeData<NodeTreeDTO> getTreeData() {
		TreeData<NodeTreeDTO> treeData = new TreeData<>();
		List<NodeTreeDTO> nodes = nodeFacade.getNodesForTree();
		nodes.forEach(n -> cache.put(n.getId(), n));
		nodes.forEach(n -> addTreeItem(treeData, n));
		return treeData;
	}

	private void addTreeItem(TreeData<NodeTreeDTO> treeData, NodeTreeDTO node) {
		if (visited.contains(node.getId()))
			return;
		NodeTreeDTO parent = cache.get(node.getParentId());
		if (parent != null && !visited.contains(parent.getId()))
			addTreeItem(treeData, parent);
		treeData.addItem(parent, node);
		visited.add(node.getId());
	}

	protected abstract void onMove();

}
