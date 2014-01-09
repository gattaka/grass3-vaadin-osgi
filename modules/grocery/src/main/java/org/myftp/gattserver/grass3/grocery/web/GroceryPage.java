package org.myftp.gattserver.grass3.grocery.web;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.grocery.facade.IGroceryFacade;
import org.myftp.gattserver.grass3.pages.template.OneColumnPage;
import org.myftp.gattserver.grass3.template.ISelectable;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

@org.springframework.stereotype.Component("groceryPage")
@Scope("prototype")
public class GroceryPage extends OneColumnPage {

	private static final long serialVersionUID = -950042653154868289L;

	@Resource(name = "groceryFacade")
	private IGroceryFacade groceryFacade;

	public GroceryPage(GrassRequest request) {
		super(request);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected Component createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);

		tabSheet.addTab(new ShopsTab(), "Obchody");
		tabSheet.addTab(new ProductTab(), "Produkty");
		tabSheet.addTab(new PurchaseTab(), "Nákupy");

		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {

			private static final long serialVersionUID = 6943259268778916110L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = (TabSheet) event.getComponent();
				Component component = tabSheet.getSelectedTab();
				if (component != null && component instanceof ISelectable) {
					((ISelectable) component).select();
				}
			}
		});

		return layout;
	}

}
