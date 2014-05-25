package org.myftp.gattserver.grass3.hw.web;

import java.io.File;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubWindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.HumanBytesSizeCreator;
import org.myftp.gattserver.grass3.util.StringPreviewCreator;
import org.vaadin.easyuploads.MultiFileUpload;

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

public class HWItemDocumentsWindow extends GrassSubWindow {

	private static final long serialVersionUID = 8587632602779343179L;

	private IHWFacade hwFacade;

	final VerticalLayout listLayout = new VerticalLayout();

	public HWItemDocumentsWindow(final HWItemDTO hwItem) {
		super(hwItem.getName());

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		setWidth("600px");

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSpacing(true);
		layout.setMargin(true);

		HorizontalLayout uploadWrapperLayout = new HorizontalLayout();
		uploadWrapperLayout.setWidth("100%");
		uploadWrapperLayout.setHeight("80px");
		layout.addComponent(uploadWrapperLayout);

		Panel panel = new Panel(listLayout);
		panel.setWidth("100%");
		panel.setHeight("400px");
		layout.addComponent(panel);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		MultiFileUpload upload = new MultiFileUpload() {
			private static final long serialVersionUID = 7352892558261131844L;

			@Override
			protected void handleFile(File file, String fileName, String mimeType, long length) {
				hwFacade.saveDocumentsFile(file, fileName, hwItem);

				// refresh listu
				listLayout.removeAllComponents();
				createDocumentsList(hwItem);
			}
		};
		upload.setUploadButtonCaption("Vložit dokumenty");
		upload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(upload);
		uploadWrapperLayout.setComponentAlignment(upload, Alignment.MIDDLE_CENTER);

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
					UI.getCurrent().addWindow(new ConfirmSubWindow("Opravdu smazat '" + file.getName() + "' ?") {
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
