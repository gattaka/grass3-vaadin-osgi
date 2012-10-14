package org.myftp.gattserver.grass3.facades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.myftp.gattserver.grass3.model.dao.UserDAO;
import org.myftp.gattserver.grass3.model.domain.User;

public class SecurityFacade {

	/**
	 * Singleton stuff
	 */
	private static SecurityFacade instance;

	public static SecurityFacade getInstance() {
		if (instance == null)
			instance = new SecurityFacade();
		return instance;
	}

	private SecurityFacade() {
	}

	/**
	 * Digest
	 */
	private static MessageDigest md;

	private String bytesToHex(byte[] b) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}

	private byte[] getSHA1FromString(String input)
			throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("SHA1");
		md.update(input.getBytes());
		return md.digest();
	}

	private String makeHashFromPasswordString(String password) {
		try {
			return bytesToHex(getSHA1FromString(password));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Authentikační metoda pro aplikaci
	 * 
	 * @param username
	 *            jméno uživatele, který se přihlašuje
	 * @param password
	 *            heslo, které použil
	 * @return objekt s přihlášeným uživatelem, jinak null
	 */
	public User authenticate(String username, String password) {
		List<User> loggedUser = new UserDAO().findByName(username);
		if (loggedUser != null
				&& loggedUser.size() == 1
				&& loggedUser.get(0).getPassword()
						.equals(makeHashFromPasswordString(password)))
			return loggedUser.get(0);
		return null;
	}

}
