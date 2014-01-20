package org.myftp.gattserver.grass3.grocery.web;

import java.util.Calendar;
import java.util.Date;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class PurchaseCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IGroceryFacade groceryFacade;

	public static enum Mode {
		ADD("Přidání nákupu"), EDIT("Úprava nákupu"), CONTINUE("Pokračování nákupu");

		String title;

		Mode(String title) {
			this.title = title;
		}
	}

	public PurchaseCreateWindow(final Component... triggerComponent) {
		this(Mode.ADD, null, triggerComponent);
	}

	public PurchaseCreateWindow(Mode mode, PurchaseDTO originPurchaseDTO, final Component... triggerComponent) {
		super(mode.title, triggerComponent);

		groceryFacade = SpringContextHelper.getBean(IGroceryFacade.class);

		GridLayout winLayout = new GridLayout(4, 2);
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		final PurchaseDTO purchaseDTO = mode == Mode.EDIT ? originPurchaseDTO : new PurchaseDTO();
		if (mode != Mode.EDIT) {
			purchaseDTO.setQuantity(1.0);
			purchaseDTO.setDate(Calendar.getInstance().getTime());
		}
		if (mode == Mode.CONTINUE) {
			purchaseDTO.setShop(originPurchaseDTO.getShop());
			purchaseDTO.setDate(originPurchaseDTO.getDate());
		}

		final BeanFieldGroup<PurchaseDTO> fieldGroup = new BeanFieldGroup<PurchaseDTO>(PurchaseDTO.class);
		fieldGroup.setItemDataSource(purchaseDTO);

		final DateField dateField = new DateField("Datum");
		fieldGroup.bind(dateField, "date");
		dateField.setDateFormat("d.M.yyyy");
		dateField.setLocale(Locale.forLanguageTag("CS"));
		winLayout.addComponent(dateField, 0, 0);
		dateField.setWidth("110px");
		dateField.setImmediate(true);

		List<ShopDTO> shops = groceryFacade.getAllShops();
		BeanItemContainer<ShopDTO> shopsContainer = new BeanItemContainer<>(ShopDTO.class, shops);
		final ComboBox shopComboBox = new ComboBox("Obchod", shopsContainer);
		winLayout.addComponent(shopComboBox, 1, 0, 2, 0);
		// shopComboBox.setWidth("100%");
		shopComboBox.setNullSelectionAllowed(false);
		shopComboBox.setImmediate(true);
		fieldGroup.bind(shopComboBox, "shop");

		List<ProductDTO> products = groceryFacade.getAllProducts();
		BeanItemContainer<ProductDTO> productsContainer = new BeanItemContainer<>(ProductDTO.class, products);
		final ComboBox productComboBox = new ComboBox("Produkt", productsContainer);
		winLayout.addComponent(productComboBox, 0, 1);
		// productComboBox.setWidth("100%");
		productComboBox.setNullSelectionAllowed(false);
		productComboBox.setImmediate(true);
		fieldGroup.bind(productComboBox, "product");

		final TextField costField = new TextField("Cena za jednotku");
		fieldGroup.bind(costField, "cost");
		winLayout.addComponent(costField, 1, 1);
		costField.setWidth("100px");
		costField.setImmediate(true);
		costField.setConverter(new StringToDoubleConverter());

		final TextField quantityField = new TextField("Množství");
		fieldGroup.bind(quantityField, "quantity");
		winLayout.addComponent(quantityField, 2, 1);
		quantityField.setWidth("100px");
		quantityField.setImmediate(true);
		quantityField.setConverter(new StringToDoubleConverter());

		Button saveBtn;
		winLayout.addComponent(saveBtn = new Button(mode == Mode.EDIT ? "Upravit" : "Přidat",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						if (save(fieldGroup))
							close();
					}
				}), 3, 0);
		winLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);

		if (mode != Mode.EDIT) {
			Button saveAndContinueBtn;
			winLayout.addComponent(saveAndContinueBtn = new Button("Přidat a pokračovat", new Button.ClickListener() {

				private static final long serialVersionUID = -8435971966889831628L;

				@Override
				public void buttonClick(ClickEvent event) {
					if (save(fieldGroup)) {
						Date date = fieldGroup.getItemDataSource().getBean().getDate();
						ShopDTO shop = fieldGroup.getItemDataSource().getBean().getShop();
						PurchaseDTO newDTO = new PurchaseDTO();
						newDTO.setDate(date);
						newDTO.setShop(shop);
						newDTO.setQuantity(1.0);
						fieldGroup.setItemDataSource(newDTO);
						productComboBox.focus();
					}
				}
			}), 3, 1);
			winLayout.setComponentAlignment(saveAndContinueBtn, Alignment.BOTTOM_RIGHT);
		}

		setContent(winLayout);
	}

	private boolean save(BeanFieldGroup<PurchaseDTO> fieldGroup) {
		try {
			fieldGroup.commit();
			if (groceryFacade.savePurchase(fieldGroup.getItemDataSource().getBean()) == false) {
				UI.getCurrent().addWindow(new ErrorSubwindow("Nezdařilo se vytvořit nový záznam"));
			} else {
				onSuccess();
			}
			return true;
		} catch (CommitException e) {
			Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
					Notification.Type.TRAY_NOTIFICATION);
			return false;
		}
	}

	protected abstract void onSuccess();

}
