package org.example.socialnetworkfx.service;

import javafx.util.Pair;
import org.example.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.domain.validation.ValidationException;
import org.example.socialnetworkfx.repository.AbstractDBRepository;
import org.example.socialnetworkfx.repository.FriendshipDBRepository;
import org.example.socialnetworkfx.repository.UserDBRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserService implements Service<User> {
    private final UserDBRepository repository;
    private final AbstractDBRepository<Long, Friendship> friendshipRepository;

    public UserService(UserDBRepository repository, AbstractDBRepository<Long, Friendship> friendshipRepository) {
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Long generateID() {
        return -1L;
    }

    @Override
    public Optional<User> delete(Long ID) {
        try {
            Optional<User> user = repository.findOne(ID);
            if (user.isPresent()) {
                Iterable<Friendship> friendships = friendshipRepository.findAll();
                friendships.forEach(friendship -> {
                    if (friendship.getFirstFriend().getID().equals(ID) || friendship.getSecondFriend().getID().equals(ID)) {
                        friendshipRepository.delete(friendship.getID());
                    }
                });
                return repository.delete(ID);
            }
            return Optional.empty();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete user with ID " + ID + ": " + e.getMessage(), e);
        }
    }

    public Optional<User> save(String firstName, String lastName, String username, String password, String cryptedP) {
        try {
            Long newID = generateID();
            User newUser = new User(firstName, lastName, username, password, cryptedP);
            newUser.setID(newID);
            return repository.save(newUser);
        } catch (ValidationException e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public Optional<User> update(Long ID, String firstName, String lastName, String username, String password, String cryptedP) {
        try {
            User toBeUpdated = new User(firstName, lastName, username, password, cryptedP);
            toBeUpdated.setID(ID);
            return repository.update(toBeUpdated);
        } catch (ValidationException e) {
            throw new RuntimeException("Failed to update user with ID " + ID + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Iterable<User> findAll() {
        return repository.findAll();
    }

    //not used
    public ArrayList<User> getFriends(Long ID) {
        ArrayList<User> friends = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship friendship : friendships) {
            if (friendship.getFirstFriend().getID().equals(ID)) {
                friends.add(friendship.getSecondFriend());
            } else if (friendship.getSecondFriend().getID().equals(ID)) {
                friends.add(friendship.getFirstFriend());
            }
        }
        return friends;
    }

    public Optional<User> findOne(Long ID) {
        try {
            return repository.findOne(ID);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to find user with ID " + ID + ": " + e.getMessage(), e);
        }
    }

    public User findUser(String username) throws SQLException {
        return repository.findUser(username).orElseThrow(() -> new SQLException("User with username " + username + " not found."));
    }

    // the provided password is hashed and compared to the hashed password stored in the database
    // if the passwords match, the user is authenticated
    // the provided password (in the login) is hashed using the SHA-256 algorithm
    // VERIFY THAT THE CRYPTED PASSWORD IS THE SAME AS THE HASHED PASSWORD IN THE DB
    public boolean verifyCredentials(User user, String password) {
        //return user != null && user.getPassword().equals(password);
        return user != null && user.getCryptedP().equals(hashPassword(password));
    }
    // the password is hashed using the SHA-256 algorithm
    // a MessageDigest object is created with the SHA-256 algorithm
    // a StringBuilder is used to append the hashed bytes to a string
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    //PROFILE
    public int getPenpalsCount(Long userId) {
        return ((FriendshipDBRepository) friendshipRepository).countFriends(userId);
    }

    public Pair<String, String> getUserDetails(Long userId) {
        return ((UserDBRepository) repository).getUserDetails(userId);
    }
}

