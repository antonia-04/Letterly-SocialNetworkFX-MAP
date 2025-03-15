package org.example.socialnetworkfx.domain;


public class FriendshipRequest extends Entity<Long> {
    private User sender;
    private User recipient;
    private Status status; ///enum: ACCEPTED, REJECTED, PENDING;

    public FriendshipRequest(User recipient, User sender, Status status) {
        this.recipient = recipient;
        this.sender = sender;
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "FriendshipRequest{" +
                "sender=" + sender +
                ", recipient=" + recipient +
                ", status=" + status +
                '}';
    }
}
