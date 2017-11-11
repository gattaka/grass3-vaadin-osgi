package cz.gattserver.grass3.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.WebWindow;

public class NodeTree extends Tree<NodeTreeDTO> implements Handler {

	private static final long serialVersionUID = -7457362355620092284L;

	private static final Action ACTION_DELETE = new Action("Smazat");
	private static final Action ACTION_RENAME = new Action("Přejmenovat");
	private static final Action[] ACTIONS = new Action[] { ACTION_DELETE, ACTION_RENAME };

	private Map<Long, NodeTreeDTO> cache;
	private Set<Long> visited;

	@Autowired
	private NodeFacade nodeFacade;

	public NodeTree() {
		SpringContextHelper.inject(this);
		cache = new HashMap<>();
		visited = new HashSet<>();
		setSelectionMode(SelectionMode.SINGLE);
		setItemIconGenerator(i -> new ThemeResource(ImageIcons.FOLDER_16_ICON));
		setItemCaptionGenerator(NodeTreeDTO::getName);
		populate();
	}

	public void populate() {
		List<NodeTreeDTO> nodes = nodeFacade.getNodesForTree();
		TreeData<NodeTreeDTO> treeData = new TreeData<>();
		nodes.forEach(n -> cache.put(n.getId(), n));
		nodes.forEach(n -> addTreeItem(treeData, n));
		setDataProvider(new TreeDataProvider<>(treeData));
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

	public void expandTo(Long id) {
		NodeTreeDTO to = cache.get(id);
		Long parent = to.getParentId();
		while (parent != null) {
			NodeTreeDTO n = cache.get(parent);
			expand(n);
			parent = n.getParentId();
		}
		select(cache.get(to.getId()));
	}

	@Override
	public void handleAction(Action action, Object sender, final Object target) {
		final NodeTreeDTO node = (NodeTreeDTO) target;
		if (action == ACTION_DELETE) {
			UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat kategorii '" + node.getName() + "' ?", e -> {
				if (nodeFacade.isEmpty(node.getId()) == false) {
					UIUtils.showWarning("Kategorie musí být prázdná");
				} else {
					try {
						nodeFacade.deleteNode(node.getId());
						NodeTree.this.getTreeData().removeItem(node);
						UIUtils.showInfo("Kategorie byla úspěšně smazána");
					} catch (Exception ex) {
						UIUtils.showWarning("Nezdařilo se smazat vybranou kategorii");
					}
				}
			}));

		} else if (action == ACTION_RENAME) {
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
				if (nodeFacade.rename(node.getId(), newNameField.getValue())) {
					UIUtils.showInfo("Kategorie byla úspěšně přejmenována");
					node.setName((String) newNameField.getValue());
				} else {
					UIUtils.showWarning("Přejmenování se nezdařilo.");
				}

				subwindow.close();
			});

			subWindowlayout.addComponent(confirm, 0, 1);
			subWindowlayout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);

			Button close = new Button("Storno", new Button.ClickListener() {

				private static final long serialVersionUID = 8490964871266821307L;

				public void buttonClick(ClickEvent event) {
					subwindow.close();
				}
			});

			subWindowlayout.addComponent(close, 1, 1);
			subWindowlayout.setComponentAlignment(close, Alignment.MIDDLE_CENTER);

			// Zaměř se na nové okno
			subwindow.focus();
		}
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		return ACTIONS;
	}

	public void addNode(NodeTreeDTO newNode) {
		if (newNode.getId() == null || newNode.getName() == null)
			throw new IllegalArgumentException("NodeTreeDTO musí mít vyplněn název a id");
		NodeTreeDTO parentTO = cache.get(newNode.getParentId());
		cache.put(newNode.getId(), newNode);
		getTreeData().addItem(parentTO, newNode);
	}

}
