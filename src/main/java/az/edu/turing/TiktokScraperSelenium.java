package az.edu.turing;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TiktokScraperSelenium {

    private static final String TIKTOK_VIDEO_URL = "https://www.tiktok.com/@_mariam23/video/7379809969564159274?is_from_webapp=1&sender_device=pc";
    private static List<User> users = new ArrayList<>();
    private static List<Video> videos = new ArrayList<>();
    public static Set<String> VIDEO_URL = new HashSet<>();
    public static JdbcConnect jdbcConnect = new JdbcConnect();
    //private static List<String> commentProfileUrls;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        Configuration.setDriverPath();
        ChromeOptions options = Configuration.getChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        runAll(TIKTOK_VIDEO_URL, wait, driver);
    }

    private static void setVideoAndUserDetails(ExecutorService executor, WebDriver driver, WebDriverWait wait, User user1, Video video1, String profileUrl, int commentCount, int likeCount, int saveCount, int shareCount, String uploadDate) throws InterruptedException, java.util.concurrent.ExecutionException {
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() -> {
            user1.setFollowerCount(Extractor.extractFollowersCount(profileUrl, driver, wait));
            return null;
        });
        tasks.add(() -> {
            user1.setFollowingCount(Extractor.extractFollowingCount(profileUrl, driver, wait));
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

        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private static void printResults(String username, String videoId, int shareCount, int likeCount, String uploadDate, int commentCount, int saveCount, String profileUrl, User user1) {
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
        TiktokScraperSelenium.VIDEO_URL.forEach(System.out::println);
    }

    private static void runAll (String videoURL, WebDriverWait wait, WebDriver driver){
        try {
            driver.get(videoURL);
            User user1 = new User();
            Video video1 = new Video();

            String username = Extractor.extractUsername(wait);
            String videoId = Extractor.extractVideoId(wait);
            int shareCount = Extractor.extractShareCount(wait);
            int likeCount = Extractor.extractLikeCount(wait);
            String uploadDate = Extractor.extractUploadDate(wait);
            int commentCount = Extractor.extractCommentCount(wait);
            int saveCount = Extractor.extractVideoSaveCount(wait);
            Set<String> videoUrls = Extractor.extractVideoUrls(wait);
            String profileUrl = Extractor.extractProfileLink(wait);


            for (String url : videoUrls) {
                if (!Util.videoUrls.contains(url) && !Util.users.contains(user1) && !Util.videos.contains(video1)) {

                    video1.setSoundPath(Downloader.downloadTikTokVideo("src/main/resources/", driver));

                    driver.get(url);
                    Util.videoUrls.add(url);
                    Util.users.add(user1);
                    Util.videos.add(video1);

                   try (
                       Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres", "postgres", "postgres");


                           PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO video (shareDate, shareCount, likeCount, commentsCount, saveCount, soundPath, user_id) values ( ?, ?, ?, ?, ?, ?, ?);",
                           Statement.RETURN_GENERATED_KEYS);){
                          preparedStatement.setString(1, uploadDate);
                            preparedStatement.setInt(2, shareCount);
                            preparedStatement.setInt(3, likeCount);
                            preparedStatement.setInt(4, commentCount);
                            preparedStatement.setInt(5, saveCount);
                            preparedStatement.setString(6, video1.getSoundPath());
                            preparedStatement.setLong(7, user1.getUserId());
                            preparedStatement.executeUpdate();

                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                       try(  Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres", "postgres", "postgres");
                               PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO userProfile (followerCount, postCount, followingCount, profileUrl)VALUES (?,?,?,?);")){
                           preparedStatement.setInt(1, user1.getFollowerCount());
                           preparedStatement.setInt(2, user1.getPostCount());
                           preparedStatement.setInt(3, user1.getFollowingCount());
                           preparedStatement.setString(4, user1.getProfileUrl());
                           preparedStatement.executeUpdate();
                       } catch (Exception e) {
                           e.printStackTrace();

                       }


                    System.out.println(Util.videos.size());
                    runAll(url, wait, driver);
                }

            }

            setVideoAndUserDetails(executor, driver, wait, user1, video1, profileUrl, commentCount, likeCount, saveCount, shareCount, uploadDate);

            users.add(user1);
            videos.add(video1);

            printResults(username, videoId, shareCount, likeCount, uploadDate, commentCount, saveCount, profileUrl, user1);

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
