package cz.gattserver.grass3.articles.services.mockjs;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.services.mock.MockPlugin;

@Component
public class MockJSPlugin implements Plugin {

	private final String tag = "MOCKJS_TAG";
	private String image = "mock/mockjs.png";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new MockJSParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "MockJS")
				.setImageResource(new StreamResource("mockjs.png", new InputStreamFactory() {
					private static final long serialVersionUID = 7776929061334071659L;

					@Override
					public InputStream createInputStream() {
						return MockPlugin.class.getResourceAsStream(image);
					}
				})).build();
	}

}
