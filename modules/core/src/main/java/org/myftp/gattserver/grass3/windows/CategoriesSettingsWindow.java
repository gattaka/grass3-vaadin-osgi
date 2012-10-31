package org.myftp.gattserver.grass3.windows;

import java.util.List;

import org.myftp.gattserver.grass3.facades.NodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;

public class CategoriesSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private static final Action ACTION_DELETE = new Action("Delete");
	private static final Action[] ACTIONS = new Action[] { ACTION_DELETE };

	private NodeFacade nodeFacade = NodeFacade.INSTANCE;

	private final Tree tree = new Tree();
	private final ReferenceHolder<NodeDTO> selectedNode = new ReferenceHolder<NodeDTO>();

	private final String panelCaptionPrefix = "Vložit novou kategorii do: ";
	private Panel panel = new Panel();

	private enum TreePropertyID {
		NÁZEV, IKONA
	}

	public CategoriesSettingsWindow() {
		setName("categories-settings");
		setCaption("Gattserver");
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new Label("<h2>Správa kategorií</h2>",
				Label.CONTENT_XHTML));

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

				TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent
						.getTargetDetails());

				Object sourceItemId = ((DataBoundTransferable) data)
						.getItemId();
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
			private void moveNode(final Object sourceItemId,
					final Object targetItemId, VerticalDropLocation location) {
				final HierarchicalContainer container = (HierarchicalContainer) tree
						.getContainerDataSource();

				// Sorting goes as
				// - If dropped ON a node, we append it as a child
				// - If dropped on the TOP part of a node, we move/add it before
				// the node
				// - If dropped on the BOTTOM part of a node, we move/add it
				// after the node

				final Object parentItemId = container.getParent(targetItemId);

				final NodeDTO parent = parentItemId == null ? null
						: (NodeDTO) parentItemId;
				final NodeDTO source = (NodeDTO) sourceItemId;
				final NodeDTO target = (NodeDTO) targetItemId;

				switch (location) {
				case MIDDLE:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					addWindow(new ConfirmSubwindow("Opravdu přesunout '"
							+ source.getName() + "' do '" + target.getName()
							+ "' ?") {

						private static final long serialVersionUID = 414272650677665672L;

						@Override
						protected void onConfirm(ClickEvent event) {
							if (nodeFacade.moveNode(source, target)) {
								if (container.setParent(sourceItemId,
										targetItemId)
										&& container.hasChildren(targetItemId)) {
									// move first in the container
									container.moveAfterSibling(sourceItemId,
											null);
								}
								CategoriesSettingsWindow.this
										.showInfo("Přesun kategorie proběhl úspěšně");
							} else {
								CategoriesSettingsWindow.this
										.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
							}
						}
					});

					break;
				case TOP:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					addWindow(new ConfirmSubwindow("Opravdu přesunout '"
							+ source.getName()
							+ "' do "
							+ (parentItemId == null ? "kořene sekce ?" : ("'"
									+ parent.getName() + "' ?"))) {

						private static final long serialVersionUID = 414272650677665672L;

						@Override
						protected void onConfirm(ClickEvent event) {
							if (nodeFacade.moveNode(source, parent)) {
								if (container.setParent(sourceItemId,
										parentItemId)) {
									// reorder only the two items, moving source
									// above target
									container.moveAfterSibling(sourceItemId,
											targetItemId);
									container.moveAfterSibling(targetItemId,
											sourceItemId);
								}
								CategoriesSettingsWindow.this
										.showInfo("Přesun kategorie proběhl úspěšně");
							} else {
								CategoriesSettingsWindow.this
										.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
							}
						}
					});

					break;
				case BOTTOM:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno

					addWindow(new ConfirmSubwindow("Opravdu přesunout '"
							+ source.getName()
							+ "' do "
							+ (parentItemId == null ? "kořene sekce ?" : ("'"
									+ parent.getName() + "' ?"))) {

						private static final long serialVersionUID = 414272650677665672L;

						@Override
						protected void onConfirm(ClickEvent event) {
							if (nodeFacade.moveNode(source, parent)) {
								if (container.setParent(sourceItemId,
										parentItemId)) {
									container.moveAfterSibling(sourceItemId,
											targetItemId);
								}
								CategoriesSettingsWindow.this
										.showInfo("Přesun kategorie proběhl úspěšně");
							} else {
								CategoriesSettingsWindow.this
										.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
							}
						}
					});

					break;
				}
			}
		});
		tree.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 191011037696709486L;

			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue() != null) {
					// If something is selected from the tree, get it's 'name'
					// and
					// insert it into the textfield
					selectedNode.setValue((NodeDTO) event.getProperty()
							.getValue());
					panel.setCaption(panelCaptionPrefix
							+ selectedNode.getValue().getName());
				} else {
					selectedNode.setValue(null);
					panel.setCaption(panelCaptionPrefix + "-kořen sekce-");
				}
			}
		});
		tree.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = -4835306347998186964L;

			public void handleAction(Action action, Object sender,
					final Object target) {
				if (action == ACTION_DELETE) {
					final NodeDTO node = (NodeDTO) target;

					addWindow(new ConfirmSubwindow("Opravdu smazat kategorii '"
							+ node.getName() + "' ?") {

						private static final long serialVersionUID = 9193745051559434697L;

						@Override
						protected void onConfirm(ClickEvent event) {

							if (!node.getContentNodes().isEmpty()
									|| !node.getSubNodeIDs().isEmpty()) {
								CategoriesSettingsWindow.this
										.showWarning("Kategorie musí být prázdná");
							} else {
								if (nodeFacade.deleteNode(node)) {
									tree.removeItem(target);
									CategoriesSettingsWindow.this
											.showInfo("Kategorie byla úspěšně smazána");
								} else {
									CategoriesSettingsWindow.this
											.showWarning("Nezdařilo se smazat vybranou kategorii");
								}
							}

						}
					});

				}
			}

			public Action[] getActions(Object target, Object sender) {
				return ACTIONS;
			}
		});

		createNewNodePanel(layout);

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

		final TextField newNodeName = new TextField();
		newNodeName.setWidth("200px");
		newNodeName.addValidator(new NullValidator(
				"Název kategorie nesmí být prázdný", false));
		panelLayout.addComponent(newNodeName);

		Button createButton = new Button("Vytvořit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -4315617904120991885L;

					public void buttonClick(ClickEvent event) {
						if (newNodeName.isValid() == false)
							return;

						if (nodeFacade.createNewNode(selectedNode.getValue(),
								newNodeName.getValue().toString())) {
							showInfo("Nový kategorie byla úspěšně vytvořena.");
							// refresh dir list
							refreshTree();
							// clean
							newNodeName.setValue("");
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

	@Override
	protected void onShow() {
		refreshTree();
		super.onShow();
	}

	private HierarchicalContainer getCategoriesContainer() {

		// Create new container
		HierarchicalContainer container = new HierarchicalContainer();
		// Create containerproperty for name
		container
				.addContainerProperty(TreePropertyID.NÁZEV, String.class, null);
		// Create containerproperty for icon
		container.addContainerProperty(TreePropertyID.IKONA,
				ThemeResource.class, new ThemeResource(
						"../runo/icons/16/folder.png"));

		List<NodeDTO> rootNodes = nodeFacade.getRootNodes();
		populateContainer(container, rootNodes, null);

		return container;
	}

	private void populateContainer(HierarchicalContainer container,
			List<NodeDTO> nodes, NodeDTO parent) {

		for (NodeDTO node : nodes) {
			Item item = container.addItem(node);
			item.getItemProperty(TreePropertyID.NÁZEV).setValue(node.getName());
			container.setChildrenAllowed(node, true);
			if (parent != null)
				container.setParent(node, parent);

			List<NodeDTO> childrenNodes = nodeFacade.getNodesByParentNode(node);
			if (childrenNodes != null)
				populateContainer(container, childrenNodes, node);
		}
	}
}
