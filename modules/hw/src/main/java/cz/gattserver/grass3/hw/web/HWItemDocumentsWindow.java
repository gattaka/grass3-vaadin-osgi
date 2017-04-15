package cz.gattserver.grass3.hw.web;

import java.io.File;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.facade.IHWFacade;
import cz.gattserver.grass3.template.MultiUpload;
import cz.gattserver.web.common.util.HumanBytesSizeCreator;
import cz.gattserver.web.common.util.StringPreviewCreator;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.WebWindow;

public class HWItemDocumentsWindow extends WebWindow {

	private static final long serialVersionUID = 8587632602779343179L;

	@Autowired
	private IHWFacade hwFacade;

	final VerticalLayout listLayout = new VerticalLayout();

	public HWItemDocumentsWindow(final HWItemDTO hwItem) {
		super(hwItem.getName());

		setWidth("882px");

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSpacing(true);
		layout.setMargin(true);

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.setWidth("100%");
		uploadWrapperLayout.setMargin(true);
		layout.addComponent(uploadWrapperLayout);

		Panel panel = new Panel(listLayout);
		panel.setWidth("100%");
		panel.setHeight("400px");
		layout.addComponent(panel);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		final MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = 8500364606014524121L;

			@Override
			public void handleFile(InputStream in, String fileName, String mime, long size) {

				hwFacade.saveDocumentsFile(in, fileName, hwItem);

				// refresh listu
				listLayout.removeAllComponents();
				createDocumentsList(hwItem);
			}

		};
		multiFileUpload.setCaption("Vložit dokumenty");
		multiFileUpload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(multiFileUpload);
		uploadWrapperLayout.setComponentAlignment(multiFileUpload, Alignment.MIDDLE_CENTER);

		createDocumentsList(hwItem);

		center();

	}

	private void createDocumentsList(final HWItemDTO hwItem) {

		for (final File file : hwFacade.getHWItemDocumentsFiles(hwItem)) {

			HorizontalLayout documentLayout = new HorizontalLayout();
			listLayout.addComponent(documentLayout);
			documentLayout.setSpacing(true);
			documentLayout.setWidth("100%");

			Button hwItemDocumentDownloadBtn = new Button("Stáhnout");
			FileDownloader downloader = new FileDownloader(new FileResource(file));
			downloader.extend(hwItemDocumentDownloadBtn);

			Button hwItemDocumentDeleteBtn = new Button("Smazat", new Button.ClickListener() {
				private static final long serialVersionUID = 3574387596782957413L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + file.getName() + "' ?") {
						private static final long serialVersionUID = -1901927025986494370L;

						@Override
						protected void onConfirm(ClickEvent event) {
							hwFacade.deleteHWItemFile(hwItem, file);

							// refresh listu
							listLayout.removeAllComponents();
							createDocumentsList(hwItem);
						}
					});
				}
			});

			hwItemDocumentDownloadBtn.setIcon(new ThemeResource("img/tags/down_16.png"));
			hwItemDocumentDeleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

			Label nameLabel = new Label(StringPreviewCreator.createPreview(file.getName(), 60));
			// nameLabel.setWidth("280px");
			nameLabel.setDescription(file.getName());
			documentLayout.addComponent(nameLabel);
			documentLayout.setExpandRatio(nameLabel, 1);
			documentLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);

			Label sizelabel = new Label(HumanBytesSizeCreator.format(file.length(), true));
			sizelabel.setDescription(file.length() + "B");
			sizelabel.setSizeUndefined();
			documentLayout.addComponent(sizelabel);
			documentLayout.setComponentAlignment(sizelabel, Alignment.MIDDLE_RIGHT);

			documentLayout.addComponent(hwItemDocumentDownloadBtn);
			documentLayout.addComponent(hwItemDocumentDeleteBtn);
			documentLayout.setComponentAlignment(hwItemDocumentDeleteBtn, Alignment.MIDDLE_RIGHT);
			documentLayout.setComponentAlignment(hwItemDocumentDeleteBtn, Alignment.MIDDLE_RIGHT);

		}

	}
}
