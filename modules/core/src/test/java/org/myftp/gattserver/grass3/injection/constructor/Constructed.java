package org.myftp.gattserver.grass3.injection.constructor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.myftp.gattserver.grass3.injection.list.ItemA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("constructed")
@Scope("prototype")
public class Constructed {

	private Integer integer;

	@Resource(name = "itemA")
	private ItemA item;

	private Constructed(Integer i) {
		this.integer = i;
		// chyba - objekt ještě nebyl vytvořen
		// System.out.println(item.getName());
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
