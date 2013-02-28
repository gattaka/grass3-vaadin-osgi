package org.myftp.gattserver.grass3.injection.parent;

import javax.annotation.Resource;

import org.junit.Test;
import org.myftp.gattserver.grass3.test.BaseSpringTest;

public class ParentInjectTest extends BaseSpringTest {

	@Resource(name = "childBean")
	private Child child;

	@Test
	public void test() {

		child.start();

	}

}
