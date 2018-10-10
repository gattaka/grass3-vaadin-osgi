package cz.gattserver.grass3.drinks.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.drinks.util.ImageUtils;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class DrinkWindow<T extends DrinkTO> extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	private static final Logger logger = LoggerFactory.getLogger(DrinkWindow.class);

	private MultiUpload upload;
	private VerticalLayout imageLayout;
	private Embedded image;

	public DrinkWindow() {
		this(null);
	}

	public DrinkWindow(final T originalTO) {
		super(originalTO == null ? "Založit" : "Upravit" + " nápoj");

		// Tahle šílenost je tu proto, aby se vycentrovalo okno s donahraným
		// obrázkem. Obrázek se bohužel nahrává nějak později, takže centrování
		// nebere v potaz jeho velikost a centruje okno špatně. Tím, že se
		// centrování zavolá později (až po nahrání obrázku, na který bohužel
		// nemám listener, takže fix-time delay)
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.delayed_center", arguments -> {
			DrinkWindow.this.setSizeUndefined();
			DrinkWindow.this.center();
		});

		T formTO = createNewInstance();

		Binder<T> binder = new Binder<>();
		binder.setBean(formTO);

		imageLayout = new VerticalLayout();
		addComponent(imageLayout);

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
		image = new Embedded(null, ImageIcon.BUBBLE_16_ICON.createResource());
		image.setVisible(false);

		upload = new MultiUpload("Nahrát foto", false) {
			private static final long serialVersionUID = 8620441233254076257L;

			@Override
			public void fileUploadFinished(InputStream in, String fileName, String mime, long size,
					int filesLeftInQueue) {
				try {
					// vytvoř miniaturu
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ImageUtils.resizeImageFile(fileName, in, bos, 400, 400);
					formTO.setImage(bos.toByteArray());
					placeImage(formTO);
				} catch (IOException e) {
					String err = "Nezdařilo se nahrát obrázek nápoje";
					logger.error(err, e);
					UIUtils.showError(err);
				}
			}
		};
		upload.setMaxFileSize(2000000);
		upload.setAcceptedMimeTypes(Arrays.asList("image/jpg", "image/jpeg", "image/png"));

		if (originalTO == null || originalTO.getImage() == null)
			placeUpload();
		else {
			placeImage(originalTO);
			formTO.setImage(originalTO.getImage());
		}

		HorizontalLayout btnsLayout = new HorizontalLayout();
		btnsLayout.setSizeUndefined();

		if (originalTO != null)
			btnsLayout.addComponent(new ModifyButton(event -> save(originalTO, binder)));
		else
			btnsLayout.addComponent(new CreateButton(event -> save(originalTO, binder)));

		VerticalLayout fieldsLayout = createForm(binder);
		fieldsLayout.addComponent(btnsLayout);
		HorizontalLayout mainLayout = new HorizontalLayout(imageLayout, fieldsLayout);
		addComponent(mainLayout);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(T originalTO, Binder<T> binder) {
		try {
			T writeTO = originalTO == null ? createNewInstance() : originalTO;
			binder.writeBean(writeTO);
			writeTO.setImage(binder.getBean().getImage());
			onSave(writeTO);
			close();
		} catch (ValidationException ve) {
			Notification.show(
					"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage(),
					Notification.Type.ERROR_MESSAGE);
		} catch (Exception ve) {
			UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
		}
	}

	private void placeImage(T to) {
		// https://vaadin.com/forum/thread/260778
		String name = to.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		image.setSource(new StreamResource(() -> new ByteArrayInputStream(to.getImage()), name));
		image.markAsDirty();
		image.setVisible(true);
		imageLayout.removeAllComponents();
		imageLayout.addComponent(image);
		imageLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);

		DeleteButton deleteButton = new DeleteButton(e -> {
			to.setImage(null);
			placeUpload();
		});
		imageLayout.addComponent(deleteButton);
		imageLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER);

		// viz koment v konstruktoru
		JavaScript.eval("setTimeout(function(){ cz.gattserver.grass3.delayed_center(); }, 10);");
	}

	private void placeUpload() {
		imageLayout.removeAllComponents();
		imageLayout.addComponent(upload);
		imageLayout.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);
	}

	protected abstract void onSave(T to);

	protected abstract T createNewInstance();

	protected abstract VerticalLayout createForm(Binder<T> binder);

}
