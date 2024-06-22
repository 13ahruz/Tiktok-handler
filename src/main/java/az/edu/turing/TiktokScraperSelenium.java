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
            return;
        }

        System.setProperty("webdriver.chrome.driver", driverType);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Reduced wait time

        try {
            driver.get(TIKTOK_VIDEO_URL);

            String username = extractUsername(wait);
            Long videoId = extractVideoId(wait);
            int shareCount = extractShareCount(wait);
            int commentCount = extractCommentCount(wait);
            int videoSaveCount = extractVideoSaveCount(wait);

            System.out.println("Publisher's username: " + username);
            System.out.println("Video ID: " + videoId);
            System.out.println("Share count: " + shareCount);
            System.out.println("Comment count: " + commentCount);
            System.out.println("Video save count: " + videoSaveCount);

            List<String> usernames = extractUsernames(wait);
            List<String> profileLinks = generateTiktokProfileLinks(usernames);

            List<User> users = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (String profileLink : profileLinks) {
                driver.get(profileLink);

                String profileUsername = extractProfileUsername(wait);
                int followerCount = extractFollowerCount(wait);
                int followingCount = extractFollowingCount(wait);
                int postCount = extractPostCount(wait);

                User user = new User(generateUserId(profileUsername), followerCount, postCount);
                users.add(user);
                Video video = new Video(extractVideoId(wait), extractUploadDate(wait), extractShareCount(wait),1,extractCommentCount(wait),extractVideoSaveCount(wait));
                users.add(user);

                System.out.println("Profile Username: " + profileUsername);
                System.out.println("Follower Count: " + followerCount);
                System.out.println("Following Count: " + followingCount);
                System.out.println("Post Count: " + postCount);
            }

            for (User user : users) {
                System.out.println(user);
            }
            for (Video video : videos) {
                System.out.println(video);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    private static String extractUploadDate( WebDriverWait wait) {
        try {
            WebElement uploadDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), '-')]")));
            return uploadDateElement.getText();
        } catch (Exception e) {
            System.out.println("Failed to extract upload date");
            e.printStackTrace();
            return "";
}
}

    private static Long generateUserId(String profileUsername) {
        // Generate a unique user ID based on username or other criteria
        return (long) profileUsername.hashCode(); // Example hash-based ID generation
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

    private static Long extractVideoId(WebDriverWait wait) {
        try {
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'tiktok-web-player')]")));
            String id = videoElement.getAttribute("id");
            return Long.parseLong(id.split("-")[2]);
        } catch (Exception e) {
            System.out.println("Failed to find video element");
            e.printStackTrace();
            return null;
        }
    }


    private static List<String> extractUsernames(WebDriverWait wait) {
        List<String> usernames = new ArrayList<>();
        try {
            List<WebElement> usernameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@class, 'css-fx1avz-StyledLink-StyledUserLinkName er1vbsz0')]")));

            for (WebElement usernameElement : usernameElements) {
                String username = usernameElement.getAttribute("href").split("@")[1];
                usernames.add(username);
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

    private static int extractCommentCount(WebDriverWait wait) {
        try {
            WebElement commentCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='comment-count']")));
            String commentCountText = commentCountElement.getText().trim();
            return Integer.parseInt(commentCountText);
        } catch (Exception e) {
            System.out.println("Failed to find comment count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractVideoSaveCount(WebDriverWait wait) {
        try {
            WebElement savedVideoCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='undefined-count']")));
            String savedVideoCountText = savedVideoCountElement.getText().trim();
            return Integer.parseInt(savedVideoCountText);
        } catch (Exception e) {
            System.out.println("Failed to find saved video count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static String extractProfileUsername(WebDriverWait wait) {
        try {
            WebElement usernameElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@data-e2e='user-title']")));
            return usernameElement.getText();
        } catch (Exception e) {
            System.out.println("Failed to find profile username element");
            e.printStackTrace();
            return "";
        }
    }

    private static int extractFollowerCount(WebDriverWait wait) {
        try {
            WebElement followerCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Followers']")));
            String followerCountText = followerCountElement.getText().trim();
            return parseCount(followerCountText);
        } catch (Exception e) {
            System.out.println("Failed to find follower count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractFollowingCount(WebDriverWait wait) {
        try {
            WebElement followingCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Following']")));
            String followingCountText = followingCountElement.getText().trim();
            return parseCount(followingCountText);
        } catch (Exception e) {
            System.out.println("Failed to find following count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractPostCount(WebDriverWait wait) {
        try {
            WebElement postCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Likes']")));
            String postCountText = postCountElement.getText().trim();
            return parseCount(postCountText);
        } catch (Exception e) {
            System.out.println("Failed to find post count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int parseCount(String countText) {
        countText = countText.replaceAll(",", "");
        if (countText.endsWith("K")) {
            return (int) (Double.parseDouble(countText.replace("K", "")) * 1000);
        } else if (countText.endsWith("M")) {
            return (int) (Double.parseDouble(countText.replace("M", "")) * 1000000);
        } else {
            return Integer.parseInt(countText);
        }
    }
}
