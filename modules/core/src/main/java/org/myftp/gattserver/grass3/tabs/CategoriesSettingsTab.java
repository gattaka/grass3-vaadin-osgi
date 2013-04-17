package org.myftp.gattserver.grass3.tabs;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.tabs.template.AbstractSettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.ReferenceHolder;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@org.springframework.stereotype.Component("categoriesSettingsTab")
@Scope("prototype")
public class CategoriesSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;
	
	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	public CategoriesSettingsTab(GrassRequest request) {
		super(request);
	}
	
	private static final Action ACTION_DELETE = new Action("Smazat");
	private static final Action ACTION_RENAME = new Action("Přejmenovat");
	private static final Action[] ACTIONS = new Action[] { ACTION_DELETE,
			ACTION_RENAME };

	private final Tree tree = new Tree();
	private final ReferenceHolder<NodeDTO> selectedNode = new ReferenceHolder<NodeDTO>();

	private final String panelCaptionPrefix = "Vložit novou kategorii do: ";
	private final String sectionRootCaption = "-kořen sekce-";
	private Panel panel = new Panel(panelCaptionPrefix + sectionRootCaption);

	private enum TreePropertyID {
		NÁZEV, IKONA
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout usersLayout = new VerticalLayout();
		layout.addComponent(usersLayout);

		usersLayout.addComponent(new Label("<h2>Správa kategorií</h2>",
				ContentMode.HTML));

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
					getUI().addWindow(
							new ConfirmSubwindow("Opravdu přesunout '"
									+ source.getName() + "' do '"
									+ target.getName() + "' ?") {

								private static final long serialVersionUID = 414272650677665672L;

								@Override
								protected void onConfirm(ClickEvent event) {
									if (nodeFacade.moveNode(source, target)) {
										if (container.setParent(sourceItemId,
												targetItemId)
												&& container
														.hasChildren(targetItemId)) {
											// move first in the container
											container.moveAfterSibling(
													sourceItemId, null);
										}
										CategoriesSettingsTab.this
												.showInfo("Přesun kategorie proběhl úspěšně");
									} else {
										CategoriesSettingsTab.this
												.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
									}
								}
							});

					break;
				case TOP:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno
					getUI().addWindow(
							new ConfirmSubwindow("Opravdu přesunout '"
									+ source.getName()
									+ "' do "
									+ (parentItemId == null ? "kořene sekce ?"
											: ("'" + parent.getName() + "' ?"))) {

								private static final long serialVersionUID = 414272650677665672L;

								@Override
								protected void onConfirm(ClickEvent event) {
									if (nodeFacade.moveNode(source, parent)) {
										if (container.setParent(sourceItemId,
												parentItemId)) {
											// reorder only the two items,
											// moving source
											// above target
											container.moveAfterSibling(
													sourceItemId, targetItemId);
											container.moveAfterSibling(
													targetItemId, sourceItemId);
										}
										CategoriesSettingsTab.this
												.showInfo("Přesun kategorie proběhl úspěšně");
									} else {
										CategoriesSettingsTab.this
												.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
									}
								}
							});

					break;
				case BOTTOM:

					// Přesunutí znamená rovnou přesun kategorie - v tom případě
					// je potřeba vyhodit potvrzovací okno

					getUI().addWindow(
							new ConfirmSubwindow("Opravdu přesunout '"
									+ source.getName()
									+ "' do "
									+ (parentItemId == null ? "kořene sekce ?"
											: ("'" + parent.getName() + "' ?"))) {

								private static final long serialVersionUID = 414272650677665672L;

								@Override
								protected void onConfirm(ClickEvent event) {
									if (nodeFacade.moveNode(source, parent)) {
										if (container.setParent(sourceItemId,
												parentItemId)) {
											container.moveAfterSibling(
													sourceItemId, targetItemId);
										}
										CategoriesSettingsTab.this
												.showInfo("Přesun kategorie proběhl úspěšně");
									} else {
										CategoriesSettingsTab.this
												.showWarning("Nezdařilo se přesunout kategorii do vybraného místa");
									}
								}
							});

					break;
				}
			}
		});
		tree.addValueChangeListener(new Property.ValueChangeListener() {

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
					panel.setCaption(panelCaptionPrefix + sectionRootCaption);
				}
			}
		});
		tree.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = -4835306347998186964L;

			public void handleAction(Action action, Object sender,
					final Object target) {
				final NodeDTO node = (NodeDTO) target;
				if (action == ACTION_DELETE) {

					getUI().addWindow(
							new ConfirmSubwindow("Opravdu smazat kategorii '"
									+ node.getName() + "' ?") {

								private static final long serialVersionUID = 9193745051559434697L;

								@Override
								protected void onConfirm(ClickEvent event) {

									if (!node.getContentNodes().isEmpty()
											|| !node.getSubNodes().isEmpty()) {
										CategoriesSettingsTab.this
												.showWarning("Kategorie musí být prázdná");
									} else {
										if (nodeFacade.deleteNode(node)) {
											tree.removeItem(target);
											CategoriesSettingsTab.this
													.showInfo("Kategorie byla úspěšně smazána");
										} else {
											CategoriesSettingsTab.this
													.showWarning("Nezdařilo se smazat vybranou kategorii");
										}
									}

								}
							});

				} else if (action == ACTION_RENAME) {
					final Window subwindow = new GrassSubWindow("Přejmenovat");
					subwindow.center();
					getUI().addWindow(subwindow);

					GridLayout subWindowlayout = new GridLayout(2, 2);
					subwindow.setContent(subWindowlayout);
					subWindowlayout.setMargin(true);
					subWindowlayout.setSpacing(true);

					final TextField newNameField = new TextField("Nový název:");
					newNameField.setValue(node.getName());
					newNameField.setRequired(true);
					newNameField
							.setRequiredError("Název kategorie nesmí být prázdný");
					subWindowlayout.addComponent(newNameField, 0, 0, 1, 0);

					Button confirm = new Button("Přejmenovat",
							new Button.ClickListener() {

								private static final long serialVersionUID = 8490964871266821307L;

								public void buttonClick(ClickEvent event) {
									if (newNameField.isValid() == false)
										return;
									if (nodeFacade.rename(node,
											(String) newNameField.getValue())) {
										showInfo("Kategorie byla úspěšně přejmenována");
										tree.getItem(node)
												.getItemProperty(
														TreePropertyID.NÁZEV)
												.setValue(
														newNameField.getValue());
										node.setName((String) newNameField
												.getValue());
									} else {
										showWarning("Přejmenování se nezdařilo.");
									}

									subwindow.close();
								}
							});

					subWindowlayout.addComponent(confirm, 0, 1);
					subWindowlayout.setComponentAlignment(confirm,
							Alignment.MIDDLE_CENTER);

					Button close = new Button("Storno",
							new Button.ClickListener() {

								private static final long serialVersionUID = 8490964871266821307L;

								public void buttonClick(ClickEvent event) {
									subwindow.close();
								}
							});

					subWindowlayout.addComponent(close, 1, 1);
					subWindowlayout.setComponentAlignment(close,
							Alignment.MIDDLE_CENTER);

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

		final TextField newNodeName = new TextField();
		newNodeName.setWidth("200px");
		newNodeName.setRequired(true);
		newNodeName.setRequiredError("Název kategorie nesmí být prázdný");
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
