package org.myftp.gattserver.grass3.hw.web;

import java.io.File;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmWindow;
import org.myftp.gattserver.grass3.subwindows.GrassWindow;
import org.myftp.gattserver.grass3.subwindows.ImageDetailWindow;
import org.myftp.gattserver.grass3.template.MultiUpload;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class HWItemImagesWindow extends GrassWindow {

	private static final long serialVersionUID = 8587632602779343179L;

	private IHWFacade hwFacade;

	final GridLayout listLayout = new GridLayout();

	public HWItemImagesWindow(final HWItemDTO hwItem) {
		super(hwItem.getName());

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		setWidth("900px");
		// setHeight("780px");

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
		panel.setHeight("700px");
		layout.addComponent(panel);
		listLayout.setColumns(4);
		listLayout.setSpacing(true);
		listLayout.setMargin(true);

		MultiUpload multiFileUpload = new MultiUpload() {
			private static final long serialVersionUID = -3899558855555370125L;

			@Override
			protected void handleFile(File file, String fileName, String mimeType, long length) {
				hwFacade.saveImagesFile(file, fileName, hwItem);

				// refresh listu
				listLayout.removeAllComponents();
				createImagesList(hwItem);
			}
		};

		multiFileUpload.setCaption("Vložit fotografie");
		multiFileUpload.setSizeUndefined();
		uploadWrapperLayout.addStyleName("bordered");
		uploadWrapperLayout.addComponent(multiFileUpload);
		uploadWrapperLayout.setComponentAlignment(multiFileUpload, Alignment.MIDDLE_CENTER);

		createImagesList(hwItem);

		center();

	}

	private void createImagesList(final HWItemDTO hwItem) {

		for (final File file : hwFacade.getHWItemImagesFiles(hwItem)) {

			VerticalLayout imageLayout = new VerticalLayout();
			listLayout.addComponent(imageLayout);
			imageLayout.setSpacing(true);

			Resource resource = new FileResource(file);
			Image img = new Image(null, resource);
			img.setWidth("200px");
			imageLayout.addComponent(img);

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSpacing(true);

			Button hwItemImageDetailBtn = new Button("Detail", new Button.ClickListener() {
				private static final long serialVersionUID = 3574387596782957413L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(new ImageDetailWindow(hwItem.getName(), file));
				}
			});

			Button hwItemImageDeleteBtn = new Button("Smazat", new Button.ClickListener() {
				private static final long serialVersionUID = 3574387596782957413L;

				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat foto HW položky ?") {
						private static final long serialVersionUID = -1901927025986494370L;

						@Override
						protected void onConfirm(ClickEvent event) {
							hwFacade.deleteHWItemFile(hwItem, file);

							// refresh listu
							listLayout.removeAllComponents();
							createImagesList(hwItem);
						}
					});
				}
			});

			hwItemImageDetailBtn.setIcon(new ThemeResource("img/tags/search_16.png"));
			hwItemImageDeleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

			btnLayout.addComponent(hwItemImageDetailBtn);
			btnLayout.addComponent(hwItemImageDeleteBtn);

			imageLayout.addComponent(btnLayout);
			imageLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);

		}

	}
}
