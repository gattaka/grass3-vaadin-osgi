package enumsingleton;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumSingletonTest {

	/**
	 * Vytváří se singleton v enumu lazy ? Nebo rovnou při statickém zavedení z
	 * classloaderu ?
	 */

	@Test
	public void test() {

		System.out.println("Test started !!");
		
		Singleton singleton = Singleton.INSTANCE;
		
		Singleton singleton2 = Singleton.INSTANCE;
		
		
	
	}

}
