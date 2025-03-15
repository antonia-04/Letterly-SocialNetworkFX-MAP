package org.example.socialnetworkfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.service.FriendshipRequestService;

import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestController {

    @FXML
    private TableView<User> friendRequestsTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    private FriendshipRequestService friendshipRequestService;
    private User currentUser;
    private ObservableList<User> friendRequests;

    public void setServices(FriendshipRequestService friendshipRequestService, User currentUser) {
        this.friendshipRequestService = friendshipRequestService;
        this.currentUser = currentUser;
        loadFriendRequests();
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    }

    private void loadFriendRequests() {
        List<User> requests = friendshipRequestService.getFriendRequestsUser(currentUser.getID());
        requests = requests.stream()
                .filter(user -> !user.equals(currentUser))
                .collect(Collectors.toList());
        friendRequests = FXCollections.observableArrayList(requests);
        friendRequestsTable.setItems(friendRequests);
    }

    @FXML
    private void handleAccept() {
        User selectedUser = friendRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            friendshipRequestService.acceptFriendRequest(currentUser.getID(), selectedUser.getID());
            friendRequests.remove(selectedUser);
        }
    }

    @FXML
    private void handleDeny() {
        User selectedUser = friendRequestsTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            friendshipRequestService.denyFriendRequest(currentUser.getID(), selectedUser.getID());
            friendRequests.remove(selectedUser);
        }
    }
}