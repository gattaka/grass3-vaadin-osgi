package cz.gattserver.grass3.tabs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.Action;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.event.DataBoundTransferable;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.TreeDragMode;
import com.vaadin.v7.ui.Tree.TreeTargetDetails;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass3.facades.NodeFacade;
import cz.gattserver.grass3.model.dto.NodeDTO;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.WebWindow;

public class NodesSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	private static final Action ACTION_DELETE = new Action("Smazat");
	private static final Action ACTION_RENAME = new Action("Přejmenovat");
	private static final Action[] ACTIONS = new Action[] { ACTION_DELETE, ACTION_RENAME };

	private Tree tree;
	private ReferenceHolder<NodeDTO> selectedNode;

	private String panelCaptionPrefix;
	private String sectionRootCaption;
	private Panel panel;

	@Autowired
	private NodeFacade nodeFacade;

	public NodesSettingsTab(GrassRequest request) {
		super(request);
	}

	private enum TreePropertyID {
		NÁZEV, IKONA
	}

	@Override
	protected Component createContent() {

		tree = new Tree();
		selectedNode = new ReferenceHolder<NodeDTO>();

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
		tree.setImmediate(true);
		tree.setDragMode(TreeDragMode.NODE);
		tree.setDropHandler(new DropHandler() {

			private static final long serialVersionUID = -5607799513535550687L;

			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			public void drop(DragAndDropEvent dropEvent) {

				Transferable data = dropEvent.getTransferable();

				TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent.getTargetDetails());

				Object sourceItemId = ((DataBoundTransferable) data).getItemId();
				Object targetItemId = dropData.getItemIdOver();

				VerticalDropLocation location = dropData.getDropLocation();

				moveNode(sourceItemId, targetItemId, location);
			}

			/**
			 * viz. http://demo.vaadin.com/sampler#DragDropTreeSorting
			 */

			/**
			 * Move a node within a tree onto, above or below another node
			 * depending on the drop location.
			 * 
			 * @param sourceItemId
			 *            id of the item to move
			 * @param targetItemId
			 *            id of the item onto which the source node should be
			 *            moved
			 * @param location
			 *            VerticalDropLocation indicating where the source node
			 *            was dropped relative to the target node
			 */
			private void moveNode(final Object sourceItemId, final Object targetItemId, VerticalDropLocation location) {
				final HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();

				// Sorting goes as
				// - If dropped ON a node, we append it as a child
				// - If dropped on the TOP part of a node, we move/add it before
				// the node
				// - If dropped on the BOTTOM part of a node, we move/add it
				// after the node

				final Object parentItemId = container.getParent(targetItemId);

				final NodeDTO parent = parentItemId == null ? null : (NodeDTO) parentItemId;
				final NodeDTO source = (NodeDTO) sourceItemId;
				final NodeDTO target = (NodeDTO) targetItemId;

				switch (location) {
				case MIDDLE:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					getUI().addWindow(new ConfirmWindow(
							"Opravdu přesunout '" + source.getName() + "' do '" + target.getName() + "' ?", e -> {
								if (nodeFacade.moveNode(source, target)) {
									if (container.setParent(sourceItemId, targetItemId)
											&& container.hasChildren(targetItemId)) {
										// move first in the container
										container.moveAfterSibling(sourceItemId, null);
									}
									NodesSettingsTab.this.showInfo("Přesun kategorie proběhl úspěšně");
								} else {
									NodesSettingsTab.this
											.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
								}
							}));
					break;
				case TOP:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					getUI().addWindow(new ConfirmWindow(
							"Opravdu přesunout '" + source.getName() + "' do "
									+ (parentItemId == null ? "kořene sekce ?" : ("'" + parent.getName() + "' ?")),
							e -> {
								if (nodeFacade.moveNode(source, parent)) {
									if (container.setParent(sourceItemId, parentItemId)) {
										// reorder only the two items,
										// moving source
										// above target
										container.moveAfterSibling(sourceItemId, targetItemId);
										container.moveAfterSibling(targetItemId, sourceItemId);
									}
									NodesSettingsTab.this.showInfo("Přesun kategorie proběhl úspěšně");
								} else {
									NodesSettingsTab.this
											.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
								}
							}));
					break;
				case BOTTOM:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					getUI().addWindow(new ConfirmWindow(
							"Opravdu přesunout '" + source.getName() + "' do "
									+ (parentItemId == null ? "kořene sekce ?" : ("'" + parent.getName() + "' ?")),
							e -> {
								if (nodeFacade.moveNode(source, parent)) {
									if (container.setParent(sourceItemId, parentItemId)) {
										container.moveAfterSibling(sourceItemId, targetItemId);
									}
									NodesSettingsTab.this.showInfo("Přesun kategorie proběhl úspěšně");
								} else {
									NodesSettingsTab.this
											.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
								}
							}));
					break;
				}
			}
		});
		tree.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 191011037696709486L;

			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue() != null) {
					// If something is selected from the tree, get it's 'name'
					// and
					// insert it into the textfield
					selectedNode.setValue((NodeDTO) event.getProperty().getValue());
					panel.setCaption(panelCaptionPrefix + selectedNode.getValue().getName());
				} else {
					selectedNode.setValue(null);
					panel.setCaption(panelCaptionPrefix + sectionRootCaption);
				}
			}
		});
		tree.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = -4835306347998186964L;

			public void handleAction(Action action, Object sender, final Object target) {
				final NodeDTO node = (NodeDTO) target;
				if (action == ACTION_DELETE) {

					getUI().addWindow(new ConfirmWindow("Opravdu smazat kategorii '" + node.getName() + "' ?", e -> {
						if (nodeFacade.isEmpty(node) == false) {
							NodesSettingsTab.this.showWarning("Kategorie musí být prázdná");
						} else {
							if (nodeFacade.deleteNode(node)) {
								tree.removeItem(target);
								NodesSettingsTab.this.showInfo("Kategorie byla úspěšně smazána");
							} else {
								NodesSettingsTab.this.showWarning("Nezdařilo se smazat vybranou kategorii");
							}
						}
					}));

				} else if (action == ACTION_RENAME) {
					final Window subwindow = new WebWindow("Přejmenovat");
					subwindow.center();
					getUI().addWindow(subwindow);

					GridLayout subWindowlayout = new GridLayout(2, 2);
					subwindow.setContent(subWindowlayout);
					subWindowlayout.setMargin(true);
					subWindowlayout.setSpacing(true);

					final TextField newNameField = new TextField("Nový název:");
					newNameField.setValue(node.getName());
					newNameField.setRequired(true);
					newNameField.setRequiredError("Název kategorie nesmí být prázdný");
					subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

					Button confirm = new Button("Přejmenovat", new Button.ClickListener() {

						private static final long serialVersionUID = 8490964871266821307L;

						@SuppressWarnings("unchecked")
						public void buttonClick(ClickEvent event) {
							if (newNameField.isValid() == false)
								return;
							if (nodeFacade.rename(node, (String) newNameField.getValue())) {
								showInfo("Kategorie byla úspěšně přejmenována");
								tree.getItem(node).getItemProperty(TreePropertyID.NÁZEV)
										.setValue(newNameField.getValue());
								node.setName((String) newNameField.getValue());
							} else {
								showWarning("Přejmenování se nezdařilo.");
							}

							subwindow.close();
						}
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

			public Action[] getActions(Object target, Object sender) {
				return ACTIONS;
			}
		});

		refreshTree();

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
		newNodeNameField.setRequired(true);
		newNodeNameField.setRequiredError("Název kategorie nesmí být prázdný");
		panelLayout.addComponent(newNodeNameField);

		Button createButton = new Button("Vytvořit", new Button.ClickListener() {

			private static final long serialVersionUID = -4315617904120991885L;

			public void buttonClick(ClickEvent event) {
				if (newNodeNameField.isValid() == false)
					return;

				String newNodeName = newNodeNameField.getValue();
				Long newNodeId = nodeFacade.createNewNode(selectedNode.getValue(), newNodeName);
				if (newNodeId != null) {
					showInfo("Nový kategorie byla úspěšně vytvořena.");
					// refresh dir list
					refreshTree();
					// clean
					newNodeNameField.setValue("");
				} else {
					showWarning("Nezdařilo se vložit novou kategorii.");
				}
			}
		});
		panelLayout.addComponent(createButton);

	}

	private void refreshTree() {
		tree.setContainerDataSource(getCategoriesContainer());
		tree.setItemCaptionPropertyId(TreePropertyID.NÁZEV);
		tree.setItemIconPropertyId(TreePropertyID.IKONA);
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
			Item item = container.addItem(node);
			item.getItemProperty(TreePropertyID.NÁZEV).setValue(node.getName());
			container.setChildrenAllowed(node, true);
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
}
