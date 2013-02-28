package org.myftp.gattserver.grass3.injection.parent;

import javax.annotation.Resource;

public class Parent {

	@Resource(name = "testBean")
	private TestBean testBean;

	protected void prepare() {
		System.out.println(testBean.getTag());
	}
}
