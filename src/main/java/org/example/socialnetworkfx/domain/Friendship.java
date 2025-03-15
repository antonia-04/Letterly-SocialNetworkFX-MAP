package org.example.socialnetworkfx.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Long> {
    private User firstFriend;
    private User secondFriend;
    private final LocalDateTime friendsFrom;

    public Friendship(User firstFriend, User secondFriend, LocalDateTime friendsFrom) {
        this.firstFriend = firstFriend;
        this.secondFriend = secondFriend;
        this.friendsFrom = friendsFrom;
    }

    public User getFirstFriend() {
        return firstFriend;
    }

    public void setFirstFriend(User firstFriend) {
        this.firstFriend = firstFriend;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public User getSecondFriend() {
        return secondFriend;
    }

    public void setSecondFriend(User secondFriend) {
        this.secondFriend = secondFriend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(firstFriend, that.firstFriend) && Objects.equals(secondFriend, that.secondFriend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstFriend, secondFriend);
    }
}
