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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Downloader {

    private static UUID UniqueId;

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

    public static void convertMp4ToMp3(String inputFilePath, String outputFilePath, String startTime)
            throws IOException, InterruptedException {
        String ffmpegPath = "";
        if (Configuration.OS.contains("win")) {
            ffmpegPath = "ffDrivers/ffmpeg-master-latest-win64-gpl-shared/bin/ffmpeg.exe";
        } else if (Configuration.OS.contains("linux")) {
            ffmpegPath = "ffDrivers/ffmpeg-master-latest-linux64-gpl-shared/bin/ffmpeg";
        } else if (Configuration.OS.contains("mac")) {
            ffmpegPath = "ffDrivers/macff/ffmpeg";
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
}
