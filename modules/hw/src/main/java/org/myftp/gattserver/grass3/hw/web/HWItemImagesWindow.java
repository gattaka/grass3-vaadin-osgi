package org.myftp.gattserver.grass3.hw.web;

import java.io.File;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.subwindows.ImageDetailSubwindow;
import org.vaadin.easyuploads.MultiFileUpload;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class HWItemImagesWindow extends GrassSubWindow {

	private static final long serialVersionUID = 8587632602779343179L;

	private IHWFacade hwFacade;

	final private HorizontalLayout uploadWrapperLayout = new HorizontalLayout();

	public HWItemImagesWindow(final HWItemDTO hwItem) {
		super(hwItem.getName());

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		setWidth("850px");
		setHeight("780px");

		final GridLayout layout = new GridLayout();
		layout.setColumns(4);
		setContent(layout);
		layout.setSpacing(true);
		layout.setMargin(true);

		uploadWrapperLayout.setWidth("200px");
		uploadWrapperLayout.setHeight("100%");

		MultiFileUpload upload = new MultiFileUpload() {
			private static final long serialVersionUID = 7352892558261131844L;

			@Override
			protected void handleFile(File file, String fileName,
					String mimeType, long length) {
				hwFacade.saveImagesFile(file, fileName, hwItem);

				// refresh listu
				layout.removeAllComponents();
				layout.addComponent(uploadWrapperLayout);
				createImagesList(layout, hwItem);
			}
		};
		upload.setUploadButtonCaption("Vložit fotografie");
		upload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(upload);
		uploadWrapperLayout.setComponentAlignment(upload,
				Alignment.MIDDLE_CENTER);

		layout.addComponent(uploadWrapperLayout);

		createImagesList(layout, hwItem);

		center();

	}

	private void createImagesList(final GridLayout layout,
			final HWItemDTO hwItem) {

		for (final File file : hwFacade.getHWItemImagesFiles(hwItem)) {

			VerticalLayout imageLayout = new VerticalLayout();
			layout.addComponent(imageLayout);
			imageLayout.setSpacing(true);

			Resource resource = new FileResource(file);
			Image img = new Image(null, resource);
			img.setWidth("200px");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button hwItemImageDetailBtn = new Button("Detail",
					new Button.ClickListener() {
						private static final long serialVersionUID = 3574387596782957413L;

						@Override
						public void buttonClick(ClickEvent event) {
							UI.getCurrent().addWindow(
									new ImageDetailSubwindow(hwItem.getName(),
											file));
						}
					});

			Button hwItemImageDeleteBtn = new Button("Smazat",
					new Button.ClickListener() {
						private static final long serialVersionUID = 3574387596782957413L;

						@Override
						public void buttonClick(ClickEvent event) {
							UI.getCurrent()
									.addWindow(
											new ConfirmSubwindow(
													"Opravdu smazat foto HW položky ?") {
												private static final long serialVersionUID = -1901927025986494370L;

												@Override
												protected void onConfirm(
														ClickEvent event) {
													hwFacade.deleteHWItemFile(
															hwItem, file);

													// refresh listu
													layout.removeAllComponents();
													layout.addComponent(uploadWrapperLayout);
													createImagesList(layout,
															hwItem);
												}
											});
						}
					});

			hwItemImageDetailBtn.setIcon(new ThemeResource(
					"img/tags/search_16.png"));
			hwItemImageDeleteBtn.setIcon(new ThemeResource(
					"img/tags/delete_16.png"));

			btnLayout.addComponent(hwItemImageDetailBtn);
			btnLayout.addComponent(hwItemImageDeleteBtn);

			imageLayout.addComponent(btnLayout);
			imageLayout.setComponentAlignment(btnLayout,
					Alignment.BOTTOM_CENTER);

		}

	}
}
