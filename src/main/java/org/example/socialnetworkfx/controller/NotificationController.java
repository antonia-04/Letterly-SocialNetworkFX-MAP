package org.example.socialnetworkfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NotificationController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}