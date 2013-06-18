package org.myftp.gattserver.grass3.injection.parent;

import org.springframework.stereotype.Component;

@Component(value = "testBean")
public class TestBean {

	private String tag = "tag!";

	public String getTag() {
		return tag;
	}
}
