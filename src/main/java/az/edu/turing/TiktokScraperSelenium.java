package az.edu.turing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TiktokScraperSelenium {

    private static String driverType;
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@aq7in/video/7349961511852428545?is_from_webapp=1&sender_device=pc"; // Replace with actual URL

    public static void main(String[] args) {
        if (OS.contains("win")) {
            driverType = "drivers/chromedriver-win64/chromedriver.exe";
        } else if (OS.contains("mac")) {
            driverType = "drivers/mac/chromedriver";
        } else if (OS.contains("linux")) {
            driverType = "drivers/linux/chromedriver";
        } else {
            System.out.println("Operating system not recognized: " + OS);
        }

        // Set the path to the chromedriver executable
        System.setProperty("webdriver.chrome.driver", driverType);

        // Configure Chrome options to run in headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode
        options.addArguments("window-size=1920,1080"); // Set window size

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            driver.get(TIKTOK_VIDEO_URL);

            String username = extractUsername(driver, wait);
            String videoId = extractVideoId(driver, wait);

            System.out.println("Username: " + username);
            System.out.println("Video ID: " + videoId);

            List<String> usernames = extractUsernames(driver, wait);
            List<String> profileLinks = generateTiktokProfileLinks(usernames);

            for (String profileLink : profileLinks) {
                System.out.println("Profile Link: " + profileLink);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static String extractUsername(WebDriver driver, WebDriverWait wait) {
        try {
            // Wait and find the element that contains the username
            WebElement usernameElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
            System.out.println("Username Element Found: " + usernameElement.getText()); // Debug statement
            return usernameElement.getText();
        } catch (Exception e) {
            System.out.println("Failed to find username element");
            e.printStackTrace();
            return "";
        }
    }

    private static String extractVideoId(WebDriver driver, WebDriverWait wait) {
        try {
            // Wait and find the element that contains the video ID
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'tiktok-web-player')]")));
            String id = videoElement.getAttribute("id");
            System.out.println("Video Element ID: " + id); // Debug statement
            return id.split("-")[2];
        } catch (Exception e) {
            System.out.println("Failed to find video element");
            e.printStackTrace();
            return "";
        }
    }

    private static List<String> extractUsernames(WebDriver driver, WebDriverWait wait) {
        List<String> usernames = new ArrayList<>();
        try {
            // Wait and find the elements that contain the usernames
            List<WebElement> usernameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@class, 'StyledUserLinkName')]")));

            for (WebElement usernameElement : usernameElements) {
                usernames.add(usernameElement.getText());
                System.out.println("Found username: " + usernameElement.getText()); // Debug statement
            }
        } catch (Exception e) {
            System.out.println("Failed to find username elements");
            e.printStackTrace();
        }
        return usernames;
    }

    private static List<String> generateTiktokProfileLinks(List<String> usernames) {
        List<String> profileLinks = new ArrayList<>();
        String baseUrl = "https://tiktok.com/";

        for (String username : usernames) {
            String profileLink = baseUrl + username;
            profileLinks.add(profileLink);
        }

        return profileLinks;
    }
}
