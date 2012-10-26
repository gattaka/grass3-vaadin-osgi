package enumsingleton;

public enum Singleton {

	INSTANCE;
	
	private Singleton() {
		System.out.println("I live again !!!");
	}
	
}
