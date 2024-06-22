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

import java.io.*;
import java.time.Duration;
import java.util.*;
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
            String profileURL = extractProfileLink(wait);

            user1.setProfileUrl(profileURL);
            int followersCount = extractFollowersCount(profileURL, driver, wait);
            int followingCount = extractFollowingCount(profileURL, driver, wait);
            user1.setFollowerCount(followersCount);
            user1.setFollowingCount(followingCount);

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
            System.out.println("Profile URL: " + profileURL);
            System.out.println("Followers count: " + followersCount);
            System.out.println("Following count: " + followingCount);

            downloadTikTokVideo(TIKTOK_VIDEO_URL, "src/main/resources/", "src/main/resources/sounds/", driver);

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

    private static String convertToNumber(String text) {
        if (text.contains("K")) {
            double number = Double.parseDouble(text.replace("K", "").trim()) * 1000;
            return String.valueOf((int) number);
        } else if (text.contains("M")) {
            double number = Double.parseDouble(text.replace("M", "").trim()) * 1000000;
            return String.valueOf((int) number);
        } else {
            return text.replace(",", "");
        }
    }

    private static String extractUploadDate(WebDriverWait wait) {
        try {
            WebElement uploadDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), '-')]")));
            return uploadDateElement.getText();
        } catch (Exception e) {
            System.out.println("Failed to extract upload date");
            e.printStackTrace();
            return "";
        }
    }

    private static String extractProfileLink(WebDriverWait wait) {
        try {
            WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
            String username = usernameElement.getText();
            return "https://www.tiktok.com/@" + username;
        } catch (Exception e) {
            System.out.println("Failed to extract profile link");
            e.printStackTrace();
            return "";
        }
    }

    private static int extractFollowersCount(String profileURL, WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(profileURL);
            WebElement followersElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='followers-count']")));
            String followersText = followersElement.getText().trim();
            return Integer.parseInt(convertToNumber(followersText));
        } catch (Exception e) {
            System.out.println("Failed to extract followers count");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractFollowingCount(String profileURL, WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(profileURL);
            WebElement followingElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='following-count']")));
            String followingText = followingElement.getText().trim();
            return Integer.parseInt(convertToNumber(followingText));
        } catch (Exception e) {
            System.out.println("Failed to extract following count");
            e.printStackTrace();
            return -1;
        }
    }

    public static void downloadTikTokVideo(String videoUrl, String destinationFilePathForVideo, String destinationFilePathForAudio, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
            String videoSourceUrl = videoElement.getAttribute("src");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(videoSourceUrl);
            httpGet.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0"));

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (InputStream inputStream = entity.getContent();
                     OutputStream outputStream = new FileOutputStream(new File(destinationFilePathForVideo + "video.mp4"))) {
                    int read;
                    byte[] buffer = new byte[4096];
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                }


                File videoFile = new File(destinationFilePathForVideo + "video.mp4");
                File audioFile = new File(destinationFilePathForAudio + "audio.mp3");
                String command = String.format("ffmpeg -i %s -q:a 0 -map a %s", videoFile.getAbsolutePath(), audioFile.getAbsolutePath());

                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();

                System.out.println("Download completed successfully.");
            } else {
                System.out.println("Failed to download the video.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during video download.");
            e.printStackTrace();
        }
    }
}
