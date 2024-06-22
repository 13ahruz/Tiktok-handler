package az.edu.turing;

import java.time.LocalDate;
import java.util.Objects;

public class Video {
    private Long videoId;
    private String shareDate;
    private int shareCount;
    private long likeCount;
    private long commentsCount;
    private long saveCount;

    public Video(Long videoId, String shareDate, int shareCount, long likeCount, long commentsCount, long saveCount) {
        this.videoId = videoId;
        this.shareDate = shareDate;
        this.shareCount = shareCount;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.saveCount = saveCount;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getShareDate() {
        return shareDate;
    }

    public void setShareDate(String shareDate) {
        this.shareDate = shareDate;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }



    public long getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(long saveCount) {
        this.saveCount = saveCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return shareCount == video.shareCount && likeCount == video.likeCount && commentsCount == video.commentsCount  && saveCount == video.saveCount && Objects.equals(videoId, video.videoId) && Objects.equals(shareDate, video.shareDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, shareDate, shareCount, likeCount, commentsCount, saveCount);
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId=" + videoId +
                ", shareDate=" + shareDate +
                ", shareCount=" + shareCount +
                ", likesCount=" + likeCount +
                ", commentsCount=" + commentsCount +
                ", savedVideoCount=" + saveCount +
                '}';
    }

}
