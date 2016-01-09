package cz.gattserver.grass3.wexp.out;

import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class WebElement {

	public abstract void write(OutputStreamWriter o) throws IOException;

}
