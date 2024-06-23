package az.edu.turing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TiktokScraperSelenium {

    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@_mariam23/video/7379809969564159274?is_from_webapp=1&sender_device=pc";
    private static List<User> users = new ArrayList<>();
    private static List<Video> videos = new ArrayList<>();
    private static long videoGettingCount;
    public static List<String> commentProfileUrls;

    public static void main(String[] args) {
        Configure.setDriverPath();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        User user1 = new User();
        Video video1 = new Video();

        try {
            driver.get(TIKTOK_VIDEO_URL);

            String username = Configure.extractUsername(wait);
            String videoId = Configure.extractVideoId(wait);
            int shareCount = Configure.extractShareCount(wait);
            int likeCount = Configure.extractLikeCount(wait);
            String uploadDate = Configure.extractUploadDate(wait);
            int commentCount = Configure.extractCommentCount(wait);
            int saveCount = Configure.extractVideoSaveCount(wait);
            commentProfileUrls = Configure.generateTiktokProfileLinks(Configure.extractUsernames(wait));
            String profileUrl = Configure.extractProfileLink(wait);

            video1.setSoundPath(Configure.downloadTikTokVideo("src/main/resources/", driver));

            // Create a list of tasks to execute concurrently
            List<Callable<Void>> tasks = new ArrayList<>();
            tasks.add(() -> {
                user1.setFollowerCount(Configure.extractFollowersCount(profileUrl, driver, wait));
                return null;
            });
            tasks.add(() -> {
                user1.setFollowingCount(Configure.extractFollowingCount(profileUrl, driver, wait));
                return null;
            });
            tasks.add(() -> {
                user1.setProfileUrl(profileUrl);
                return null;
            });
            tasks.add(() -> {
                video1.setCommentsCount(commentCount);
                return null;
            });
            tasks.add(() -> {
                video1.setLikeCount(likeCount);
                return null;
            });
            tasks.add(() -> {
                video1.setSaveCount(saveCount);
                return null;
            });
            tasks.add(() -> {
                video1.setShareCount(shareCount);
                return null;
            });
            tasks.add(() -> {
                video1.setShareDate(uploadDate);
                return null;
            });

            // Execute tasks concurrently
            List<Future<Void>> futures = executor.invokeAll(tasks);

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                future.get();
            }

            users.add(user1);
            videos.add(video1);

            System.out.println("Publisher's username: " + username);
            System.out.println("Video ID: " + videoId);
            System.out.println("Share count: " + shareCount);
            System.out.println("Like count: " + likeCount);
            System.out.println("Upload date: " + uploadDate);
            System.out.println("Comment count: " + commentCount);
            System.out.println("Save count: " + saveCount);
            System.out.println("Profile URL: " + profileUrl);
            System.out.println("Follower Count: " + user1.getFollowerCount());
            System.out.println("Following Count: " + user1.getFollowingCount());
            commentProfileUrls.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
            executor.shutdown();
        }
    }
}