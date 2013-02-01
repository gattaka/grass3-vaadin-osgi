package sandbox;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Window;

/**
 * Kořenový předek všech oken, která lze zobrazit v Grass systému
 * 
 * @author gatt
 * 
 */
public abstract class GrassWindow extends Window {

	private static final long serialVersionUID = 8889472078008074552L;

	@Override
	public void setApplication(Application application) {
		super.setApplication(application);
		buildLayout();
	}

	/**
	 * Teprve tato metoda staví obsah okna - je jí potřeba mít takhle oddělenou,
	 * protože během stavby využívá dat jako referenci na instanci aplikace
	 * apod. Tyto informace jsou oknu dodány později (settery apod.), takže
	 * kdyby tato logika byla přímo v konstruktoru, vznikne problém ve velkém
	 * množství null pointer chyb apod.
	 * 
	 * Zároveň je tak možné počkat až budou zaregistrována všechny povinná okna
	 * do aplikace a teprve poté na nich na všech zavolat build, při kterém
	 * nebude ani problém s nacházením těchto oken v registru oken aplikace
	 */
	protected abstract void buildLayout();

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {
		onShow();
		return super.handleURI(context, relativeUri);
	}

	/**
	 * Speciální metoda, která je volána při každém zobrazení okna, je tak
	 * pomocí této metody provádět refresh různých objektů v okně.
	 * 
	 * Metoda je volána původně z handleURI, takže definitivní přepsání
	 * handleURI bez volání předka způsobí nefunkčnost této metody
	 */
	protected abstract void onShow();

	public URL getWindowURL(URL appURL, String name) {
		URL url = null;
		try {
			url = new URL(appURL, name + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * Získá instanci okna dle jména
	 */
	public Window getWindow(String name) {
		return getApplication().getWindow(name);
	}
}
