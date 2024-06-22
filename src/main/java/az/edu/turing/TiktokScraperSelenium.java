package az.edu.turing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TiktokScraperSelenium {

    private static String driverType;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@aq7in/video/7349961511852428545?is_from_webapp=1&sender_device=pc"; // Replace with actual URL
    private static List<User> users = new ArrayList<>();
    private static List<Video> videos = new ArrayList<>();

    public static void main(String[] args) {
        setDriverPath();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        User user1 = new User();
        Video video1 = new Video();

        try {
            driver.get(TIKTOK_VIDEO_URL);

            String username = extractUsername(wait);
            String videoId = extractVideoId(wait);
            int shareCount = extractShareCount(wait);
            int likeCount = extractLikeCount(wait);
            String uploadDate = extractUploadDate(wait);
            int commentCount = extractCommentCount(wait);
            int saveCount = extractVideoSaveCount(wait);
          //  int followerCount = extractFollowerCount(wait);
           // int followingCount = extractFollowingCount(wait);
           // int postCount = extractPostCount(wait);
            String profileURL = extractProfileLink(wait);

//            user1.setFollowerCount(followerCount);
//            user1.setFollowingCount(followingCount);
//            user1.setPostCount(postCount);
            user1.setProfileUrl(profileURL);

            video1.setCommentsCount(commentCount);
            video1.setLikeCount(likeCount);
            video1.setSaveCount(saveCount);
            video1.setShareCount(shareCount);
            video1.setShareDate(uploadDate);

            users.add(user1);
            videos.add(video1);


            System.out.println("Publisher's username: " + username);
            System.out.println("Video ID: " + videoId);
            System.out.println("Share count: " + shareCount);
            System.out.println("Like count: " + likeCount);
            System.out.println("Upload date: " + uploadDate);
            System.out.println("Comment count: " + commentCount);
            System.out.println("Save count: " + saveCount);
//            System.out.println("Follower Count: " + followerCount);
//            System.out.println("Following Count: " + followingCount);
//            System.out.println("Post count: " + postCount);
            System.out.println("Profile URL: " + profileURL);

            downloadTikTokVideo("src/main/resources/video.mp4", driver);

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void setDriverPath() {
        if (OS.contains("win")) {
            driverType = "drivers/chromedriver-win64/chromedriver.exe";
        } else if (OS.contains("mac")) {
            driverType = "drivers/mac/chromedriver";
        } else if (OS.contains("linux")) {
            driverType = "drivers/linux/chromedriver";
        } else {
            System.out.println("Operating system not recognized: " + OS);
            System.exit(1);
        }
        System.setProperty("webdriver.chrome.driver", driverType);
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

    private static int extractShareCount(WebDriverWait wait) {
        try {
            WebElement shareCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='share-count']")));
            String shareCountText = shareCountElement.getText().trim();
            return Integer.parseInt(shareCountText);
        } catch (Exception e) {
            System.out.println("Failed to find share count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractLikeCount(WebDriverWait wait) {
        try {
            WebElement likeCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='like-count']")));
            String likeCountText = likeCountElement.getText().trim();
            return Integer.parseInt(convertToNumber(likeCountText));
        } catch (Exception e) {
            System.out.println("Failed to find like count element");
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

    private static String convertToNumber(String likeCountText) {
        if (likeCountText.contains("K")) {
            double number = Double.parseDouble(likeCountText.replace("K", "").replace(",", "").trim()) * 1000;
            return String.valueOf((int) number);
        } else {
            return likeCountText.replace(",", "");
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

//    private static int extractFollowerCount(WebDriverWait wait) {
//        try {
//            WebElement followerCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Followers']")));
//            String followerCountText = followerCountElement.getText().trim();
//            return parseCount(followerCountText);
//        } catch (Exception e) {
//            System.out.println("Failed to find follower count element");
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    private static int extractFollowingCount(WebDriverWait wait) {
//        try {
//            WebElement followingCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Following']")));
//            String followingCountText = followingCountElement.getText().trim();
//            return parseCount(followingCountText);
//        } catch (Exception e) {
//            System.out.println("Failed to find following count element");
//            e.printStackTrace();
//            return -1;
//        }
//    }

//    private static int extractPostCount(WebDriverWait wait) {
//        try {
//            WebElement postCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@title='Likes']")));
//            String postCountText = postCountElement.getText().trim();
//            return parseCount(postCountText);
//        } catch (Exception e) {
//            System.out.println("Failed to find post count element");
//            e.printStackTrace();
//            return -1;
//        }
//    }

    private static String extractProfileLink(WebDriverWait wait) {
        try {
            WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
            String username = usernameElement.getText();
            String profileLink = "https://www.tiktok.com/@" + username;

            return profileLink;
        } catch (Exception e) {
            System.out.println("Failed to extract profile link");
            e.printStackTrace();
            return "";
        }
    }

//    private static int parseCount(String countText) {
//        countText = countText.replaceAll(",", "");
//        if (countText.endsWith("K")) {
//            return (int) (Double.parseDouble(countText.replace("K", "")) * 1000);
//        } else if (countText.endsWith("M")) {
//            return (int) (Double.parseDouble(countText.replace("M", "")) * 1000000);
//        } else {
//            return Integer.parseInt(countText);
//        }
//    }

    public static void downloadTikTokVideo(String destinationFilePath, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
            String videoSource = videoElement.getAttribute("src");

            Map<String, String> cookies = driver.manage().getCookies().stream()
                    .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie.getValue()));
            String cookieHeader = cookies.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("; "));

            List<BasicHeader> headers = driver.manage().getCookies().stream()
                    .map(cookie -> new BasicHeader("Cookie", cookie.getName() + "=" + cookie.getValue()))
                    .collect(Collectors.toList());

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(videoSource);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            httpGet.setHeader("Cookie", cookieHeader);

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (InputStream inputStream = entity.getContent();
                     FileOutputStream outputStream = new FileOutputStream(new File(destinationFilePath))) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Video downloaded successfully to " + destinationFilePath);
            }

            EntityUtils.consume(entity);
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
