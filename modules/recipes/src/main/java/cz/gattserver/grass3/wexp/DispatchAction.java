package cz.gattserver.grass3.wexp;

import java.io.Serializable;

import cz.gattserver.grass3.wexp.in.impl.UI;

public interface DispatchAction extends Serializable {

	public UI dispatch();

}
