package cz.gattserver.grass3.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import cz.gattserver.grass3.mock.MockConfiguration;
import cz.gattserver.grass3.test.AbstractDBUnitTest;

@DatabaseSetup(value = "deleteAll.xml", type = DatabaseOperation.DELETE_ALL)
@WebAppConfiguration
public class ConfigurationServiceTest extends AbstractDBUnitTest {

	@Autowired
	private ConfigurationService configurationService;

	@Test
	public void testSaveConfiguration() {
		MockConfiguration mc = new MockConfiguration("mockConfiguration");
		mc.setConfigValue("mockConfigValue");
		mc.setConfigValue2("mockConfigValue2");
		mc.setNonConfigValue("mockNonConfigValue");
		configurationService.saveConfiguration(mc);

		MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
		toLoadMc.setConfigValue("defaultMockConfigValue");
		configurationService.loadConfiguration(toLoadMc);

		assertEquals("mockConfigValue", toLoadMc.getConfigValue());
		assertEquals("mockConfigValue2", toLoadMc.getConfigValue2());
		assertNull(toLoadMc.getNonConfigValue());
	}

	@Test
	public void testSaveConfiguration2() {
		MockConfiguration mc = new MockConfiguration("mockConfiguration");
		mc.setConfigValue("mockConfigValue");
		mc.setConfigValue2("mockConfigValue2");
		mc.setNonConfigValue("mockNonConfigValue");
		configurationService.saveConfiguration(mc);

		MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
		toLoadMc.setConfigValue("defaultMockConfigValue");
		configurationService.loadConfiguration(toLoadMc);

		assertEquals("mockConfigValue", toLoadMc.getConfigValue());
		assertEquals("mockConfigValue2", toLoadMc.getConfigValue2());
		assertNull(toLoadMc.getNonConfigValue());

		mc = new MockConfiguration("mockConfiguration");
		mc.setConfigValue("mockConfigValueChanged");
		configurationService.saveConfiguration(mc);

		toLoadMc = new MockConfiguration("mockConfiguration");
		toLoadMc.setConfigValue("defaultMockConfigValue");
		toLoadMc.setConfigValue2("defaultMockConfigValue2");
		configurationService.loadConfiguration(toLoadMc);

		assertEquals("mockConfigValueChanged", toLoadMc.getConfigValue());
		assertNull(toLoadMc.getConfigValue2());
		assertNull(toLoadMc.getNonConfigValue());
	}

	@Test(expected = NullPointerException.class)
	public void testSaveConfiguration_fail() {
		configurationService.saveConfiguration(null);
	}

	@Test
	public void testLoadConfiguration() {
		MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
		toLoadMc.setConfigValue("defaultMockConfigValue");
		configurationService.loadConfiguration(toLoadMc);

		assertEquals("defaultMockConfigValue", toLoadMc.getConfigValue());
		assertNull(toLoadMc.getNonConfigValue());
	}

	@Test(expected = NullPointerException.class)
	public void testLoadConfiguration_fail() {
		configurationService.loadConfiguration(null);
	}

}
