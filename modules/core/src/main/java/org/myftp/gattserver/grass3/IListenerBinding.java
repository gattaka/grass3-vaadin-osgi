package org.myftp.gattserver.grass3;

public interface IListenerBinding<E> {

	public void onBind(E service);

	public void onUnbind(E service);

}
