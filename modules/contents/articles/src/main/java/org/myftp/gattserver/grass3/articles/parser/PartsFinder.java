package org.myftp.gattserver.grass3.articles.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Hledač částí pro částečné úpravy
 * 
 * @author Gattaka
 * 
 */
public class PartsFinder {

	public static class Result {

		private String prePart = "";
		private String targetPart = "";
		private String postPart = "";

		// testovací účely
		private int checkSum;

		public String getPrePart() {
			return prePart;
		}

		public String getTargetPart() {
			return targetPart;
		}

		public String getPostPart() {
			return postPart;
		}

		public int getCheckSum() {
			return checkSum;
		}

	}

	static enum ScanPhase {
		PRE_PART, TARGET_PART, POST_PART
	}

	private PartsFinder() {
	}

	/**
	 * Naparsuje vstupní text dle nadpisu a jeho pozice.
	 * 
	 * @param inputStream
	 * @param searchPartOrderNumber
	 *            kolikátá v pořadí má být hledaná část ? První nadpis, pátý
	 *            nadpis ?
	 * @return
	 * @throws IOException
	 */
	public static Result findParts(InputStream inputStream,
			final int searchPartOrderNumber) throws IOException {

		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// tohle by se opravdu stát nemělo
			e.printStackTrace();
		}

		int hitCounter = 0;

		FinderArray searchWindow = new FinderArray();

		StringBuilder builder = new StringBuilder();
		ScanPhase phase = ScanPhase.PRE_PART;

		Result result = new Result();

		int c;
		while (true) {

			c = reader.read();

			/**
			 * Dokud není konec souboru střádej text a hledej počátek nadpisu
			 */
			if (c != -1) {
				builder.append((char) c);

				/**
				 * Nadpis mne zajímá pouze pokud jsem ještě nenašel cílovou část
				 * a nebo pokud hledám začátek dalšího nadpisu kde tím pádem
				 * cílová část končí
				 */
				if (phase != ScanPhase.POST_PART) {
					searchWindow.addChar((char) c);

					/**
					 * Byl nalezen začátek nadpisu ?
					 */
					if (searchWindow.getChar(0) == '['
							&& searchWindow.getChar(1) == 'N'
							&& searchWindow.getChar(2) >= '0'
							&& searchWindow.getChar(2) <= '5'
							&& searchWindow.getChar(3) == ']') {

						/**
						 * Hledal jsem cílovou část - právě skončila "předčást"
						 */
						if (phase == ScanPhase.PRE_PART) {

							/**
							 * Zvyš čítač nálezů nadpisů - pokud byl nalezen
							 * hledaný nadpis (chtěl jsem text 3. nadpisu apod.)
							 * zpracuj ho jako předčást
							 */
							if (hitCounter == searchPartOrderNumber) {
								result.prePart = builder.substring(
										0,
										builder.length()
												- searchWindow.getSize());
								phase = ScanPhase.TARGET_PART;
							}
							hitCounter++; 

						} else {
							result.targetPart = builder.substring(
									result.prePart.length(), builder.length()
											- searchWindow.getSize());
							phase = ScanPhase.POST_PART;
						}
					}
				}

			} else {

				switch (phase) {
				case PRE_PART:
					/**
					 * Nebyl nalezen ani jeden nadpis
					 */
					result.targetPart = builder.toString();
					break;
				case TARGET_PART:
					/**
					 * Cílová část sahá až na konec souboru
					 */
					result.targetPart = builder.substring(result.prePart
							.length());
					break;
				case POST_PART:
					/**
					 * Konec "post-části"
					 */
					result.postPart = builder.substring(result.prePart.length()
							+ result.targetPart.length());
				}

				/**
				 * Je konec souboru, opusť smyčku
				 */
				break;
			}

		}

		result.checkSum = builder.length();

		return result;
	}

}