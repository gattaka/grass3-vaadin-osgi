package org.myftp.gattserver.grass3.util.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.myftp.gattserver.grass3.util.IGrassRequestHandler;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

public abstract class AbstractGrassRequestHandler implements
		IGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	private String mountPoint;

	/**
	 * @param mountPoint
	 *            místo do kterého bude přimountován tento servlet - musí
	 *            začínat zpětným lomítkem - například "/soubory"
	 */
	public AbstractGrassRequestHandler(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	protected String getMimeType(String fileName) {
		return null;
	}

	protected abstract InputStream getResourceStream(String fileName)
			throws FileNotFoundException;

	public boolean handleRequest(VaadinSession session, VaadinRequest request,
			VaadinResponse response) throws IOException {

		String path = request.getPathInfo();

		// adresa musí začínat mountpointem
		// adresa musí být delší než mountpoint + '/'
		if (path.startsWith(mountPoint)
				&& path.length() > (mountPoint.length() + 1)) {

			String fileName = path.substring(mountPoint.length() + 1);

			InputStream in = getResourceStream(fileName);

			byte[] buffer = new byte[1024];

			int bytesRead = in.read(buffer);
			while (bytesRead > -1) {
				response.getOutputStream().write(buffer, 0, bytesRead);
				bytesRead = in.read(buffer);
			}

			String mime = getMimeType(fileName);
			if (mime != null)
				response.setContentType(mime);

			return true; // We wrote a response
		} else
			return false; // No response was written
	}

}
