package org.myftp.gattserver.grass3.grocery.web;

import java.util.List;
import java.util.Locale;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.grocery.dto.ProductDTO;
import org.myftp.gattserver.grass3.grocery.dto.PurchaseDTO;
import org.myftp.gattserver.grass3.grocery.dto.ShopDTO;
import org.myftp.gattserver.grass3.grocery.facade.IGroceryFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.ui.util.StringToDoubleConverter;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class PurchaseCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IGroceryFacade groceryFacade;

	public PurchaseCreateWindow(final Component... triggerComponent) {
		this(null, triggerComponent);
	}

	public PurchaseCreateWindow(PurchaseDTO modifiedPurchaseDTO, final Component... triggerComponent) {
		super(modifiedPurchaseDTO == null ? "Přidání nákupu" : "Úprava nákupu", triggerComponent);

		groceryFacade = SpringContextHelper.getBean(IGroceryFacade.class);

		GridLayout winLayout = new GridLayout(3, 5);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		// winLayout.setWidth("300px");

		final PurchaseDTO PurchaseDTO = modifiedPurchaseDTO == null ? new PurchaseDTO() : modifiedPurchaseDTO;
		final BeanFieldGroup<PurchaseDTO> fieldGroup = new BeanFieldGroup<PurchaseDTO>(PurchaseDTO.class);
		fieldGroup.setItemDataSource(PurchaseDTO);

		final DateField dateField = new DateField("Datum");
		fieldGroup.bind(dateField, "date");
		dateField.setDateFormat("d. MMMMM yyyy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		winLayout.addComponent(dateField, 0, 0);
		dateField.setWidth("110px");
		// dateField.setWidth("100%");
		dateField.setImmediate(true);

		final TextField costField = new TextField("Cena za kus");
		fieldGroup.bind(costField, "cost");
		winLayout.addComponent(costField, 1, 0);
		costField.setWidth("100px");
		costField.setImmediate(true);
		costField.setConverter(new StringToDoubleConverter());

		final TextField quantityField = new TextField("Množství");
		fieldGroup.bind(quantityField, "quantity");
		winLayout.addComponent(quantityField, 2, 0);
		quantityField.setWidth("100px");
		quantityField.setImmediate(true);
		quantityField.setConverter(new StringToDoubleConverter());

		List<ShopDTO> shops = groceryFacade.getAllShops();
		BeanItemContainer<ShopDTO> shopsContainer = new BeanItemContainer<>(ShopDTO.class, shops);
		final ComboBox shopComboBox = new ComboBox("Obchod", shopsContainer);
		winLayout.addComponent(shopComboBox, 0, 1, 2, 1);
		shopComboBox.setWidth("100%");
		shopComboBox.setNullSelectionAllowed(false);
		shopComboBox.setImmediate(true);
		fieldGroup.bind(shopComboBox, "shop");

		List<ProductDTO> products = groceryFacade.getAllProducts();
		BeanItemContainer<ProductDTO> productsContainer = new BeanItemContainer<>(ProductDTO.class, products);
		final ComboBox productComboBox = new ComboBox("Produkt", productsContainer);
		winLayout.addComponent(productComboBox, 0, 2, 2, 2);
		productComboBox.setWidth("100%");
		productComboBox.setNullSelectionAllowed(false);
		productComboBox.setImmediate(true);
		fieldGroup.bind(productComboBox, "product");

		Label separator = new Label("");
		separator.setHeight("10px");
		winLayout.addComponent(separator, 0, 3);

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(modifiedPurchaseDTO == null ? "Přidat" : "Upravit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (groceryFacade.savePurchase(PurchaseDTO) == false) {
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
				}), 2, 4);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		setContent(winLayout);
	}

	protected abstract void onSuccess();

}
