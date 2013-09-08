package org.myftp.gattserver.grass3.pg.pages;

import java.io.File;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.facades.INodeFacade;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.pages.factories.template.IPageFactory;
import org.myftp.gattserver.grass3.pages.template.ContentViewerPage;
import org.myftp.gattserver.grass3.pg.config.PhotogalleryConfiguration;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.pg.facade.IPhotogalleryFacade;
import org.myftp.gattserver.grass3.security.ICoreACL;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.InfoSubwindow;
import org.myftp.gattserver.grass3.subwindows.WarnSubwindow;
import org.myftp.gattserver.grass3.template.DefaultContentOperations;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.myftp.gattserver.grass3.util.URLIdentifierUtils;
import org.myftp.gattserver.grass3.util.URLPathAnalyzer;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component("photogalleryViewerPage")
@Scope("prototype")
public class PhotogalleryViewerPage extends ContentViewerPage {

	private static final long serialVersionUID = 5078280973817331002L;

	@Resource(name = "coreACL")
	private ICoreACL coreACL;

	@Resource(name = "photogalleryFacade")
	private IPhotogalleryFacade photogalleryFacade;

	@Resource(name = "userFacade")
	private IUserFacade userFacade;

	@Resource(name = "nodeFacade")
	private INodeFacade nodeFacade;

	@Resource
	private IConfigurationService configurationService;

	@Resource(name = "photogalleryViewerPageFactory")
	private IPageFactory photogalleryViewerPageFactory;

	@Resource(name = "categoryPageFactory")
	private IPageFactory categoryPageFactory;

	@Resource(name = "photogalleryEditorPageFactory")
	private IPageFactory photogalleryEditorPageFactory;

	private PhotogalleryDTO photogallery;

	public PhotogalleryViewerPage(GrassRequest request) {
		super(request);
	}

	protected void init() {
		URLPathAnalyzer analyzer = getRequest().getAnalyzer();
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils
				.parseURLIdentifier(analyzer.getCurrentPathToken());
		if (identifier == null) {
			showError404();
			return;
		}

		photogallery = photogalleryFacade.getPhotogalleryForDetail(identifier
				.getId());
		if (photogallery == null) {
			showError404();
			return;
		}

		if (photogallery.getContentNode().isPublicated()
				|| (getUser() != null && (photogallery.getContentNode()
						.getAuthor().equals(getUser()) || getUser().getRoles()
						.contains(Role.ADMIN)))) {
		} else {
			showError403();
			return;
		}

		super.init();
	}

	@Override
	protected ContentNodeDTO getContentNodeDTO() {
		return photogallery.getContentNode();
	}

	@Override
	protected void createContent(VerticalLayout layout) {

		PhotogalleryConfiguration configuration = new PhotogalleryConfiguration();
		configurationService.loadConfiguration(configuration);

		File galleryDir = new File(configuration.getRootDir(),
				photogallery.getPhotogalleryPath());

		File miniaturesDirFile = new File(galleryDir,
				configuration.getMiniaturesDir());

		if (miniaturesDirFile.exists() == false) {
			showError404();
			return;
		}

		File[] miniatures = miniaturesDirFile.listFiles();

		// 150px je nejdelší rozměr náhledu + 2x 5px margin
		int finalwidth = 150 + 10;
		int galleryWidth = 700;
		int cols = galleryWidth / finalwidth;
		int rows = (int) Math.ceil(miniatures.length * 1.0 / cols);

		GridLayout gridLayout = new GridLayout(cols, rows);
		gridLayout.setSpacing(true);
		layout.addComponent(gridLayout);

		for (int i = 0; i < miniatures.length; i++) {
			Embedded embedded = new Embedded(null, new FileResource(
					miniatures[i]));
			gridLayout.addComponent(embedded, i % cols, i / cols);
		}

	}

	@Override
	protected void updateOperationsList(CssLayout operationsListLayout) {

		// Upravit
		if (coreACL.canModifyContent(photogallery.getContentNode(), getUser())) {
			Button modifyButton = new Button(null);
			modifyButton.setDescription("Upravit");
			modifyButton
					.setIcon((com.vaadin.server.Resource) new ThemeResource(
							"img/tags/pencil_16.png"));
			modifyButton.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {

					redirect(getPageURL(photogalleryEditorPageFactory,
							DefaultContentOperations.EDIT.toString(),
							URLIdentifierUtils.createURLIdentifier(photogallery
									.getId(), photogallery.getContentNode()
									.getName())));

				}

			});
			operationsListLayout.addComponent(modifyButton);
		}

		// Smazat
		if (coreACL.canDeleteContent(photogallery.getContentNode(), getUser())) {
			Button deleteButton = new Button(null);
			deleteButton.setDescription("Smazat");
			deleteButton
					.setIcon((com.vaadin.server.Resource) new ThemeResource(
							"img/tags/delete_16.png"));
			deleteButton.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 607422393151282918L;

				public void buttonClick(ClickEvent event) {

					ConfirmSubwindow confirmSubwindow = new ConfirmSubwindow(
							"Opravdu si přejete smazat tuto galerii ?") {

						private static final long serialVersionUID = -3214040983143363831L;

						@Override
						protected void onConfirm(ClickEvent event) {

							NodeDTO node = photogallery.getContentNode()
									.getParent();

							final String category = getPageURL(
									categoryPageFactory,
									URLIdentifierUtils.createURLIdentifier(
											node.getId(), node.getName()));

							// zdařilo se ? Pokud ano, otevři info okno a při
							// potvrzení jdi na kategorii
							if (photogalleryFacade
									.deletePhotogallery(photogallery)) {
								InfoSubwindow infoSubwindow = new InfoSubwindow(
										"Smazání galerie proběhlo úspěšně.") {

									private static final long serialVersionUID = -6688396549852552674L;

									protected void onProceed(ClickEvent event) {
										redirect(category);
									};
								};
								getUI().addWindow(infoSubwindow);
							} else {
								// Pokud ne, otevři warn okno a při
								// potvrzení jdi na kategorii
								WarnSubwindow warnSubwindow = new WarnSubwindow(
										"Smazání galerie se nezdařilo.") {

									private static final long serialVersionUID = -6688396549852552674L;

									protected void onProceed(ClickEvent event) {
										redirect(category);
									};
								};
								getUI().addWindow(warnSubwindow);
							}

							// zavři původní confirm okno
							getUI().removeWindow(this);

						}
					};
					getUI().addWindow(confirmSubwindow);

				}

			});
			operationsListLayout.addComponent(deleteButton);
		}

		// Deklarace tlačítek oblíbených
		final Button removeFromFavouritesButton = new Button(null);
		final Button addToFavouritesButton = new Button(null);

		// Přidat do oblíbených
		addToFavouritesButton.setDescription("Přidat do oblíbených");
		addToFavouritesButton
				.setIcon((com.vaadin.server.Resource) new ThemeResource(
						"img/tags/heart_16.png"));
		addToFavouritesButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -4003115363728232801L;

			public void buttonClick(ClickEvent event) {

				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.addContentToFavourites(
						photogallery.getContentNode(), getUser())) {
					InfoSubwindow infoSubwindow = new InfoSubwindow(
							"Vložení do oblíbených proběhlo úspěšně.");
					getUI().addWindow(infoSubwindow);
					addToFavouritesButton.setVisible(false);
					removeFromFavouritesButton.setVisible(true);
				} else {
					// Pokud ne, otevři warn okno
					WarnSubwindow warnSubwindow = new WarnSubwindow(
							"Vložení do oblíbených se nezdařilo.");
					getUI().addWindow(warnSubwindow);
				}

			}
		});

		// Odebrat z oblíbených
		removeFromFavouritesButton.setDescription("Odebrat z oblíbených");
		removeFromFavouritesButton
				.setIcon((com.vaadin.server.Resource) new ThemeResource(
						"img/tags/broken_heart_16.png"));
		removeFromFavouritesButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4826586588570179321L;

			public void buttonClick(ClickEvent event) {

				// zdařilo se ? Pokud ano, otevři info okno
				if (userFacade.removeContentFromFavourites(
						photogallery.getContentNode(), getUser())) {
					InfoSubwindow infoSubwindow = new InfoSubwindow(
							"Odebrání z oblíbených proběhlo úspěšně.");
					getUI().addWindow(infoSubwindow);
					removeFromFavouritesButton.setVisible(false);
					addToFavouritesButton.setVisible(true);
				} else {
					// Pokud ne, otevři warn okno
					WarnSubwindow warnSubwindow = new WarnSubwindow(
							"Odebrání z oblíbených se nezdařilo.");
					getUI().addWindow(warnSubwindow);
				}

			}
		});

		// Oblíbené
		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(
				photogallery.getContentNode(), getUser()));
		removeFromFavouritesButton.setVisible(coreACL
				.canRemoveContentFromFavourites(photogallery.getContentNode(),
						getUser()));

		operationsListLayout.addComponent(addToFavouritesButton);
		operationsListLayout.addComponent(removeFromFavouritesButton);

	}

	@Override
	protected IPageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}
}
