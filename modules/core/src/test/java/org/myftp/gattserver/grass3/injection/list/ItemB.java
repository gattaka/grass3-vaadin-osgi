package org.myftp.gattserver.grass3.injection.list;

import org.springframework.stereotype.Component;

@Component
public class ItemB implements ItemInterface {

	@Override
	public String getName() {
		return "itemB";
	}

}
