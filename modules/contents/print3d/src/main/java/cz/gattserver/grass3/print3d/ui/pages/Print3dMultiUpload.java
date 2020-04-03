package cz.gattserver.grass3.print3d.ui.pages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import cz.gattserver.grass3.print3d.service.Print3dService;
import cz.gattserver.grass3.ui.dialogs.ProgressDialog;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.window.WarnDialog;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

public class Print3dMultiUpload extends Upload {

	private static final long serialVersionUID = -5223991901495532219L;

	private static final Logger logger = LoggerFactory.getLogger(Print3dMultiUpload.class);

	@Autowired
	private Print3dService print3dService;

	private MultiFileMemoryBuffer buffer;
	private Set<String> existingFiles;
	private boolean allUploadsProcessed;

	public Print3dMultiUpload(String projectDir) {
		existingFiles = new HashSet<>();
		allUploadsProcessed = false;
		setAcceptedFileTypes("image/*", "video/*");
		buffer = new MultiFileMemoryBuffer();
		setReceiver(buffer);
		SpringContextHelper.inject(this);

		// https://github.com/vaadin/vaadin-upload-flow/issues/134
		getElement().addEventListener("upload-start", e -> this.allUploadsProcessed = false);
		getElement().addEventListener("upload-success", e -> {
			JsonArray files = e.getEventData().getArray("element.files");

			boolean allUploadsProcessed = true;
			for (int i = 0; i < files.length(); i++) {
				JsonObject file = files.getObject(i);
				if (!file.getBoolean("complete"))
					allUploadsProcessed = false;
			}

			if (!this.allUploadsProcessed && allUploadsProcessed)
				onDone();
			this.allUploadsProcessed = allUploadsProcessed;
		}).addEventData("element.files");

		addFinishedListener(event -> {
			try {
				InputStream is = buffer.getInputStream(event.getFileName());
				String fileUTF8 = IOUtils.toString(event.getFileName().getBytes(), "UTF-8");
				Path path = print3dService.uploadFile(is, fileUTF8, projectDir);
				fileUploadSuccess(event.getFileName(), Files.size(path));
			} catch (FileAlreadyExistsException f) {
				existingFiles.add(event.getFileName());
			} catch (IOException e) {
				logger.error("Nezdařilo se uložit soubor {}", event.getFileName(), e);
			}
		});
	}

	protected void onDone() {
		if (existingFiles.isEmpty()) {
			allFilesUploaded();
		} else {
			WarnDialog warnWindow = new WarnDialog("Následující soubory již existují:") {
				private static final long serialVersionUID = 3428203680996794639L;

				@Override
				protected void createDetails(String details) {
					HtmlDiv div = new HtmlDiv();
					String value = "";
					for (String existing : existingFiles)
						value += existing + "<br/>";
					div.setValue(value);
					addComponent(div);
				}

				@Override
				public void close() {
					super.close();
					allFilesUploaded();
				}
			};
			ProgressDialog.runInUI(() -> warnWindow.open(), UI.getCurrent());
		}
	}

	protected void fileUploadSuccess(String fileName, long size) {
	};

	protected void allFilesUploaded() {
	};

}
