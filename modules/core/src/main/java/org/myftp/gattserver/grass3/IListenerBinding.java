package org.myftp.gattserver.grass3;

public interface BindListener<E> {

	public void onBind(E service);

	public void onUnbind(E service);

}
