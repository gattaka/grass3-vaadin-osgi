package cz.gattserver.grass3.ui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.services.NodeService;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public class NodeTree extends VerticalLayout {

	private static final long serialVersionUID = -7457362355620092284L;

	private static final String PREJMENOVAT_LABEL = "Přejmenovat";

	private transient NodeService nodeFacade;

	// Serializable HashMap
	private HashMap<Long, NodeOverviewTO> cache;
	private Set<Long> visited;

	private TreeGrid<NodeOverviewTO> grid;

	// Serializable ArrayList
	private ArrayList<NodeOverviewTO> draggedItems;

	public NodeTree() {
		this(false);
	}

	private NodeService getNodeService() {
		if (nodeFacade == null)
			nodeFacade = SpringContextHelper.getBean(NodeService.class);
		return nodeFacade;
	}

	public TreeGrid<NodeOverviewTO> getGrid() {
		return grid;
	}

	public NodeTree(boolean enableEditFeatures) {

		setSpacing(true);
		setPadding(false);

		cache = new HashMap<>();
		visited = new HashSet<>();

		grid = new TreeGrid<>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		add(grid);
		expand(grid);

		grid.addHierarchyColumn(NodeOverviewTO::getName).setHeader("Název");
		populate();

		if (enableEditFeatures)
			initEditFeatures();
	}

	private void initEditFeatures() {

		// TODO
		// TreeGridDragSource<NodeOverviewTO> dragSource = new
		// TreeGridDragSource<>(grid);
		// dragSource.setEffectAllowed(EffectAllowed.MOVE);
		// dragSource.addGridDragStartListener(e -> draggedItems = new
		// ArrayList<>(e.getDraggedItems()));
		//
		// TreeGridDropTarget<NodeOverviewTO> dropTarget = new
		// TreeGridDropTarget<>(grid, DropMode.ON_TOP_OR_BETWEEN);
		// dropTarget.setDropEffect(DropEffect.MOVE);
		// dropTarget.addTreeGridDropListener(event -> {
		// NodeOverviewTO dropNode = event.getDropTargetRow().get();
		// switch (event.getDropLocation()) {
		// case ON_TOP:
		// // vkládám do dropNode
		// break;
		// case ABOVE:
		// case BELOW:
		// // vkládám do parenta dropNode
		// dropNode = dropNode.getParentId() == null ? null :
		// cache.get(dropNode.getParentId());
		// break;
		// case EMPTY:
		// default:
		// // výchozí je vkládání do root
		// dropNode = null;
		// }
		// for (NodeOverviewTO n : draggedItems)
		// moveAction(n, dropNode);
		// grid.getDataProvider().refreshAll();
		// });

		/*
		 * Context menu
		 */
		GridContextMenu<NodeOverviewTO> gridMenu = new GridContextMenu<>(grid);
		gridMenu.addGridContextMenuOpenedListener(e -> {
			gridMenu.removeAll();
			if (e.getItem().isPresent()) {
				NodeOverviewTO node = e.getItem().get();
				grid.select(node);
				// Bohužel, je zde asi bug, protože ContextMenu addon neumí
				// zpracovat ClassResource, umí evidentě pouze ThemeResource
				gridMenu.addItem("Smazat", selectedItem -> deleteAction(node));
				gridMenu.addItem(PREJMENOVAT_LABEL, selectedItem -> renameAction(node));
			}
			gridMenu.addItem("Vytvořit zde novou", selectedItem -> createNodeAction(e.getItem()));
		});

		/*
		 * Delete shortcut
		 */
		Shortcuts.addShortcutListener(this, () -> {
			if (!grid.getSelectedItems().isEmpty())
				deleteAction(grid.getSelectedItems().iterator().next());
		}, Key.DELETE);

		/*
		 * Buttons
		 */
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		add(btnLayout);

		CreateGridButton createBtn = new CreateGridButton("Vytvořit",
				e -> createNodeAction(grid.getSelectedItems().isEmpty() ? Optional.empty()
						: Optional.of(grid.getSelectedItems().iterator().next())));
		btnLayout.add(createBtn);

		ModifyGridButton<NodeOverviewTO> modifyBtn = new ModifyGridButton<>(PREJMENOVAT_LABEL, this::renameAction,
				grid);
		btnLayout.add(modifyBtn);

		// mazání chci po jednom
		DeleteGridButton<NodeOverviewTO> deleteBtn = new DeleteGridButton<>("Smazat",
				nodes -> deleteAction(nodes.iterator().next()), grid);
		deleteBtn.setEnableResolver(items -> items.size() == 1);
		btnLayout.add(deleteBtn);

	}

	public void populate() {
		List<NodeOverviewTO> nodes = getNodeService().getNodesForTree();
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

		new ConfirmDialog("Opravdu přesunout '" + node.getName() + "' do "
				+ (newParent == null ? "kořene sekce" : "'" + newParent.getName() + "'") + "?", e -> {
					try {
						getNodeService().moveNode(node.getId(), newParent == null ? null : newParent.getId());
						node.setParentId(newParent == null ? null : newParent.getId());
						grid.getTreeData().setParent(node, newParent);
						grid.getDataProvider().refreshAll();
						expandTo(node.getId());
					} catch (IllegalArgumentException ex) {
						UIUtils.showWarning("Nelze přesunou předka do potomka");
					}
				}).open();
	}

	private void deleteAction(NodeOverviewTO node) {
		new ConfirmDialog("Opravdu smazat kategorii '" + node.getName() + "' ?", e -> {
			if (!getNodeService().isNodeEmpty(node.getId())) {
				UIUtils.showWarning("Kategorie musí být prázdná");
			} else {
				try {
					getNodeService().deleteNode(node.getId());
					grid.getTreeData().removeItem(node);
					grid.getDataProvider().refreshAll();
					if (node.getParentId() != null)
						expandTo(node.getParentId());
				} catch (Exception ex) {
					UIUtils.showWarning("Nezdařilo se smazat vybranou kategorii");
				}
			}
		}).open();
	}

	private void renameAction(NodeOverviewTO node) {
		final WebDialog dialog = new WebDialog(PREJMENOVAT_LABEL);
		dialog.open();

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(node.getName());
		dialog.add(newNameField);

		HorizontalLayout btnLayout = new HorizontalLayout();
		dialog.addComponent(btnLayout);

		Button confirmBtn = new Button(PREJMENOVAT_LABEL, event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				getNodeService().rename(node.getId(), newNameField.getValue());
				node.setName((String) newNameField.getValue());
				grid.getDataProvider().refreshItem(node);
				expandTo(node.getId());
			} catch (Exception e) {
				UIUtils.showWarning("Přejmenování se nezdařilo.");
			}

			dialog.close();
		});
		btnLayout.add(confirmBtn);

		Button closeBtn = new Button("Storno", event -> dialog.close());
		btnLayout.add(closeBtn);
	}

	public void createNodeAction(Optional<NodeOverviewTO> parentNode) {
		final WebDialog dialog = new WebDialog(
				parentNode.isPresent() ? "Vytvořit novou kategorii do '" + parentNode.get().getName() + "'"
						: "Vytvořit novou kořenovou kategorii");
		dialog.open();

		final TextField newNameField = new TextField("Nový název:");
		dialog.addComponent(newNameField);

		HorizontalLayout btnLayout = new HorizontalLayout();
		dialog.addComponent(btnLayout);

		Button confirmBtn = new Button("Vytvořit", event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				String newNodeName = newNameField.getValue();
				Long parentNodeId = parentNode.isPresent() ? parentNode.get().getId() : null;
				Long newNodeId = getNodeService().createNewNode(parentNodeId, newNodeName);
				NodeOverviewTO newNode = new NodeOverviewTO();
				newNode.setId(newNodeId);
				newNode.setName(newNodeName);
				newNode.setParentId(parentNodeId);
				cache.put(newNode.getId(), newNode);
				grid.getTreeData().addItem(parentNode.orElse(null), newNode);
				grid.getDataProvider().refreshAll();
				expandTo(newNodeId);
			} catch (Exception ex) {
				UIUtils.showWarning("Vytvoření se nezdařilo.");
			}

			dialog.close();
		});
		btnLayout.add(confirmBtn);

		Button closeBtn = new Button("Storno", event -> dialog.close());
		btnLayout.add(closeBtn);
	}

}
