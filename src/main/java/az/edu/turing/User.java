package az.edu.turing;

import java.util.Objects;

public class User {
    private Long userId;
    private int followerCount;
    private int postCount;
    private int followingCount;
    private static long MaxID = 0;
    private String profileUrl;

    public User(int followerCount, int postCount, int followingCount, String profileUrl) {
        this.userId = ++MaxID;
        this.followerCount = followerCount;
        this.postCount = postCount;
        this.followingCount = followingCount;
        this.profileUrl = profileUrl;
    }

    public User (){
        this.userId = ++MaxID;
    }

    public Long getUserId() {
        return userId;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return followerCount == user.followerCount && postCount == user.postCount && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, followerCount, postCount);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", follower=" + followerCount +
                ", posts=" + postCount +
                '}';
    }
}
