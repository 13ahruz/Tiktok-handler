package az.edu.turing;

import java.util.Objects;

public class User {
    private Long userId;
    private int followerCount;
    private int postCount;

    public User(Long userId, int followerCount, int postCount) {
        this.userId = userId;
        this.followerCount = followerCount;
        this.postCount = postCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
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
