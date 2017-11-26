package cz.gattserver.grass3.articles.editor.parser.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.util.PartsFinder.Result;

public class PartsFinderTest {

	@Test
	public void testMiddlePart() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("TestText.txt");
		Result result = PartsFinder.findParts(in, 2);
		String prePart = result.getPrePart();
		String targetPart = result.getTargetPart();
		String postPart = result.getPostPart();

		int partsLengthSum = prePart.length() + targetPart.length() + postPart.length();
		assertEquals(result.getCheckSum(), partsLengthSum);

		InputStream inPrePartExp = this.getClass().getResourceAsStream("PrePart2.txt");
		String prePartExp = IOUtils.toString(inPrePartExp, StandardCharsets.UTF_8);
		assertEquals(prePartExp, prePart);

		InputStream inTargetPartExp = this.getClass().getResourceAsStream("TargetPart2.txt");
		String targetPartExp = IOUtils.toString(inTargetPartExp, StandardCharsets.UTF_8);
		assertEquals(targetPartExp, targetPart);

		InputStream inPostPartExp = this.getClass().getResourceAsStream("PostPart2.txt");
		String postPartExp = IOUtils.toString(inPostPartExp, StandardCharsets.UTF_8);
		assertEquals(postPartExp, postPart);
	}

	@Test
	public void testFirstPart() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("TestText.txt");
		Result result = PartsFinder.findParts(in, 0);
		String prePart = result.getPrePart();
		String targetPart = result.getTargetPart();
		String postPart = result.getPostPart();

		int partsLengthSum = prePart.length() + targetPart.length() + postPart.length();
		assertEquals(result.getCheckSum(), partsLengthSum);

		assertEquals(0, prePart.length());

		InputStream inTargetPartExp = this.getClass().getResourceAsStream("TargetPart0.txt");
		String targetPartExp = IOUtils.toString(inTargetPartExp, StandardCharsets.UTF_8);
		assertEquals(targetPartExp, targetPart);

		InputStream inPostPartExp = this.getClass().getResourceAsStream("PostPart0.txt");
		String postPartExp = IOUtils.toString(inPostPartExp, StandardCharsets.UTF_8);
		assertEquals(postPartExp, postPart);
	}

	@Test
	public void testLastPart() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("TestText.txt");
		Result result = PartsFinder.findParts(in, 3);
		String prePart = result.getPrePart();
		String targetPart = result.getTargetPart();
		String postPart = result.getPostPart();

		int partsLengthSum = prePart.length() + targetPart.length() + postPart.length();
		assertEquals(result.getCheckSum(), partsLengthSum);

		InputStream inPrePartExp = this.getClass().getResourceAsStream("PrePart3.txt");
		String prePartExp = IOUtils.toString(inPrePartExp, StandardCharsets.UTF_8);
		assertEquals(prePartExp, prePart);

		InputStream inTargetPartExp = this.getClass().getResourceAsStream("TargetPart3.txt");
		String targetPartExp = IOUtils.toString(inTargetPartExp, StandardCharsets.UTF_8);
		assertEquals(targetPartExp, targetPart);

		assertEquals(0, postPart.length());
	}

	@Test
	public void testNoPart() throws IOException {
		InputStream in = this.getClass().getResourceAsStream("TestTextNoParts.txt");
		Result result = PartsFinder.findParts(in, 3);
		String prePart = result.getPrePart();
		String targetPart = result.getTargetPart();
		String postPart = result.getPostPart();

		int partsLengthSum = prePart.length() + targetPart.length() + postPart.length();
		assertEquals(result.getCheckSum(), partsLengthSum);

		assertEquals(0, prePart.length());

		InputStream inTargetPartExp = this.getClass().getResourceAsStream("TargetNoParts.txt");
		String targetPartExp = IOUtils.toString(inTargetPartExp, StandardCharsets.UTF_8);
		assertEquals(targetPartExp, targetPart);

		assertEquals(0, postPart.length());
	}

}
