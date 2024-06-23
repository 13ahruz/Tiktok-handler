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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class TiktokScraperSelenium {

    private static String driverType;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@_mariam23/video/7379809969564159274?is_from_webapp=1&sender_device=pc";
    private static List<User> users = new ArrayList<>();
    private static List<Video> videos = new ArrayList<>();
    private static UUID UniqueId;
    private static long videoGettingCount;
    public static List<String> commentProfileUrls;

    public static void main(String[] args) {
        setDriverPath();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
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
            commentProfileUrls = generateTiktokProfileLinks(extractUsernames(wait));
            String profileUrl = extractProfileLink(wait);

            video1.soundPath = downloadTikTokVideo("src/main/resources/", driver);

            int followerCount = extractFollowersCount(profileUrl, driver, wait);
            int followingCount = extractFollowingCount(profileUrl, driver, wait);

            user1.setFollowerCount(followerCount);
            user1.setFollowingCount(followingCount);
            user1.setProfileUrl(profileUrl);

            video1.setCommentsCount(commentCount);
            video1.setLikeCount(likeCount);
            video1.setSaveCount(saveCount);
            video1.setShareCount(shareCount);
            video1.setShareDate(uploadDate);

            users.add(user1);
            videos.add(video1);

            commentProfileUrls.forEach(System.out::println);
            System.out.println(extractLastVideoFromProfile(commentProfileUrls.get(0), driver, wait));


            System.out.println("Publisher's username: " + username);
            System.out.println("Video ID: " + videoId);
            System.out.println("Share count: " + shareCount);
            System.out.println("Like count: " + likeCount);
            System.out.println("Upload date: " + uploadDate);
            System.out.println("Comment count: " + commentCount);
            System.out.println("Save count: " + saveCount);
            System.out.println("Profile URL: " + profileUrl);
            System.out.println("Follower Count: " + followerCount);
            System.out.println("Following Count: " + followingCount);

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
            return Integer.parseInt(convertToNumber(shareCountText));
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
            return Integer.parseInt(convertToNumber(commentCountText));
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
            return Integer.parseInt(convertToNumber(savedVideoCountText));
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
        } else if (likeCountText.contains("M")) {
            double number = Double.parseDouble(likeCountText.replace("M", "").replace(",", "").trim()) * 1000000;
            return String.valueOf((int) number);
        } else {
            return likeCountText.replace(",", "");
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
            String profileLink = "https://www.tiktok.com/@" + username;

            return profileLink;
        } catch (Exception e) {
            System.out.println("Failed to extract profile link");
            e.printStackTrace();
            return "";
        }
    }

    public static String downloadTikTokVideo(String destinationFilePathForVideo, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
            String videoSource = videoElement.getAttribute("src");

            Map<String, String> cookies = driver.manage().getCookies().stream()
                    .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie.getValue(), (oldValue, newValue) -> newValue)); // Handle duplicate keys
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
                UniqueId = UUID.randomUUID();
                String uniqueFileName = destinationFilePathForVideo + "video_" + UniqueId + ".mp4";
                try (InputStream inputStream = entity.getContent();
                     FileOutputStream outputStream = new FileOutputStream(new File(uniqueFileName))) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Video downloaded successfully to " + uniqueFileName);
                convertMp4ToMp3(uniqueFileName, "src/main/resources/sounds/sound_" + UniqueId + ".mp3", "00:00:00");
                Files.delete(Path.of(uniqueFileName));
                return "src/main/resources/sounds/sound_" + UniqueId + ".mp3";
            }

            EntityUtils.consume(entity);
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int extractFollowersCount(String profileUrl, WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(profileUrl);
            WebElement followersCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='followers-count']")));
            String followersCountText = followersCountElement.getText().trim();
            return Integer.parseInt(convertToNumber(followersCountText));
        } catch (Exception e) {
            System.out.println("Failed to find followers count element");
            e.printStackTrace();
            return -1;
        }
    }

    private static int extractFollowingCount(String profileUrl, WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(profileUrl);
            WebElement followingCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='following-count']")));
            String followingCountText = followingCountElement.getText().trim();
            return Integer.parseInt(convertToNumber(followingCountText));
        } catch (Exception e) {
            System.out.println("Failed to find following count element");
            e.printStackTrace();
            return -1;
        }
    }

    //TEST

    public static void convertMp4ToMp3(String inputFilePath, String outputFilePath, String startTime)
            throws IOException, InterruptedException {
        String ffmpegPath="";
        if (OS.contains("win")) {
            ffmpegPath="ffDrivers/ffmpeg-master-latest-win64-gpl-shared/bin/ffmpeg.exe";
        } else if (OS.contains("linux")) {
            ffmpegPath="ffDrivers/ffmpeg-master-latest-linux64-gpl-shared/bin/ffmpeg";
        } else if (OS.contains("mac")) {
            ffmpegPath="ffDrivers/macff/ffmpeg";
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath, "-i", inputFilePath, "-vn", "-ss", startTime, "-acodec", "libmp3lame", outputFilePath);

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Audio extracted successfully.");
        } else {
            System.out.println("Error extracting audio. Exit code: " + exitCode);
        }
    }

    private static List<String> extractUsernames(WebDriverWait wait) {
        List<String> usernames = new ArrayList<>();
        try {
            List<WebElement> usernameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@class, 'css-fx1avz-StyledLink-StyledUserLinkName')]")));

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

    private static String extractLastVideoFromProfile (String profileUrl, WebDriver driver, WebDriverWait wait){
        driver.get("https://www.tiktok.com/@asousa808");

        List<WebElement> videoElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@data-e2e='user-post-item']//a")));

        if (!videoElements.isEmpty()) {

            String firstVideoUrl = videoElements.get(0).getAttribute("href");
            return firstVideoUrl;

        } else {
            System.out.println("No videos found on the profile.");
            return "";
        }
    }
}
