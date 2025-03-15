package org.example.socialnetworkfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.service.UserService;

import java.io.IOException;

public class StartController {

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button confirmButton;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRQService;
    private MessageService messageService; // Add this field

    public StartController() {
    }

    public StartController(UserService userService, FriendshipService friendshipService, FriendshipRequestService friendshipRQService, MessageService messageService) { // Update this line
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendshipRQService = friendshipRQService;
        this.messageService = messageService; // Add this line
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleGetStartedButton() {
        openLoginPage();
        closeCurrentPage();
    }

    private void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetworkfx/fxml/login-view.fxml"));
            VBox loginPane = loader.load();

            LoginController loginController = loader.getController();
            loginController.setUserService(userService, friendshipService, friendshipRQService, messageService); // Update this line

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(loginPane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCurrentPage() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}