package cz.gattserver.grass3.ui.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public class NodeTree extends VerticalLayout {

	private static final long serialVersionUID = -7457362355620092284L;

	private Map<Long, NodeOverviewTO> cache;
	private Set<Long> visited;

	private TreeGrid<NodeOverviewTO> grid;

	private Set<NodeOverviewTO> draggedItems;

	@Autowired
	private NodeService nodeFacade;

	public NodeTree() {
		this(false);
	}

	public TreeGrid<NodeOverviewTO> getGrid() {
		return grid;
	}

	public NodeTree(boolean enableEditFeatures) {
		SpringContextHelper.inject(this);

		setSpacing(true);
		setMargin(false);

		cache = new HashMap<>();
		visited = new HashSet<>();

		grid = new TreeGrid<>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth("100%");
		addComponent(grid);

		grid.addColumn(NodeOverviewTO::getName).setCaption("Název");
		populate();

		if (enableEditFeatures)
			initEditFeatures();

		setWidth("100%");
	}

	private void initEditFeatures() {
		/*
		 * Drag drop features
		 */
		TreeGridDragSource<NodeOverviewTO> dragSource = new TreeGridDragSource<>(grid);
		dragSource.setEffectAllowed(EffectAllowed.MOVE);
		dragSource.addGridDragStartListener(e -> draggedItems = e.getDraggedItems());

		TreeGridDropTarget<NodeOverviewTO> dropTarget = new TreeGridDropTarget<>(grid, DropMode.ON_TOP_OR_BETWEEN);
		dropTarget.setDropEffect(DropEffect.MOVE);
		dropTarget.addTreeGridDropListener(event -> {
			NodeOverviewTO dropNode = event.getDropTargetRow().get();
			switch (event.getDropLocation()) {
			case ON_TOP:
				// vkládám do dropNode
				break;
			case ABOVE:
			case BELOW:
				// vkládám do parenta dropNode
				dropNode = dropNode.getParentId() == null ? null : cache.get(dropNode.getParentId());
				break;
			case EMPTY:
			default:
				// výchozí je vkládání do root
				dropNode = null;
			}
			for (NodeOverviewTO n : draggedItems)
				moveAction(n, dropNode);
			grid.getDataProvider().refreshAll();
		});

		/*
		 * Context menu
		 */
		GridContextMenu<NodeOverviewTO> gridMenu = new GridContextMenu<>(grid);
		gridMenu.addGridBodyContextMenuListener(e -> {
			e.getContextMenu().removeItems();
			if (e.getItem() != null) {
				NodeOverviewTO node = (NodeOverviewTO) e.getItem();
				grid.select(node);
				e.getContextMenu().addItem("Smazat", ImageIcon.DELETE_16_ICON.createResource(),
						selectedItem -> deleteAction(node));
				e.getContextMenu().addItem("Přejmenovat", ImageIcon.PENCIL_16_ICON.createResource(),
						selectedItem -> renameAction(node));
			}
			e.getContextMenu().addItem("Vytvořit zde novou", ImageIcon.PLUS_16_ICON.createResource(),
					selectedItem -> createNodeAction(e.getItem() == null ? null : (NodeOverviewTO) e.getItem()));
		});

		/*
		 * Delete shortcut
		 */
		addShortcutListener(new ShortcutListener("Delete", ShortcutAction.KeyCode.DELETE, null) {
			private static final long serialVersionUID = -7239845094514060176L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (!grid.getSelectedItems().isEmpty())
					deleteAction(grid.getSelectedItems().iterator().next());
			}
		});

		/*
		 * Buttons
		 */
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		addComponent(btnLayout);

		CreateGridButton createBtn = new CreateGridButton("Vytvořit", e -> createNodeAction(
				grid.getSelectedItems().isEmpty() ? null : grid.getSelectedItems().iterator().next()));
		btnLayout.addComponent(createBtn);

		ModifyGridButton<NodeOverviewTO> modifyBtn = new ModifyGridButton<>("Přejmenovat", this::renameAction, grid);
		btnLayout.addComponent(modifyBtn);

		// mazání chci po jednom
		DeleteGridButton<NodeOverviewTO> deleteBtn = new DeleteGridButton<>("Smazat",
				nodes -> deleteAction(nodes.iterator().next()), grid);
		deleteBtn.setEnableResolver(items -> items.size() == 1);
		btnLayout.addComponent(deleteBtn);

	}

	public void populate() {
		List<NodeOverviewTO> nodes = nodeFacade.getNodesForTree();
		TreeData<NodeOverviewTO> treeData = new TreeData<>();
		nodes.forEach(n -> cache.put(n.getId(), n));
		nodes.forEach(n -> addTreeItem(treeData, n));
		grid.setDataProvider(new TreeDataProvider<>(treeData));
	}

	private void addTreeItem(TreeData<NodeOverviewTO> treeData, NodeOverviewTO node) {
		if (visited.contains(node.getId()))
			return;
		NodeOverviewTO parent = cache.get(node.getParentId());
		if (parent != null && !visited.contains(parent.getId()))
			addTreeItem(treeData, parent);
		treeData.addItem(parent, node);
		visited.add(node.getId());
	}

	public void expandTo(Long id) {
		NodeOverviewTO to = cache.get(id);
		Long parent = to.getParentId();
		while (parent != null) {
			NodeOverviewTO n = cache.get(parent);
			grid.expand(n);
			parent = n.getParentId();
		}
		grid.select(cache.get(to.getId()));
	}

	private void moveAction(NodeOverviewTO node, NodeOverviewTO newParent) {
		if (node.equals(newParent) || node.getParentId() == null && newParent == null
				|| node.getParentId() != null && newParent != null && node.getParentId().equals(newParent.getId()))
			return; // bez změn

		UI.getCurrent().addWindow(new ConfirmWindow("Opravdu přesunout '" + node.getName() + "' do "
				+ (newParent == null ? "kořene sekce" : "'" + newParent.getName() + "'") + "?", e -> {
					try {
						nodeFacade.moveNode(node.getId(), newParent == null ? null : newParent.getId());
						node.setParentId(newParent == null ? null : newParent.getId());
						grid.getTreeData().setParent(node, newParent);
						grid.getDataProvider().refreshAll();
						expandTo(node.getId());
					} catch (IllegalArgumentException ex) {
						UIUtils.showWarning("Nelze přesunou předka do potomka");
					}
				}));
	}

	private void deleteAction(NodeOverviewTO node) {
		UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat kategorii '" + node.getName() + "' ?", e -> {
			if (!nodeFacade.isNodeEmpty(node.getId())) {
				UIUtils.showWarning("Kategorie musí být prázdná");
			} else {
				try {
					nodeFacade.deleteNode(node.getId());
					grid.getTreeData().removeItem(node);
					grid.getDataProvider().refreshAll();
					if (node.getParentId() != null)
						expandTo(node.getParentId());
				} catch (Exception ex) {
					UIUtils.showWarning("Nezdařilo se smazat vybranou kategorii");
				}
			}
		}));
	}

	private void renameAction(NodeOverviewTO node) {
		final Window subwindow = new WebWindow("Přejmenovat");
		subwindow.center();
		UI.getCurrent().addWindow(subwindow);

		GridLayout subWindowlayout = new GridLayout(2, 2);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(node.getName());
		subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

		Button confirm = new Button("Přejmenovat", event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				nodeFacade.rename(node.getId(), newNameField.getValue());
				node.setName((String) newNameField.getValue());
				grid.getDataProvider().refreshItem(node);
				expandTo(node.getId());
			} catch (Exception e) {
				UIUtils.showWarning("Přejmenování se nezdařilo.");
			}

			subwindow.close();
		});
		subWindowlayout.addComponent(confirm, 0, 1);
		subWindowlayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		Button close = new Button("Storno", event -> subwindow.close());
		subWindowlayout.addComponent(close, 1, 1);
		subWindowlayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

		// Zaměř se na nové okno
		subwindow.focus();
	}

	public void createNodeAction(NodeOverviewTO parentNode) {
		final Window subwindow = new WebWindow(parentNode == null ? "Vytvořit novou kořenovou kategorii"
				: "Vytvořit novou kategorii do '" + parentNode.getName() + "'");
		subwindow.center();
		UI.getCurrent().addWindow(subwindow);

		GridLayout subWindowlayout = new GridLayout(2, 2);
		subwindow.setContent(subWindowlayout);
		subWindowlayout.setMargin(true);
		subWindowlayout.setSpacing(true);

		final TextField newNameField = new TextField("Nový název:");
		subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

		Button confirm = new Button("Vytvořit", event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				String newNodeName = newNameField.getValue();
				Long parentNodeId = parentNode == null ? null : parentNode.getId();
				Long newNodeId = nodeFacade.createNewNode(parentNodeId, newNodeName);
				NodeOverviewTO newNode = new NodeOverviewTO();
				newNode.setId(newNodeId);
				newNode.setName(newNodeName);
				newNode.setParentId(parentNodeId);
				cache.put(newNode.getId(), newNode);
				grid.getTreeData().addItem(parentNode, newNode);
				grid.getDataProvider().refreshAll();
				expandTo(newNodeId);
			} catch (Exception ex) {
				UIUtils.showWarning("Vytvoření se nezdařilo.");
			}

			subwindow.close();
		});
		subWindowlayout.addComponent(confirm, 0, 1);
		subWindowlayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

		Button close = new Button("Storno", event -> subwindow.close());
		subWindowlayout.addComponent(close, 1, 1);
		subWindowlayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

		// Zaměř se na nové okno
		subwindow.focus();
	}

}
