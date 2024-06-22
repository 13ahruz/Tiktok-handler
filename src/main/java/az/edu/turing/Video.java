package az.edu.turing;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class Video {
    private Long videoId;
    private LocalDate shareDate;
    private long shareCount;
    private long likeCount;
    private long commentsCount;
    private long saveCount;
    private byte[] sound;
    private String profileLink;

    public Video(Long videoId, LocalDate shareDate, long shareCount, long likeCount, long commentsCount, long saveCount, byte[] sound, String profileLink) {
        this.videoId = videoId;
        this.shareDate = shareDate;
        this.shareCount = shareCount;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.saveCount = saveCount;
        this.sound = sound;
        this.profileLink = profileLink;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public LocalDate getShareDate() {
        return shareDate;
    }

    public void setShareDate(LocalDate shareDate) {
        this.shareDate = shareDate;
    }

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
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

    public byte[] getSound() {
        return sound;
    }

    public void setSound(byte[] sound) {
        this.sound = sound;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return shareCount == video.shareCount && likeCount == video.likeCount && commentsCount == video.commentsCount && saveCount == video.saveCount && Objects.equals(videoId, video.videoId) && Objects.equals(shareDate, video.shareDate) && Objects.deepEquals(sound, video.sound) && Objects.equals(profileLink, video.profileLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, shareDate, shareCount, likeCount, commentsCount, saveCount, Arrays.hashCode(sound), profileLink);
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId=" + videoId +
                ", shareDate=" + shareDate +
                ", shareCount=" + shareCount +
                ", likeCount=" + likeCount +
                ", commentsCount=" + commentsCount +
                ", saveCount=" + saveCount +
                ", sound=" + Arrays.toString(sound) +
                ", profileLink='" + profileLink + '\'' +
                '}';
    }
}
