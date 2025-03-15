package org.example.socialnetworkfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.service.UserService;

public class ProfileController {

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label bio;

    @FXML
    private Label from;

    @FXML
    private Label penpals;

    @FXML
    private ImageView profileImage;

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUser(User user) {
        fullNameLabel.setText(user.getFirstName() + " " + user.getLastName());
        usernameLabel.setText("@" + user.getUsername());

        Pair<String, String> userDetails = userService.getUserDetails(user.getID());
        if (userDetails != null) {
            bio.setText(userDetails.getKey());
            from.setText(userDetails.getValue());
        }

        // Get penpals count from the UserService
        int penpalsCount = userService.getPenpalsCount(user.getID());
        penpals.setText(String.valueOf(penpalsCount));

    }
}