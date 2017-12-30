package cz.gattserver.grass3.pg.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class ImageComparatorTest {

	@Test
	public void test() throws IOException {
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("armchair.png")));
		assertTrue(ImageComparator.isEqualAsImageData(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("armchair.png")));
		assertTrue(ImageComparator.isEqualAsImagePixels(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("armchair.png")));
	}

	@Test
	public void test2() throws IOException {
		assertFalse(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("candle.png")));
		assertFalse(ImageComparator.isEqualAsImageData(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("candle.png")));
		assertFalse(ImageComparator.isEqualAsImagePixels(this.getClass().getResourceAsStream("armchair.png"),
				this.getClass().getResourceAsStream("candle.png")));
	}

	@Test
	public void test3() throws IOException {
		assertFalse(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("candle.png"),
				this.getClass().getResourceAsStream("candle_indexed_colors.png")));
		assertFalse(ImageComparator.isEqualAsImageData(this.getClass().getResourceAsStream("candle.png"),
				this.getClass().getResourceAsStream("candle_indexed_colors.png")));
		assertTrue(ImageComparator.isEqualAsImagePixels(this.getClass().getResourceAsStream("candle.png"),
				this.getClass().getResourceAsStream("candle_indexed_colors.png")));
	}

	@Test
	public void test4() throws IOException {
		assertFalse(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("candle_indexed_colors.png"),
				this.getClass().getResourceAsStream("candle.png")));
		assertFalse(ImageComparator.isEqualAsImageData(this.getClass().getResourceAsStream("candle_indexed_colors.png"),
				this.getClass().getResourceAsStream("candle.png")));
		assertTrue(ImageComparator.isEqualAsImagePixels(this.getClass().getResourceAsStream("candle_indexed_colors.png"),
				this.getClass().getResourceAsStream("candle.png")));
	}

}
