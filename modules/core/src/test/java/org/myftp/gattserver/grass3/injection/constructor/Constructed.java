package org.myftp.gattserver.grass3.injection.constructor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.myftp.gattserver.grass3.injection.list.ItemInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("constructed")
@Scope("prototype")
public class Constructed {

	private Integer integer;

	@Resource(name = "itemA")
	private ItemInterface item;

	public void setInteger(int i) {
		this.integer = i;
	}

	public void printInteger() {
		System.out.println(item.getName());
		System.out.println(integer);
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		System.out.println("postConstruct");
	}
}
