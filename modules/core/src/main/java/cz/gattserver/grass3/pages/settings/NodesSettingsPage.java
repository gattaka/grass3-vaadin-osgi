package cz.gattserver.grass3.pages.settings;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass3.components.NodeTree;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.NodeTreeDTO;
import cz.gattserver.grass3.server.GrassRequest;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.H2Label;

public class NodesSettingsPage extends ModuleSettingsPage {

	private NodeTree tree;
	private ReferenceHolder<NodeTreeDTO> selectedNode;

	private String panelCaptionPrefix;
	private String sectionRootCaption;
	private Panel panel;

	@Autowired
	private NodeFacade nodeFacade;

	public NodesSettingsPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		tree = new NodeTree();
		selectedNode = new ReferenceHolder<NodeTreeDTO>();

		panelCaptionPrefix = "Vložit novou kategorii do: ";
		sectionRootCaption = "-kořen sekce-";
		panel = new Panel(panelCaptionPrefix + sectionRootCaption);

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new H2Label("Správa kategorií"));
		usersLayout.addComponent(tree);

		// /**
		// * viz. http://demo.vaadin.com/sampler#DragDropTreeSorting
		// */
		//
		// /**
		// * Move a node within a tree onto, above or below another node
		// * depending on the drop location.
		// *
		// * @param sourceItemId
		// * id of the item to move
		// * @param targetItemId
		// * id of the item onto which the source node should be
		// * moved
		// * @param location
		// * VerticalDropLocation indicating where the source node
		// * was dropped relative to the target node
		// */
		// private void moveNode(final Object sourceItemId, final Object
		// targetItemId, VerticalDropLocation location) {
		// final HierarchicalContainer container = (HierarchicalContainer)
		// tree.getContainerDataSource();
		//
		// // Sorting goes as
		// // - If dropped ON a node, we append it as a child
		// // - If dropped on the TOP part of a node, we move/add it before
		// // the node
		// // - If dropped on the BOTTOM part of a node, we move/add it
		// // after the node
		//
		// final Object parentItemId = container.getParent(targetItemId);
		//
		// final NodeDTO parent = parentItemId == null ? null : (NodeDTO)
		// parentItemId;
		// final NodeDTO source = (NodeDTO) sourceItemId;
		// final NodeDTO target = (NodeDTO) targetItemId;
		//
		// switch (location) {
		// case MIDDLE:
		//
		// // Přesunutí znamená rovnou přesun kategorie - v tom případě
		// // je potřeba vyhodit potvrzovací okno
		// UI.getCurrent().addWindow(new ConfirmWindow(
		// "Opravdu přesunout '" + source.getName() + "' do '" +
		// target.getName() + "' ?", e -> {
		// if (nodeFacade.moveNode(source, target)) {
		// if (container.setParent(sourceItemId, targetItemId)
		// && container.hasChildren(targetItemId)) {
		// // move first in the container
		// container.moveAfterSibling(sourceItemId, null);
		// }
		// NodesSettingsPage.this.showInfo("Přesun kategorie proběhl úspěšně");
		// } else {
		// NodesSettingsPage.this
		// .showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
		// }
		// }));
		// break;
		// case TOP:
		//
		// // Přesunutí znamená rovnou přesun kategorie - v tom případě
		// // je potřeba vyhodit potvrzovací okno
		// UI.getCurrent()
		// .addWindow(new ConfirmWindow("Opravdu přesunout '" + source.getName()
		// + "' do "
		// + (parentItemId == null ? "kořene sekce ?" : ("'" + parent.getName()
		// + "' ?")),
		// e -> {
		// if (nodeFacade.moveNode(source, parent)) {
		// if (container.setParent(sourceItemId, parentItemId)) {
		// // reorder only the two items,
		// // moving source
		// // above target
		// container.moveAfterSibling(sourceItemId, targetItemId);
		// container.moveAfterSibling(targetItemId, sourceItemId);
		// }
		// NodesSettingsPage.this.showInfo("Přesun kategorie proběhl úspěšně");
		// } else {
		// NodesSettingsPage.this
		// .showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
		// }
		// }));
		// break;
		// case BOTTOM:
		//
		// // Přesunutí znamená rovnou přesun kategorie - v tom případě
		// // je potřeba vyhodit potvrzovací okno
		// UI.getCurrent()
		// .addWindow(new ConfirmWindow("Opravdu přesunout '" + source.getName()
		// + "' do "
		// + (parentItemId == null ? "kořene sekce ?" : ("'" + parent.getName()
		// + "' ?")),
		// e -> {
		// if (nodeFacade.moveNode(source, parent)) {
		// if (container.setParent(sourceItemId, parentItemId)) {
		// container.moveAfterSibling(sourceItemId, targetItemId);
		// }
		// NodesSettingsPage.this.showInfo("Přesun kategorie proběhl úspěšně");
		// } else {
		// NodesSettingsPage.this
		// .showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
		// }
		// }));
		// break;
		// }
		// }
		// });
		tree.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedNode.setValue(event.getFirstSelectedItem().get());
				panel.setCaption(panelCaptionPrefix + selectedNode.getValue().getName());
			} else {
				selectedNode.setValue(null);
				panel.setCaption(panelCaptionPrefix + sectionRootCaption);
			}
		});

		createNewNodePanel(layout);

		return layout;
	}

	private void createNewNodePanel(VerticalLayout layout) {
		layout.addComponent(panel);

		HorizontalLayout panelBackgroudLayout = new HorizontalLayout();
		panelBackgroudLayout.setSizeFull();
		panel.setContent(panelBackgroudLayout);

		HorizontalLayout panelLayout = new HorizontalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setMargin(true);
		panelBackgroudLayout.addComponent(panelLayout);

		final TextField newNodeNameField = new TextField();
		newNodeNameField.setWidth("200px");
		panelLayout.addComponent(newNodeNameField);

		Button createButton = new Button("Vytvořit", event -> {
			if (StringUtils.isBlank(newNodeNameField.getValue())) {
				UIUtils.showError("Název kategorie nesmí být prázdný");
				return;
			}

			String newNodeName = newNodeNameField.getValue();
			Long parentNodeId = selectedNode.getValue() == null ? null : selectedNode.getValue().getId();
			Long newNodeId = nodeFacade.createNewNode(parentNodeId, newNodeName);
			if (newNodeId != null) {
				UIUtils.showInfo("Nový kategorie byla úspěšně vytvořena.");
				NodeTreeDTO newNode = new NodeTreeDTO();
				newNode.setId(newNodeId);
				newNode.setName(newNodeName);
				newNode.setParentId(parentNodeId);
				tree.addNode(newNode);
				// clean
				newNodeNameField.setValue("");
			} else {
				UIUtils.showWarning("Nezdařilo se vložit novou kategorii.");
			}
		});
		panelLayout.addComponent(createButton);
	}

}
