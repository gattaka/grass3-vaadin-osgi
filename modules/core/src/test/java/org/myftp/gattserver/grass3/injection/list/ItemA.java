package org.myftp.gattserver.grass3.injection.list;

import org.springframework.stereotype.Component;

@Component
public class ItemA implements ItemInterface {

	@Override
	public String getName() {
		return "itemA";
	}

}
