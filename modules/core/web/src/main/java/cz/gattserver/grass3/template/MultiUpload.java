package cz.gattserver.grass3.template;

import java.io.InputStream;

import com.vaadin.ui.CssLayout;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

public abstract class MultiUpload extends CssLayout {

	private static final long serialVersionUID = 8634797364790772321L;

	protected MultiFileUpload multiFileUpload;
	protected UploadStateWindow stateWindow;

	protected abstract void handleFile(InputStream in, String fileName, String mimeType, long length);

	protected void onFail(String fileName, String mime, long size) {
	}

	@Override
	public void setCaption(String caption) {
		multiFileUpload.setUploadButtonCaptions(caption, caption);
	}

	public MultiUpload() {
		this(true);
	}

	public MultiUpload(boolean multiple) {
		stateWindow = new UploadStateWindow();
		multiFileUpload = new MultiFileUpload(new UploadFinishedHandler() {

			@Override
			public void handleFile(InputStream in, String fileName, String mime, long size) {
				MultiUpload.this.handleFile(in, fileName, mime, size);
			}

		}, stateWindow, multiple);
		multiFileUpload.setWidth(null);
		addComponent(multiFileUpload);
	}

}
