package cz.gattserver.grass3.web;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.server.SeleniumServer;

@Ignore
public class WebTest {

	private static final String APP_BASE_URL = "http://localhost:8180/web/home";

	private WebDriver driver;

	@Before
	public void setUp() throws Exception {
		StandaloneConfiguration configuration = new StandaloneConfiguration();
		SeleniumServer seleniumServer = new SeleniumServer(configuration);
		seleniumServer.boot();

		// capability.setPlatform(Platform.LINUX);
		// capability.setPlatform(Platform.WINDOWS);
		ChromeOptions chromeOptions = new ChromeOptions();
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\Hynek\\workspace\\moje\\eclipse neon\\grass3-vaadin-osgi\\modules\\core\\src\\test\\resources\\selenium\\chromedriver.exe");
		driver = new ChromeDriver(chromeOptions);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	}

	@Test
	public void test() throws Exception {
		// otevrit base page
		driver.get(APP_BASE_URL);

		// prihlasit
		// WebElement usernameElement =
		// driver.findElement(By.id("j_username")).findElement(By.cssSelector("input[type=\"text\"]"));
		// usernameElement.sendKeys("167474");
		// WebElement passwordElement =
		// driver.findElement(By.id("j_password")).findElement(By.cssSelector("input[type=\"password\"]"));
		// passwordElement.sendKeys("1234");
		// driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();

		// driver.get(APP_BASE_URL + "/mvcr-oe/login/");
		// driver.findElement(By.id("j_username")).clear();
		// driver.findElement(By.id("j_username")).sendKeys("167474");
		// driver.findElement(By.id("j_password")).clear();
		// driver.findElement(By.id("j_password")).sendKeys("1234");
		// driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();

		// WebElement ucokrElement =
		// driver.findElement(By.id("ucokrField")).findElement(By.cssSelector("input[type=\"text\"]"));
		// ucokrElement.sendKeys("2200");
		// ucokrElement.sendKeys(Keys.ENTER);

		// driver.findElement(By.id("searchButton")).click();

		Thread.sleep(10000);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

}
