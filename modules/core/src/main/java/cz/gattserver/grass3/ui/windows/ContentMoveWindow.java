package cz.gattserver.grass3.ui.windows;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.interfaces.ContentNodeTO;
import cz.gattserver.grass3.interfaces.NodeOverviewTO;
import cz.gattserver.grass3.services.ContentNodeService;
import cz.gattserver.grass3.ui.components.NodeTree;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class ContentMoveWindow extends WebWindow {

	private static final long serialVersionUID = -2550619983411515006L;

	@Autowired
	private ContentNodeService contentNodeFacade;

	private Button moveBtn;
	private NodeTree tree;

	public ContentMoveWindow(final ContentNodeTO contentNodeDTO) {
		super("Přesunout obsah");

		VerticalLayout layout = (VerticalLayout) getContent();

		setWidth("500px");

		Panel panel = new Panel();
		layout.addComponent(panel);
		panel.setHeight("300px");

		tree = new NodeTree();
		panel.setContent(tree);
		tree.getGrid().addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));

		moveBtn = new Button("Přesunout");
		moveBtn.setEnabled(false);
		moveBtn.addClickListener(event -> {
			NodeOverviewTO nodeDTO = tree.getGrid().getSelectedItems().iterator().next();
			contentNodeFacade.moveContent(nodeDTO.getId(), contentNodeDTO.getId());
			close();
			onMove();
		});

		layout.addComponent(moveBtn);
		layout.setComponentAlignment(moveBtn, Alignment.MIDDLE_RIGHT);

		NodeOverviewTO to = contentNodeDTO.getParent();
		tree.expandTo(to.getParentId());
	}

	protected abstract void onMove();

}
