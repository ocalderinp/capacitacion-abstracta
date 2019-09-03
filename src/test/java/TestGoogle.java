import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import sun.awt.OSInfo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class TestGoogle {

    private WebDriver driver;
    private WebElement searchInput;
    private WebDriverWait wait;
    private WebElement results;

    private By searchBarBy = By.name("q");
    private By resultsDivBy = By.id("resultStats");
    private String browser;
    private static final String WORD = "selenium";
    private static final int TIMEOUT = 5;
    private static final int WAIT = 10;

    //reportes
    protected static ExtentHtmlReporter extentHtmlReporter;
    protected static ExtentReports extentReports;
    protected static ExtentTest extentTest;

    private String HUB_URL = "http://selenium-hub:4444/wd/hub";

    @BeforeSuite(alwaysRun = true)
    public void setupReports() {
        extentHtmlReporter = new ExtentHtmlReporter("reports/reporte.html");
        extentHtmlReporter.config().setDocumentTitle("Automation Reports");
        extentHtmlReporter.config().setReportName("Reporte de Pruebas Automatizadas");
        extentHtmlReporter.config().setTheme(Theme.DARK);

        extentReports = new ExtentReports();
        extentReports.attachReporter(extentHtmlReporter);

        extentReports.setSystemInfo("Sistema Operativo", OSInfo.getOSType().name());
    }

    @BeforeTest(alwaysRun = true)
    @Parameters("browser")
    public void setup(String browser) throws MalformedURLException {
        this.browser = browser;
        if (this.browser.equalsIgnoreCase("chrome")) {
            driver = new RemoteWebDriver(new URL(HUB_URL), new ChromeOptions());
        } else if (this.browser.equalsIgnoreCase("firefox"))
            driver = new RemoteWebDriver(new URL(HUB_URL), new FirefoxOptions());
        else driver = new RemoteWebDriver(new URL(HUB_URL), new ChromeOptions());

        wait = new WebDriverWait(driver, WAIT);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        driver.get("https://www.google.com");
    }

    @Test
    public void searchTest(Method method) {
        extentTest = extentReports.createTest(method.getName() + " in " + this.browser);
        searchInput =  driver.findElement(searchBarBy);
        searchInput.sendKeys(WORD);
        extentTest.log(Status.INFO, "Searching " + WORD);
        searchInput.submit();
        wait.until(ExpectedConditions.titleIs(WORD + " - Buscar con Google"));
        results = driver.findElement(resultsDivBy);
        extentTest.log(Status.INFO, "Verifying if there are results");
        assertTrue(results.isDisplayed(), "No se encontraron resultados");
    }


    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            extentTest.log(Status.FAIL,
                    "Test Case " + result.getName() + " failed");
            extentTest.log(Status.FAIL,
                    "Caused: " + result.getThrowable());
            String screenShoot = WebDriverUtils.takeScreenShot(driver);
            extentTest.log(Status.FAIL, "Image: ");
            extentTest.addScreenCaptureFromPath(screenShoot);
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.log(Status.SKIP,
                    "Test Case " + result.getName() + " skipped");
            extentTest.log(Status.SKIP,
                    "Caused: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.log(Status.PASS,
                    "Test Case " + result.getName() + " passed");
        }
        driver.quit();
    }

    @AfterSuite(alwaysRun = true)
    public void flushReports() {
        extentReports.flush();
    }
}
