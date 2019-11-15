package cz.gattserver.grass3.print3d.ui.pages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

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

public class Print3dMultiUpload extends Upload {

	private static final long serialVersionUID = -5223991901495532219L;

	private static final Logger logger = LoggerFactory.getLogger(Print3dMultiUpload.class);

	@Autowired
	private Print3dService print3dService;

	/**
	 * Je již zobrazené okno, informující o tom, že některé nahrávané soubory
	 * mají jmennou kolizi s exitujcími soubory projektu?
	 */
	private boolean warnWindowDeployed = false;
	private HtmlDiv existingFiles;
	private WarnDialog warnWindow;
	private MultiFileMemoryBuffer buffer;

	public Print3dMultiUpload(String projectDir) {
		UI ui = UI.getCurrent();
		buffer = new MultiFileMemoryBuffer();
		setReceiver(buffer);
		SpringContextHelper.inject(this);
		addSucceededListener(event -> {
			try {
				InputStream is = buffer.getInputStream(event.getFileName());
				String fileUTF8 = IOUtils.toString(event.getFileName().getBytes(), "UTF-8");
				Path path = print3dService.uploadFile(is, fileUTF8, projectDir);
				fileUploadSuccess(event.getFileName(), Files.size(path));
			} catch (FileAlreadyExistsException f) {
				if (!warnWindowDeployed) {
					existingFiles = new HtmlDiv("");
					warnWindow = new WarnDialog("Následující soubory již existují:") {
						private static final long serialVersionUID = 3428203680996794639L;

						@Override
						protected void createDetails(String details) {
							addComponent(existingFiles);
						}

						@Override
						public void close() {
							existingFiles.setText("");
							warnWindowDeployed = false;
							super.close();
						}
					};
					ProgressDialog.runInUI(() -> warnWindow.open(), ui);
					warnWindowDeployed = true;
				}
				existingFiles.setValue(existingFiles.getValue() + event.getFileName() + "<br/>");
			} catch (IOException e) {
				logger.error("Nezdařilo se uložit soubor {}", event.getFileName(), e);
			}
		});
	}

	public boolean isWarnWindowDeployed() {
		return warnWindowDeployed;
	}

	public WarnDialog getWarnWindow() {
		return warnWindow;
	}

	protected void fileUploadSuccess(String fileName, long size) {
	};

}
