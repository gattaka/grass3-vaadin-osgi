package cz.gattserver.grass3.articles.services.mock;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass3.articles.editor.parser.Parser;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass3.articles.plugins.Plugin;

@Component
public class MockPlugin implements Plugin {

	private final String tag = "MOCK_TAG";
	private String image = "mock/mock.png";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new MockParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "Mock")
				.setImageResource(new StreamResource("mock.png", () -> MockPlugin.class.getResourceAsStream(image)))
				.build();
	}

}
