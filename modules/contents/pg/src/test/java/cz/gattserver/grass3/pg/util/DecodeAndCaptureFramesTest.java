package cz.gattserver.grass3.pg.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.pg.test.MockFileSystemService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class DecodeAndCaptureFramesTest extends AbstractContextAwareTest {

	@Autowired
	protected MockFileSystemService fileSystemService;
	
	@Before
	public void init() {
		fileSystemService.init();
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
