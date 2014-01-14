package org.myftp.gattserver.grass3.grocery.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.grocery.facade.IGroceryFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class ProductCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IGroceryFacade groceryFacade;

	public ProductCreateWindow(final Component... triggerComponent) {
		this(null, triggerComponent);
	}

	public ProductCreateWindow(ProductDTO modifiedProductDTO, final Component... triggerComponent) {
		super(modifiedProductDTO == null ? "Přidání produktu" : "Úprava produktu", triggerComponent);

		groceryFacade = SpringContextHelper.getBean(IGroceryFacade.class);

		GridLayout winLayout = new GridLayout(2, 3);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		winLayout.setWidth("300px");

		final ProductDTO productDTO = modifiedProductDTO == null ? new ProductDTO() : modifiedProductDTO;
		final BeanFieldGroup<ProductDTO> fieldGroup = new BeanFieldGroup<ProductDTO>(ProductDTO.class);
		fieldGroup.setItemDataSource(productDTO);

		final TextField nameField = new TextField("Název");
		winLayout.addComponent(nameField, 0, 0, 1, 0);
		nameField.setWidth("100%");
		nameField.setImmediate(true);
		fieldGroup.bind(nameField, "name");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 1);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(modifiedProductDTO == null ? "Přidat" : "Upravit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (groceryFacade.saveProduct(productDTO) == false) {
								UI.getCurrent().addWindow(new ErrorSubwindow("Nezdařilo se vytvořit nový záznam"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
									Notification.Type.TRAY_NOTIFICATION);
						}
					}
				}), 1, 2);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);
		
		addFocusListener(new FieldEvents.FocusListener() {
			private static final long serialVersionUID = -5983342141625196725L;

			@Override
			public void focus(FocusEvent event) {
				nameField.focus();
			}
		});
	}

	protected abstract void onSuccess();

}
