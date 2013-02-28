package org.myftp.gattserver.grass3.injection.parent;

import org.springframework.stereotype.Component;

@Component(value="childBean")
public class Child extends Parent {

	public void start() {
		prepare();
	}

}
