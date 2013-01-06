package enumtest;

import org.junit.Test;

public class EnumTest {

	private interface Ifce {
		public Long getId();
	}

	private enum Greek implements Ifce {
		ALFA(5L);

		private Long id;

		private Greek(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void test() {

		Enum<Greek> en1 = Greek.ALFA;
		Enum<?> en2 = Greek.ALFA;
		Class<? extends Enum<Greek>> en3 = Greek.class;
		Class<? extends Enum<?>> en4 = Greek.class;

		Ifce en5 = Greek.ALFA;
		Class<? extends Ifce> en6 = Greek.class;

		Enum<? extends Ifce> en7 = Greek.ALFA;
		// en7.getId(); ERROR ????!!!
		// en7 = Latin.A; ERROR - OK

		System.out.println(((Ifce) en7).getId());

		System.out.println(en7.getDeclaringClass());
		System.out.println(en7.getClass());

	}

}
