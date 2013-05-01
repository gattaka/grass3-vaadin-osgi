package org.myftp.gattserver.grass3.articles.basic.style.color;

import java.util.List;

import org.myftp.gattserver.grass3.articles.basic.style.StyleElement;
import org.myftp.gattserver.grass3.articles.basic.style.StyleFactory;
import org.myftp.gattserver.grass3.articles.basic.style.StyleTree;
import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;


/**
 *
 * @author gatt
 */
public class BlueFactory extends StyleFactory {

	public final static String TAG = "BLU";
	
	public BlueFactory() {
		super(TAG);
	}
	
    public AbstractParserPlugin getPluginParser() {
        return new StyleElement(TAG) {
			
			@Override
			protected StyleTree getTree(List<AbstractElementTree> elist) {
				return new BlueTree(elist);
			}
		};
    }

	public EditorButtonResources getEditorButtonResources() {
		EditorButtonResources resources = new EditorButtonResources(TAG);
		resources.setDescription("Modře");
		resources.setTagFamily("Obarvení");
		return resources;
	}

  }
