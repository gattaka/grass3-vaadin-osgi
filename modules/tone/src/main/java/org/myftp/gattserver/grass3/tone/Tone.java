package org.myftp.gattserver.grass3.tone;

public enum Tone {

	C(0),
	Cis(1),
	D(2),
	Dis(3),
	E(4),
	F(5),
	Fis(6),
	G(7),
	Gis(8),
	A(9),
	Ais(10),
	H(11);
	
	public static Tone[] tones = {C,Cis,D,Dis,E,F,Fis,G,Gis,A,Ais,H};
	
	private int orderNumberFromC;
	
	private Tone(int number) {
		this.orderNumberFromC = number;
	}
	
	public int getOrderNumberFromC() {
		return orderNumberFromC;
	}
	
}
