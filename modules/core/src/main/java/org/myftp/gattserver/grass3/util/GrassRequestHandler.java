package org.myftp.gattserver.grass3.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

public abstract class GrassRequestHandler implements RequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	private String mountPoint;

	/**
	 * @param mountPoint
	 *            místo do kterého bude přimountován tento servlet - musí
	 *            začínat zpětným lomítkem - například "/soubory"
	 */
	public GrassRequestHandler(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	protected abstract String getMimeType(String fileName);

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

			response.setContentType(getMimeType(fileName));

			return true; // We wrote a response
		} else
			return false; // No response was written
	}

}
