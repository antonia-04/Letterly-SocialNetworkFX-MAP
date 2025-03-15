package org.example.socialnetworkfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.socialnetworkfx.controller.StartController;
import org.example.socialnetworkfx.service.UserService;
import org.example.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.repository.UserDBRepository;
import org.example.socialnetworkfx.repository.FriendshipDBRepository;
import org.example.socialnetworkfx.repository.FriendshipRequestDBRepository;
import org.example.socialnetworkfx.repository.MessageDBRepository;
import org.example.socialnetworkfx.domain.validation.UserValidation;
import org.example.socialnetworkfx.domain.validation.FriendshipValidation;
import org.example.socialnetworkfx.domain.validation.FriendshipRequestValidation;

import java.sql.Connection;
import java.sql.DriverManager;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String user = "postgres";
        String password = "antonia";
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to the database.");

        UserDBRepository userRepository = new UserDBRepository(new UserValidation(), connection);
        FriendshipDBRepository friendshipRepository = new FriendshipDBRepository(new FriendshipValidation(), connection);
        FriendshipRequestDBRepository friendshipRequestRepository = new FriendshipRequestDBRepository(new FriendshipRequestValidation(), connection);
        MessageDBRepository messageRepository = new MessageDBRepository(connection);

        UserService userService = new UserService(userRepository, friendshipRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository);
        FriendshipRequestService friendshipRequestService = new FriendshipRequestService(friendshipRequestRepository, userRepository, friendshipRepository);
        MessageService messageService = new MessageService(messageRepository);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/socialnetworkfx/fxml/hello-view.fxml"));
        loader.setControllerFactory(param -> new StartController(userService, friendshipService, friendshipRequestService, messageService));
        Parent root = loader.load();

        primaryStage.setTitle("Letterly - Welcome page!");
        primaryStage.setScene(new Scene(root, 360, 640));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}