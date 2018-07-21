package cz.gattserver.grass3.drinks.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.drinks.model.domain.MaltType;
import cz.gattserver.grass3.drinks.model.interfaces.BeerTO;
import cz.gattserver.grass3.drinks.util.ImageUtils;
import cz.gattserver.grass3.ui.components.CreateButton;
import cz.gattserver.grass3.ui.components.DeleteButton;
import cz.gattserver.grass3.ui.components.ModifyButton;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class BeerWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	private static final Logger logger = LoggerFactory.getLogger(BeerWindow.class);

	private MultiUpload upload;
	private VerticalLayout imageLayout;
	private Embedded image;

	public BeerWindow() {
		this(null);
	}

	protected abstract void onSave(BeerTO to);

	private void placeImage(BeerTO to) {
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

	public BeerWindow(final BeerTO originalTO) {
		super(originalTO == null ? "Založit" : "Upravit" + " nápoj");

		// Tahle šílenost je tu proto, aby se vycentrovalo okno s donahraným
		// obrázkem. Obrázek se bohužel nahrává nějak později, takže centrování
		// nebere v potaz jeho velikost a centruje okno špatně. Tím, že se
		// centrování zavolá později (až po nahrání obrázku, na který bohužel
		// nemám listener, takže fix-time delay)
		JavaScript.getCurrent().addFunction("cz.gattserver.grass3.delayed_center", (arguments) -> {
			BeerWindow.this.setSizeUndefined();
			BeerWindow.this.center();
		});

		BeerTO formTO = new BeerTO();

		if (originalTO == null) {
			formTO.setCountry("ČR");
			formTO.setMaltType(MaltType.BARLEY);
		}

		Binder<BeerTO> binder = new Binder<>(BeerTO.class);
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
		upload.setAcceptedMimeTypes(Arrays.asList(new String[] { "image/jpg", "image/jpeg", "image/png" }));

		if (originalTO == null || originalTO.getImage() == null)
			placeUpload();
		else {
			placeImage(originalTO);
			formTO.setImage(originalTO.getImage());
		}

		final TextField breweryField = new TextField("Pivovar");
		binder.forField(breweryField).asRequired().bind(BeerTO::getBrewery, BeerTO::setBrewery);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(BeerTO::getName, BeerTO::setName);

		final TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(BeerTO::getCountry, BeerTO::setCountry);

		final RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(BeerTO::getRating, BeerTO::setRating);
		ratingStars.setAnimated(false);
		ratingStars.setCaption("Hodnocení");

		HorizontalLayout line1Layout = new HorizontalLayout(breweryField, nameField, countryField, ratingStars);

		final TextField categoryField = new TextField("Kategorie (APA, IPA, ...)");
		binder.forField(categoryField).asRequired().bind(BeerTO::getCategory, BeerTO::setCategory);

		final TextField degreeField = new TextField("Stupně (°)");
		binder.forField(degreeField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Stupně (°) musí být celé číslo"))
				.bind(BeerTO::getDegrees, BeerTO::setDegrees);
		degreeField.setWidth("80px");

		final TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo"))
				.bind(BeerTO::getAlcohol, BeerTO::setAlcohol);
		alcoholField.setWidth("80px");

		final TextField ibuField = new TextField("Hořkost (IBU)");
		binder.forField(ibuField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Hořkost (IBU) musí být celé číslo"))
				.bind(BeerTO::getIbu, BeerTO::setIbu);
		ibuField.setWidth("80px");

		final ComboBox<MaltType> maltTypeField = new ComboBox<>("Typ sladu", Arrays.asList(MaltType.values()));
		maltTypeField.setItemCaptionGenerator(MaltType::getCaption);
		binder.forField(maltTypeField).bind(BeerTO::getMaltType, BeerTO::setMaltType);

		HorizontalLayout line2Layout = new HorizontalLayout(categoryField, degreeField, alcoholField, ibuField,
				maltTypeField);

		final TextField maltsField = new TextField("Slady");
		binder.forField(maltsField).bind(BeerTO::getMalts, BeerTO::setMalts);
		maltsField.setWidth("290px");

		final TextField hopsField = new TextField("Chmely");
		binder.forField(hopsField).bind(BeerTO::getHops, BeerTO::setHops);
		hopsField.setWidth("290px");

		HorizontalLayout line3Layout = new HorizontalLayout(maltsField, hopsField);

		final TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(BeerTO::getDescription, BeerTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		HorizontalLayout btnsLayout = new HorizontalLayout();
		btnsLayout.setSizeUndefined();

		if (originalTO != null)
			btnsLayout.addComponent(new ModifyButton(event -> save(originalTO, binder)));
		else
			btnsLayout.addComponent(new CreateButton(event -> save(originalTO, binder)));

		VerticalLayout fieldsLayout = new VerticalLayout(line1Layout, line2Layout, line3Layout, descriptionField,
				btnsLayout);
		HorizontalLayout mainLayout = new HorizontalLayout(imageLayout, fieldsLayout);
		addComponent(mainLayout);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(BeerTO originalTO, Binder<BeerTO> binder) {
		try {
			BeerTO writeTO = originalTO == null ? new BeerTO() : originalTO;
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
