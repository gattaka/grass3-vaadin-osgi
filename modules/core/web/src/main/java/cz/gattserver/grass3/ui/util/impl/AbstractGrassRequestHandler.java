package cz.gattserver.grass3.ui.util.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

import cz.gattserver.grass3.ui.util.IGrassRequestHandler;

public abstract class AbstractGrassRequestHandler implements IGrassRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	private String mountPoint;

	/**
	 * @param mountPoint
	 *            místo do kterého bude přimountován tento servlet - musí začínat zpětným lomítkem - například
	 *            "/soubory"
	 */
	public AbstractGrassRequestHandler(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	protected String getMimeType(String fileName) {
		return null;
	}

	protected abstract File getFile(String fileName) throws FileNotFoundException;

	public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
			throws IOException {

		String path = request.getPathInfo();

		// adresa musí začínat mountpointem
		// adresa musí být delší než mountpoint + '/'
		if (path.startsWith(mountPoint) && path.length() > (mountPoint.length() + 1)) {

			String fileName = path.substring(mountPoint.length() + 1);

			InputStream in = null;
			try {
				File file = getFile(fileName);
				response.setHeader("Content-Length", String.valueOf(file.length()));
				in = new BufferedInputStream(new FileInputStream(file));
			} catch (Exception e) {
				response.sendError(404, "Content not found");
				return true;
			}

			try {
				byte[] buffer = new byte[1024];

				int bytesRead = in.read(buffer);
				while (bytesRead > -1) {
					response.getOutputStream().write(buffer, 0, bytesRead);
					bytesRead = in.read(buffer);
				}

				String mime = getMimeType(fileName);
				if (mime != null)
					response.setContentType(mime);
			} catch (Exception e) {
				// může se stát, že se zavře u klienta socket, pak je potřeba zavřít stream
				System.out.println("Socket on client failed -- closing stream");
				in.close();
				return false;
			}
			in.close();
			return true; // We wrote a response
		} else {
			return false; // No response was written
		}
	}

}
