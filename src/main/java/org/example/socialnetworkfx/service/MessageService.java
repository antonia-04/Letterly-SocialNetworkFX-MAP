package org.example.socialnetworkfx.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.socialnetworkfx.domain.Message;
import org.example.socialnetworkfx.repository.MessageDBRepository;

import java.util.List;

public class MessageService {
    private final MessageDBRepository messageRepository;
    private final ObservableList<Message> messages;

    public MessageService(MessageDBRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.messages = FXCollections.observableArrayList();
    }

    public void sendMessage(Message message) {
        messageRepository.save(message);
        messages.add(message);
    }

    public ObservableList<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        List<Message> messageList = messageRepository.getMessagesBetweenUsers(userId1, userId2);
        messages.setAll(messageList);
        return messages;
    }
}