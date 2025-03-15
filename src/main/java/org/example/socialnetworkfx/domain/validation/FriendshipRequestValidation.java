package org.example.socialnetworkfx.domain.validation;

import org.example.socialnetworkfx.domain.FriendshipRequest;
import org.example.socialnetworkfx.domain.Status;
import org.example.socialnetworkfx.domain.User;

public class FriendshipRequestValidation implements Validation<FriendshipRequest> {

    @Override
    public void validate(FriendshipRequest request) throws ValidationException {
        if (request == null) {
            throw new ValidationException("FriendshipRequest cannot be null.");
        }

        User sender = request.getSender();
        User recipient = request.getRecipient();
        Status status = request.getStatus();

        if (sender == null) {
            throw new ValidationException("Sender cannot be null.");
        }

        if (recipient == null) {
            throw new ValidationException("Recipient cannot be null.");
        }

        if (sender.getID().equals(recipient.getID())) {
            throw new ValidationException("A user cannot send a friendship request to themselves.");
        }

        if (status == null) {
            throw new ValidationException("Status of friendship request must be set.");
        }

        if (status != Status.PENDING && status != Status.ACCEPTED && status != Status.REJECTED) {
            throw new ValidationException("Status must be one of: PENDING, ACCEPTED, REJECTED.");
        }
    }
}
