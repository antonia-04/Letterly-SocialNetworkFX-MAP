package org.example.socialnetworkfx.domain.validation;

import org.example.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.domain.User;


public class FriendshipValidation  implements Validation<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Friendship cannot be null.");
        }

        User firstFriend = entity.getFirstFriend();
        User secondFriend = entity.getSecondFriend();

        if (firstFriend == null || secondFriend == null) {
            throw new ValidationException("The users involved in the friendship cannot be null.");
        }

        if (firstFriend.getID().equals(secondFriend.getID())) {
            throw new ValidationException("A user cannot be friends with themselves.");
        }
    }
}