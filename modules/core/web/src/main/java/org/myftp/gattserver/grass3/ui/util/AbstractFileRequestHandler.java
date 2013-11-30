package org.myftp.gattserver.grass3.ui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.myftp.gattserver.grass3.ui.util.impl.AbstractGrassRequestHandler;

public abstract class AbstractFileRequestHandler extends AbstractGrassRequestHandler {

	public AbstractFileRequestHandler(String mountPoint) {
		super(mountPoint);
	}

	private static final long serialVersionUID = 7154339775034959876L;

	/**
	 * Zjistí dle aktuální konfigurace kořenový adresář
	 */
	protected abstract String getRootDir();

	@Override
	protected InputStream getResourceStream(String fileName)
			throws FileNotFoundException {
		File file = new File(getRootDir() + "/" + fileName);
		return new BufferedInputStream(new FileInputStream(file));
	}

}
