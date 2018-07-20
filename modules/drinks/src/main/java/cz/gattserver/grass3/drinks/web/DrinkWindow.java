package cz.gattserver.grass3.drinks.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.drinks.model.domain.DrinkType;
import cz.gattserver.grass3.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;
import elemental.json.JsonArray;

public abstract class DrinkWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	private static final Logger logger = LoggerFactory.getLogger(DrinkWindow.class);

	private MultiUpload upload;
	private VerticalLayout imageLayout;
	private Embedded image;

	public DrinkWindow() {
		this(null);
	}

	protected abstract void onSave(DrinkTO to);

	private void placeImage(DrinkTO to) {
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

	public DrinkWindow(final DrinkTO originalTO) {
		super(originalTO == null ? "Založit" : "Upravit" + " nápoj");

		setWidth("600px");

		// Tahle šílenost je tu proto, aby se vycentrovalo okno s donahraným
		// obrázkem. Obrázek se bohužel nahrává nějak později, takže centrování
		// nebere v potaz jeho velikost a centruje okno špatně. Tím, že se
		// centrování zavolá později (až po nahrání obrázku, na který bohužel
		// nemám listener, takže fix-time delay)
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.delayed_center", (arguments) -> {
			DrinkWindow.this.setSizeUndefined();
			DrinkWindow.this.center();
		});

		final DrinkTO formTO = new DrinkTO();

		Binder<DrinkTO> binder = new Binder<>(DrinkTO.class);
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
					formTO.setImage(IOUtils.toByteArray(in));
					placeImage(formTO);
				} catch (IOException e) {
					String err = "Nezdařilo se nahrát obrázek nápoje";
					logger.error(err, e);
					UIUtils.showError(err);
				}
			}
		};
		upload.setMaxFileSize(2000000);
		upload.setAcceptedMimeTypes(Arrays.asList(new String[] { "image/jpg", "image/jpeg", "image/png" }));

		if (originalTO == null || originalTO.getImage() == null)
			placeUpload();
		else
			placeImage(originalTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(DrinkTO::getName, DrinkTO::setName);
		nameField.setWidth("100%");

		final ComboBox<DrinkType> typeField = new ComboBox<>("Typ", Arrays.asList(DrinkType.values()));
		typeField.setItemCaptionGenerator(DrinkType::getCaption);
		binder.forField(typeField).asRequired().bind(DrinkTO::getType, DrinkTO::setTyp);
		typeField.setWidth("100%");

		final RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(DrinkTO::getRating, DrinkTO::setRating);
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout infoLayout = new HorizontalLayout(nameField, typeField, ratingStars);
		infoLayout.setSizeFull();
		addComponent(infoLayout);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(DrinkTO::getDescription, DrinkTO::setDescription);
		descriptionField.setWidth("100%");
		descriptionField.setHeight("100px");
		addComponent(descriptionField);

		Button b;
		if (originalTO != null)
			addComponent(b = new ModifyButton(event -> save(originalTO, binder)));
		else
			addComponent(b = new CreateButton(event -> save(originalTO, binder)));
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);

		addComponent(b = new ModifyButton(event -> {
			setSizeUndefined();
			center();
		}));

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(DrinkTO originalTO, Binder<DrinkTO> binder) {
		try {
			DrinkTO writeTO = originalTO == null ? new DrinkTO() : originalTO;
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

}
