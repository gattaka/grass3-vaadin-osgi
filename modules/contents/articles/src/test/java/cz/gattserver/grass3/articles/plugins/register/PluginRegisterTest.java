package cz.gattserver.grass3.articles.plugins.register;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.Plugin;
import cz.gattserver.grass3.articles.services.mock.MockParser;
import cz.gattserver.grass3.articles.services.mock.MockPlugin;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class PluginRegisterTest extends AbstractContextAwareTest {

	@Autowired
	private PluginRegisterService pluginRegister;

	@Test
	public void testGetRegisteredTags() {
		assertEquals(6, pluginRegister.getRegisteredTags().size());
		assertEquals("[MOCKJS_TAG, N1, N2, N3, N4, MOCK_TAG]", pluginRegister.getRegisteredTags().toString());
	}

	@Test
	public void testCreateRegisterSnapshot() {
		Map<String, Plugin> map = pluginRegister.createRegisterSnapshot();

		Plugin mockTag = map.get("MOCK_TAG");
		assertNotNull(mockTag);
		assertEquals("MOCK_TAG", mockTag.getTag());
		assertEquals(MockPlugin.class, mockTag.getClass());
		assertEquals(MockParser.class, mockTag.getParser().getClass());

		assertNull(map.get("NONEXISTENT_TAG"));
	}

	@Test
	public void testGetGroupTags() {
		Set<EditorButtonResourcesTO> resourcesTOs = pluginRegister.getTagResourcesByGroup("Mock");
		assertEquals(1, resourcesTOs.size());

		resourcesTOs = pluginRegister.getTagResourcesByGroup("Nadpisy");
		assertEquals(4, resourcesTOs.size());

		resourcesTOs = pluginRegister.getTagResourcesByGroup("Nonexistent");
		assertTrue(resourcesTOs.isEmpty());
	}

	@Test
	public void testGetRegisteredGroups() {
		Set<String> groups = pluginRegister.getRegisteredGroups();
		assertEquals(3, groups.size());
		assertTrue(groups.contains("Nadpisy"));
		assertTrue(groups.contains("Mock"));
	}

}
