package cz.gattserver.grass3.pg.ui.pages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.pg.service.PGService;
import cz.gattserver.grass3.ui.windows.ProgressWindow;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.MultiUpload;
import cz.gattserver.web.common.ui.window.WarnWindow;

public abstract class PGMultiUpload extends MultiUpload {

	private static final long serialVersionUID = -5223991901495532219L;

	private static final Logger logger = LoggerFactory.getLogger(PGMultiUpload.class);

	@Autowired
	private PGService pgService;

	private String galleryDir;

	/**
	 * Je již zobrazené okno, informující o tom, že některé nahrávané soubory
	 * mají jmennou kolizi s exitujcími soubory galerie?
	 */
	private boolean warnWindowDeployed = false;
	private Label existingFiles;
	private WarnWindow warnWindow;
	private UI ui;

	public PGMultiUpload(String galleryDir) {
		setCaption("Nahrát obsah");
		this.galleryDir = galleryDir;
		this.ui = UI.getCurrent();
		SpringContextHelper.inject(this);
	}

	public boolean isWarnWindowDeployed() {
		return warnWindowDeployed;
	}
	
	public WarnWindow getWarnWindow() {
		return warnWindow;
	}

	protected void fileUploadSuccess(String fileName) {
	}

	@Override
	protected void fileUploadFinished(InputStream in, String fileName, String mimeType, long length,
			int filesLeftInQueue) {
		try {
			pgService.uploadFile(in, fileName, galleryDir);
			fileUploadSuccess(fileName);
		} catch (FileAlreadyExistsException f) {
			if (!warnWindowDeployed) {
				existingFiles = new Label("", ContentMode.HTML);
				warnWindow = new WarnWindow("Následující soubory již existují:") {
					private static final long serialVersionUID = 3428203680996794639L;

					@Override
					protected void createDetails(String details) {
						addComponent(existingFiles);
					}

					@Override
					public void close() {
						existingFiles.setValue("");
						warnWindowDeployed = false;
						super.close();
					}
				};
				ProgressWindow.runInUI(() -> ui.addWindow(warnWindow), ui);
				warnWindowDeployed = true;
			}
			existingFiles.setValue(existingFiles.getValue() + fileName + "<br/>");
		} catch (IOException e) {
			logger.error("Nezdařilo se uložit soubor {}", fileName, e);
		}
		super.fileUploadFinished(in, fileName, mimeType, length, filesLeftInQueue);
	}

}
