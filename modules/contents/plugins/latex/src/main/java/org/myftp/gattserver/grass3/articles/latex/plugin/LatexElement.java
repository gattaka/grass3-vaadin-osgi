/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myftp.gattserver.grass3.articles.latex.plugin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.articles.latex.config.LatexConfiguration;
import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.exceptions.ParserException;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.config.ConfigurationManager;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * 
 * @author gatt
 */
public class LatexElement extends AbstractParserPlugin {

	private String tag;

	public LatexElement(String tag) {
		this.tag = tag;
	}

	private String bytesToHex(byte[] bt) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < bt.length; j++) {
			buf.append(hexDigit[(bt[j] >> 4) & 0x0f]);
			buf.append(hexDigit[bt[j] & 0x0f]);
		}
		return buf.toString();
	}

	private byte[] getSHA1FromString(String input)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(input.getBytes());
		return md.digest();
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	private String getOutputPath() {
		ConfigurationManager configurationManager = ConfigurationManager
				.getInstance();
		if (configurationManager == null)
			throw new ParserException();
		else {
			try {
				return new ConfigurationUtils<LatexConfiguration>(
						new LatexConfiguration(),
						LatexConfiguration.CONFIG_PATH)
						.loadExistingOrCreateNewConfiguration().getOutputPath();
			} catch (JAXBException e) {
				e.printStackTrace();
				throw new ParserException();
			}
		}
	}

	@Override
	public AbstractElementTree parse(PluginBag pluginBag) {

		// zpracovat počáteční tag
		String startTag = pluginBag.getStartTag();

		if (!startTag.equals(tag)) {
			log("Čekal jsem: [" + tag + "] ne " + startTag);
			throw new ParserException();
		}

		// START_TAG byl zpracován
		pluginBag.nextToken();

		StringBuilder formulaBuilder = new StringBuilder();

		/**
		 * Zpracuje vnitřek tagů jako code - jedu dokud nenarazím na svůj
		 * koncový tag - všechno ostatní beru jako obsah latex zdrojáku - text i
		 * potenciální počáteční tagy. Jediná věc, která mne může zastavit je
		 * EOF nebo můj koncový tag.
		 */
		while (true) {
			if ((pluginBag.getToken() == Token.END_TAG && pluginBag.getEndTag()
					.equals(tag)) || pluginBag.getToken() == Token.EOF)
				break;
			formulaBuilder.append(pluginBag.getCodeTextTree().getText());
		}

		String formula = formulaBuilder.toString();

		/**
		 * Spočítej ze zadání hash a vytvoř jména
		 */
		String formulaHash = "";
		try {
			formulaHash = bytesToHex(getSHA1FromString(formula));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ParserException();
		}
		String outputPath = getOutputPath();

		/**
		 * Existuje výstupní adresář ?
		 */
		File output = new File(outputPath);
		if (!output.exists())
			if (output.mkdirs() == false) {
				log("Error during creating " + outputPath);
				throw new ParserException();
			}

		String filePath = outputPath + "/" + formulaHash + ".png";
		String path = LatexConfiguration.IMAGE_PATH_ALIAS + "/" + formulaHash
				+ ".png";

		/**
		 * Pokud existuje soubor, který má stejný hash, pak se nezdržuj renderem
		 * a ber jenom odkaz
		 */
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				TeXFormula teXFormula = new TeXFormula(formula);
				TeXIcon teXIcon = teXFormula.createTeXIcon(
						TeXConstants.STYLE_DISPLAY, 20);
				BufferedImage image = new BufferedImage(teXIcon.getIconWidth(),
						teXIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				JLabel label = new JLabel();
				label.setForeground(Color.darkGray);
				teXIcon.paintIcon(label, image.getGraphics(), 0, 0);
				ImageIO.write(image, "png", file);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ParserException();
			}
		}

		// zpracovat koncový tag
		String endTag = pluginBag.getEndTag();
		log(this.getClass().getSimpleName() + ": " + pluginBag.getToken());

		if (!endTag.equals(tag)) {
			log("Čekal jsem: [/" + tag + "], ne " + pluginBag.getCode());
			throw new ParserException();
		}

		// END_TAG byl zpracován
		pluginBag.nextToken();

		// position 1, position 2, link odkazu, text odkazu (optional), ikona
		// (optional), default ikona
		return new LatexTree(pluginBag.getContextRoot() + path, formula);
	}

	@Override
	public boolean canHoldBreakline() {
		// nemůžu vložit <br/> do latex elementu
		return false;
	}
}
