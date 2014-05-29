package org.myftp.gattserver.grass3.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.io.IOUtils;

import com.vaadin.ui.CssLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

public abstract class MultiUpload extends CssLayout {

	private static final long serialVersionUID = 8634797364790772321L;

	protected MultiFileUpload multiFileUpload;
	protected UploadStateWindow stateWindow;

	protected abstract void handleFile(File file, String fileName, String mimeType, long length);

	protected void onFail(String fileName, String mime, long size) {
	}

	@Override
	public void setCaption(String caption) {
		multiFileUpload.setCaption(caption);
	}

	public MultiUpload() {
		this(true);
	}

	public MultiUpload(boolean multiple) {
		UploadStateWindow stateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new MultiFileUpload(new UploadFinishedHandler() {

			@Override
			public void handleFile(InputStream in, String fileName, String mime, long size) {
				File tempFile;
				try {
					tempFile = File.createTempFile("tmp_upload_" + System.currentTimeMillis(), ".tmp");
					FileOutputStream out = new FileOutputStream(tempFile);
					IOUtils.copy(in, out);
					MultiUpload.this.handleFile(tempFile, fileName, mime, size);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, stateWindow, multiple);
		addComponent(multiFileUpload);
	}

}
