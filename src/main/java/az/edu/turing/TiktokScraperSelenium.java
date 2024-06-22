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

        System.setProperty("webdriver.chrome.driver", driverType);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10000));

        try {
            driver.get(TIKTOK_VIDEO_URL);

            String username = extractUsername(wait);
            String videoId = extractVideoId(wait);
            int shareCount = extractShareCount(wait);

            System.out.println("Publisher's username: " + username);
            System.out.println("Video ID: " + videoId);
            System.out.println("Share count: " + shareCount);

            List<String> usernames = extractUsernames(wait);
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

    private static String extractUsername(WebDriverWait wait) {
        try {
            WebElement usernameElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
            return usernameElement.getText();
        } catch (Exception e) {
            System.out.println("Failed to find username element");
            e.printStackTrace();
            return "";
        }
    }

    private static String extractVideoId(WebDriverWait wait) {
        try {
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'tiktok-web-player')]")));
            String id = videoElement.getAttribute("id");
            return id.split("-")[2];
        } catch (Exception e) {
            System.out.println("Failed to find video element");
            e.printStackTrace();
            return "";
        }
    }

    private static List<String> extractUsernames(WebDriverWait wait) {
        List<String> usernames = new ArrayList<>();
        try {
            List<WebElement> usernameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@class, 'css-fx1avz-StyledLink-StyledUserLinkName er1vbsz0')]")));

            for (WebElement usernameElement : usernameElements) {
                String username = usernameElement.getAttribute("href").split("@")[1];
                usernames.add(username);
                System.out.println("Found username: " + username);
            }
        } catch (Exception e) {
            System.out.println("Failed to find username elements");
            e.printStackTrace();
        }
        return usernames;
    }

    private static List<String> generateTiktokProfileLinks(List<String> usernames) {
        List<String> profileLinks = new ArrayList<>();
        String baseUrl = "https://tiktok.com/@";

        for (String username : usernames) {
            String profileLink = baseUrl + username;
            profileLinks.add(profileLink);
        }

        return profileLinks;
    }

    private static int extractShareCount(WebDriverWait wait) {
        try {
            WebElement viewCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='share-count']")));
            String viewCountText = viewCountElement.getText().trim();
            return Integer.parseInt(viewCountText);
        } catch (Exception e) {
            System.out.println("Failed to find view count element");
            e.printStackTrace();
            return -1;
        }
    }
}
