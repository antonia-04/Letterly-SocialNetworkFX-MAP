package org.example.socialnetworkfx.service;

import org.example.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.domain.FriendshipRequest;
import org.example.socialnetworkfx.domain.Status;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.observer.Observer;
import org.example.socialnetworkfx.observer.Observable;
import org.example.socialnetworkfx.repository.AbstractDBRepository;
import org.example.socialnetworkfx.repository.FriendshipRequestDBRepository;
import org.example.socialnetworkfx.repository.UserDBRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FriendshipRequestService implements Service<FriendshipRequest>, Observable {
    private final FriendshipRequestDBRepository repository;
    private final UserDBRepository userRepository;
    private final AbstractDBRepository<Long, Friendship> friendshipDBRepository;
    private final List<Observer> observers = new ArrayList<>();

    public FriendshipRequestService(FriendshipRequestDBRepository repository, UserDBRepository userRepository, AbstractDBRepository<Long, Friendship> friendshipDBRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.friendshipDBRepository = friendshipDBRepository;
    }

    @Override
    public Long generateID() {
        return -1L;
    }

    @Override
    public Optional<FriendshipRequest> delete(Long ID) {
        Optional<FriendshipRequest> result = repository.delete(ID);
        notifyObservers();
        return result;
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        return repository.findAll();
    }


    public void sendFriendRequest(Long id1, Long id2) {
        repository.sendFriendRequest(id1, id2);
        notifyObservers();
    }

    public List<User> getFriendRequests(Long userId) {
        return repository.getFriendRequests(userId);
    }

    public List<User> getFriendRequestsUser(Long userId) {
        return repository.getFriendRequestsUser(userId);
    }

    public void acceptFriendRequest(Long recipientId, Long senderId) {
        Optional<FriendshipRequest> requestOptional = repository.findRequestBySenderAndRecipient(senderId, recipientId);
        if (requestOptional.isPresent()) {
            FriendshipRequest request = requestOptional.get();
            request.setStatus(Status.ACCEPTED);
            repository.updateFR(request);
            LocalDateTime date = LocalDateTime.now();
            User user1 = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
            User user2 = userRepository.findById(recipientId).orElseThrow(() -> new RuntimeException("Recipient not found"));
            friendshipDBRepository.save(new Friendship(user1, user2, date));
            notifyObservers();
        } else {
            throw new RuntimeException("Friendship request not found.");
        }
    }

    public void denyFriendRequest(Long recipientId, Long senderId) {
        Optional<FriendshipRequest> requestOptional = repository.findRequestBySenderAndRecipient(senderId, recipientId);
        if (requestOptional.isPresent()) {
            FriendshipRequest request = requestOptional.get();
            request.setStatus(Status.REJECTED);
            repository.updateFR(request);
            notifyObservers();
        } else {
            throw new RuntimeException("Friendship request not found.");
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public boolean hasPendingRequests(Long userId) {
        List<FriendshipRequest> requests = (List<FriendshipRequest>) repository.findAll();
        return requests.stream()
                .anyMatch(request -> request.getRecipient().getID().equals(userId) && request.getStatus() == Status.PENDING);
    }
}