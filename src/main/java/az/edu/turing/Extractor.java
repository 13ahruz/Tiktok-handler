package az.edu.turing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Extractor {

    public static String extractUsername(WebDriverWait wait) {
        WebElement usernameElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
        return usernameElement.getText();
    }

    public static String extractVideoId(WebDriverWait wait) {
        WebElement videoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'tiktok-web-player')]")));
        String id = videoElement.getAttribute("id");
        return id.split("-")[2];
    }

    public static int extractShareCount(WebDriverWait wait) {
        WebElement shareCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='share-count']")));
        String shareCountText = shareCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(shareCountText));
    }

    public static int extractLikeCount(WebDriverWait wait) {
        WebElement likeCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='like-count']")));
        String likeCountText = likeCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(likeCountText));
    }

    public static int extractCommentCount(WebDriverWait wait) {
        WebElement commentCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='comment-count']")));
        String commentCountText = commentCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(commentCountText));
    }

    public static int extractVideoSaveCount(WebDriverWait wait) {
        WebElement savedVideoCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='undefined-count']")));
        String savedVideoCountText = savedVideoCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(savedVideoCountText));
    }

    public static String extractUploadDate(WebDriverWait wait) {
        WebElement uploadDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), '-')]")));
        return uploadDateElement.getText();
    }

    public static String extractProfileLink(WebDriverWait wait) {
        WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@data-e2e='browse-username']")));
        String username = usernameElement.getText();
        return "https://www.tiktok.com/@" + username;
    }

    public static int extractFollowersCount(String profileUrl, WebDriver driver, WebDriverWait wait) {
        driver.get(profileUrl);
        WebElement followersCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='followers-count']")));
        String followersCountText = followersCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(followersCountText));
    }

    public static int extractFollowingCount(String profileUrl, WebDriver driver, WebDriverWait wait) {
        driver.get(profileUrl);
        WebElement followingCountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//strong[@data-e2e='following-count']")));
        String followingCountText = followingCountElement.getText().trim();
        return Integer.parseInt(convertToNumber(followingCountText));
    }

    public static List<String> extractUsernames(WebDriverWait wait) {
        List<WebElement> usernameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@class, 'css-fx1avz-StyledLink-StyledUserLinkName')]")));
        return usernameElements.stream().map(e -> e.getAttribute("href").split("@")[1]).collect(Collectors.toList());
    }

    public static List<String> generateTiktokProfileLinks(List<String> usernames) {
        String baseUrl = "https://tiktok.com/@";
        return usernames.stream().map(username -> baseUrl + username).collect(Collectors.toList());
    }

    private static String convertToNumber(String text) {
        if (text.contains("K")) {
            double number = Double.parseDouble(text.replace("K", "").replace(",", "").trim()) * 1000;
            return String.valueOf((int) number);
        } else if (text.contains("M")) {
            double number = Double.parseDouble(text.replace("M", "").replace(",", "").trim()) * 1000000;
            return String.valueOf((int) number);
        } else {
            return text.replace(",", "");
        }
    }
}
