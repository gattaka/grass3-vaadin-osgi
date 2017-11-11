package cz.gattserver.grass3.server;

import java.io.File;
import java.io.FileNotFoundException;

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
	protected File getFile(String fileName) throws FileNotFoundException {
		return new File(getRootDir() + "/" + fileName);
	}

}
