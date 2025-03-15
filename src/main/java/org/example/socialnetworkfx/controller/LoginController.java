package org.example.socialnetworkfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.domain.exception.EntityMissingException;
import org.example.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.service.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRQService;
    private MessageService messageService;

    public void setUserService(UserService userService, FriendshipService friendshipService, FriendshipRequestService friendshipRQService, MessageService messageService) { // Update this line
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendshipRQService = friendshipRQService;
        this.messageService = messageService;
    }

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username != null && password != null) {
            try {
                User user = userService.findUser(username);
                if (!userService.verifyCredentials(user, password)) {
                    showInformationAlert("Login Failed", "Operation Failed", "Wrong username or password.");
                    return;
                }

                openUserPage(user);
                closePage((Stage) loginButton.getScene().getWindow());

            } catch (EntityMissingException | SQLException entityMissingException) {
                System.out.println(entityMissingException.getMessage());
                showInformationAlert("Login Failed", "Invalid Credentials", entityMissingException.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showInformationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closePage(Stage stage) {
        stage.close();
    }

    private void openUserPage(User user) throws IOException {
        System.out.println(user);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetworkfx/fxml/home-page.fxml"));
        Scene scene = new Scene(loader.load());

        HomePageController homePageController = loader.getController();
        homePageController.setUserService(userService);
        homePageController.setFriendshipService(friendshipService);
        homePageController.setFriendRequestsService(friendshipRQService);
        homePageController.setMessageService(messageService);
        homePageController.setCurrentUser(user);

        Stage stage = new Stage();
        stage.setTitle("Home Page");
        stage.setScene(scene);
        stage.show();
    }
}