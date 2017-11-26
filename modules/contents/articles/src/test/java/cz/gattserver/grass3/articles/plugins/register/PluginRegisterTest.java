package cz.gattserver.grass3.articles.plugins.register;

import static org.junit.Assert.*;

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
	private PluginRegister pluginRegister;

	@Test
	public void testGetRegisteredTags() {
		assertEquals(5, pluginRegister.getRegisteredTags().size());
		assertEquals("[N1, N2, N3, N4, MOCK_TAG]", pluginRegister.getRegisteredTags().toString());
	}

	@Test
	public void testGet() {
		Plugin mockTag = pluginRegister.get("MOCK_TAG");
		assertNotNull(mockTag);
		assertEquals("MOCK_TAG", mockTag.getTag());
		assertEquals(MockPlugin.class, mockTag.getClass());
		assertEquals(MockParser.class, mockTag.getParser().getClass());

		assertNull(pluginRegister.get("NONEXISTENT_TAG"));
	}

	@Test
	public void testGetGroupTags() {
		Set<EditorButtonResourcesTO> resourcesTOs = pluginRegister.getGroupTags("Mock");
		assertEquals(1, resourcesTOs.size());

		resourcesTOs = pluginRegister.getGroupTags("Nadpisy");
		assertEquals(4, resourcesTOs.size());

		resourcesTOs = pluginRegister.getGroupTags("Nonexistent");
		assertTrue(resourcesTOs.isEmpty());
	}

	@Test
	public void testGetRegisteredGroups() {
		Set<String> groups = pluginRegister.getRegisteredGroups();
		assertEquals(2, groups.size());
		assertTrue(groups.contains("Nadpisy"));
		assertTrue(groups.contains("Mock"));
	}

	@Test
	public void testIsRegistered() {
		assertTrue(pluginRegister.isRegistered("MOCK_TAG"));
		assertFalse(pluginRegister.isRegistered("NONEXISTENT_TAG"));
	}

}
