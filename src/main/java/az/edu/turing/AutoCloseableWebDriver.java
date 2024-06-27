package az.edu.turing;

import org.openqa.selenium.WebDriver;

public class AutoCloseableWebDriver implements AutoCloseable {
    private final WebDriver driver;

    public AutoCloseableWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}