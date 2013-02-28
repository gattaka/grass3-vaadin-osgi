package org.myftp.gattserver.grass3.injection.list;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.myftp.gattserver.grass3.test.BaseSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ListInjectTest extends BaseSpringTest {

	@Autowired
	private List<ItemInterface> items;

	@Test
	public void test() {

		int cnt = 0;
		for (ItemInterface item : items) {
			System.out.println(item.getName());
			cnt++;
		}

		assertEquals(2, cnt);
	}

}
