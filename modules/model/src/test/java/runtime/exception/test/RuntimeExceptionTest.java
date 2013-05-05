package runtime.exception.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class RuntimeExceptionTest {

	@Test
	public void test() {

		try {
			throw new RuntimeException("Hou");
		} catch (Exception e) {
			System.out.println("Runtime exception occured - " + e.getMessage());
		}
		// fail("Not yet implemented");
	}

	private void throwMethod() {
		throw new RuntimeException("Hou2");
	}

	@Test
	public void test2() {

		try {
			throwMethod();
		} catch (Exception e) {
			System.out.println("Runtime exception occured - " + e.getMessage());
		}
	}

}
