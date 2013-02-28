package org.myftp.gattserver.grass3.injection.constructor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("constructed")
@Scope("prototype")
public class Constructed {

	private Integer integer;

	public Constructed(Integer i) {
		this.integer = i;
	}

	public void printInteger() {
		System.out.println(integer);
	}
}
