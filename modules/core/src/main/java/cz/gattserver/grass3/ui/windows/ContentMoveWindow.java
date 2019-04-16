package cz.gattserver.grass3.ui.windows;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;

import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.ui.components.NodeTree;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ContentMoveWindow extends WebWindow {

	private static final long serialVersionUID = -2550619983411515006L;

	private Button moveBtn;
	private NodeTree tree;

	public ContentMoveWindow(final ContentNodeTO contentNodeDTO) {
		super("Přesunout obsah");

		setWidth("500px");

		tree = new NodeTree();
		tree.getGrid().addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));
		tree.setHeight("300px");
		layout.addComponent(tree);

		moveBtn = new Button("Přesunout");
		moveBtn.setEnabled(false);
		moveBtn.addClickListener(event -> {
			NodeOverviewTO nodeDTO = tree.getGrid().getSelectedItems().iterator().next();
			SpringContextHelper.getBean(ContentNodeService.class).moveContent(nodeDTO.getId(), contentNodeDTO.getId());
			close();
			onMove();
		});

		layout.addComponent(moveBtn);
		layout.setComponentAlignment(moveBtn, Alignment.MIDDLE_RIGHT);

		NodeOverviewTO to = contentNodeDTO.getParent();
		tree.expandTo(to.getId());
	}

	protected abstract void onMove();

}
