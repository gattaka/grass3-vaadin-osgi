package cz.gattserver.grass3.pg.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.pg.test.MockFileSystemService;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class PGServiceImplTest extends AbstractContextAwareTest {

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
