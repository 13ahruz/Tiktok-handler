package az.edu.turing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.concurrent.TimeUnit;

public class TiktokScraperSelenium {

    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@aq7in/video/7349961511852428545?is_from_webapp=1&sender_device=pc"; // Replace with actual URL

    public static void main(String[] args) {
        // Set the path to the chromedriver executable
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\ROMedia\\Downloads\\chromedriver-win64\\chromedriver.exe");

        // Configure Chrome options to run in headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode
        options.addArguments("window-size=1920,1080"); // Set window size

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(TIKTOK_VIDEO_URL);

            // Extract data
            String id = extractVideoId(driver);
            String date = extractDate(driver);
            String sharer = extractSharer(driver);

            System.out.println("ID: " + id);
            System.out.println("Date: " + date);
            System.out.println("Sharer: " + sharer);
        } finally {
            driver.quit();
        }
    }

    private static String extractVideoId(WebDriver driver) {
        // Example of extracting video ID
        WebElement videoIdElement = driver.findElement(By.cssSelector("meta[property='og:video']"));
        return videoIdElement != null ? videoIdElement.getAttribute("content") : "N/A";
    }

    private static String extractDate(WebDriver driver) {
        // Example of extracting upload date
        WebElement dateElement = driver.findElement(By.cssSelector("meta[property='og:video:release_date']"));
        return dateElement != null ? dateElement.getAttribute("content") : "N/A";
    }

    private static String extractSharer(WebDriver driver) {
        // Example of extracting sharer
        WebElement sharerElement = driver.findElement(By.cssSelector("meta[property='og:title']"));
        return sharerElement != null ? sharerElement.getAttribute("content") : "N/A";
    }
}
