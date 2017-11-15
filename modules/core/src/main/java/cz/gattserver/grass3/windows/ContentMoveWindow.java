package cz.gattserver.grass3.windows;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.components.NodeTree;
import cz.gattserver.grass3.facades.ContentNodeFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.NodeOverviewDTO;
import cz.gattserver.web.common.window.WebWindow;

public abstract class ContentMoveWindow extends WebWindow {

	private static final long serialVersionUID = -2550619983411515006L;

	@Autowired
	private ContentNodeFacade contentNodeFacade;

	private Button moveBtn;
	private NodeTree tree;

	public ContentMoveWindow(final ContentNodeDTO contentNodeDTO) {
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
			NodeOverviewDTO nodeDTO = tree.getGrid().getSelectedItems().iterator().next();
			contentNodeFacade.moveContent(nodeDTO.getId(), contentNodeDTO.getId());
			close();
			onMove();
		});

		layout.addComponent(moveBtn);
		layout.setComponentAlignment(moveBtn, Alignment.MIDDLE_RIGHT);

		NodeOverviewDTO to = contentNodeDTO.getParent();
		tree.expandTo(to.getParentId());
	}

	protected abstract void onMove();

}
