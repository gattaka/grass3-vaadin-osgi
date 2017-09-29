package cz.gattserver.grass3.security;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Vytváří SHA1 encoder
 */
@Component("grassPasswordEncoder")
public class GrassPasswordEncoder extends ShaPasswordEncoder {

	private final String SALT;

	public GrassPasswordEncoder() {
		// TODO ... ani tahle výchozí konfigurace by asi neměla být takhle tady
		// napevno dána
		this(1, "a&~54%|$gre564a45sa54sđĐ[#54");
	}

	public GrassPasswordEncoder(int strength, String salt) {
		super(strength);
		SALT = salt;
	}

	@Override
	public String encodePassword(String rawPass, Object salt) {
		return super.encodePassword(rawPass, salt == null ? SALT : salt);
	}

}
