package org.myftp.gattserver.grass3.windows;

import org.myftp.gattserver.grass3.facades.QuotesFacade;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.windows.template.OneColumnWindow;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class QuotesWindow extends OneColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	public static final String NAME = "quotes";

	public QuotesWindow() {
		setName(NAME);
		setCaption("Gattserver - Hlášky");
	}

	@Override
	protected void createContent(HorizontalLayout layout) {

		layout.setMargin(true);
		
		Table table = new Table();
		table.setSizeFull();
		
		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("ID", Long.class, 1L);
		container.addContainerProperty("Obsah", String.class, "");
		
        table.setContainerDataSource(container);
        layout.addComponent(table);

		for (QuoteDTO quoteDTO : QuotesFacade.getInstance().findAllQuotes()) {
			
			Item item = table.addItem(quoteDTO);
			item.getItemProperty("ID").setValue(quoteDTO.getId());
			item.getItemProperty("Obsah").setValue(quoteDTO.getName());
			
		}

	}
}
