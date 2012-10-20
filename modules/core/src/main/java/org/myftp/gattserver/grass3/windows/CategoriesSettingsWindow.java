package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;

public class CategoriesSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private final Tree tree = new Tree();

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
			private void moveNode(Object sourceItemId, Object targetItemId,
					VerticalDropLocation location) {
				HierarchicalContainer container = (HierarchicalContainer) tree
						.getContainerDataSource();

				// Sorting goes as
				// - If dropped ON a node, we append it as a child
				// - If dropped on the TOP part of a node, we move/add it before
				// the node
				// - If dropped on the BOTTOM part of a node, we move/add it
				// after the node

				if (location == VerticalDropLocation.MIDDLE) {
					if (container.setParent(sourceItemId, targetItemId)
							&& container.hasChildren(targetItemId)) {
						// move first in the container
						container.moveAfterSibling(sourceItemId, null);
					}
				} else if (location == VerticalDropLocation.TOP) {
					Object parentId = container.getParent(targetItemId);
					if (container.setParent(sourceItemId, parentId)) {
						// reorder only the two items, moving source above
						// target
						container.moveAfterSibling(sourceItemId, targetItemId);
						container.moveAfterSibling(targetItemId, sourceItemId);
					}
				} else if (location == VerticalDropLocation.BOTTOM) {
					Object parentId = container.getParent(targetItemId);
					if (container.setParent(sourceItemId, parentId)) {
						container.moveAfterSibling(sourceItemId, targetItemId);
					}
				}
			}
		});

	}

	@Override
	protected void onShow() {
		tree.setContainerDataSource(getCategoriesContainer());
		super.onShow();
	}

	private enum TreePropertyID {
		NÁZEV, IKONA
	}
	
	private HierarchicalContainer getCategoriesContainer() {
		Item item = null;
		int itemId = 0; // Increasing numbering for itemId:s

		// Create new container
		HierarchicalContainer hwContainer = new HierarchicalContainer();
		// Create containerproperty for name
		hwContainer.addContainerProperty(TreePropertyID.NÁZEV, String.class, null);
		// Create containerproperty for icon
		hwContainer.addContainerProperty(TreePropertyID.IKONA, ThemeResource.class,
				new ThemeResource("../runo/icons/16/document.png"));
//		for (int i = 0; i < hardware.length; i++) {
//			// Add new item
//			item = hwContainer.addItem(itemId);
//			// Add name property for item
//			item.getItemProperty(TreePropertyID.NÁZEV).setValue(hardware[i][0]);
//			// Allow children
//			hwContainer.setChildrenAllowed(itemId, true);
//			itemId++;
//			for (int j = 1; j < hardware[i].length; j++) {
//				if (j == 1) {
//					item.getItemProperty(TreePropertyID.IKONA).setValue(
//							new ThemeResource("../runo/icons/16/folder.png"));
//				}
//				// Add child items
//				item = hwContainer.addItem(itemId);
//				item.getItemProperty(TreePropertyID.NÁZEV).setValue(hardware[i][j]);
//				hwContainer.setParent(itemId, itemId - j);
//				hwContainer.setChildrenAllowed(itemId, false);
//
//				itemId++;
//			}
//		}
		return hwContainer;
	}

}
