package org.example.socialnetworkfx.service;

import org.example.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.repository.FriendshipDBRepository;
import org.example.socialnetworkfx.observer.Observer;
import org.example.socialnetworkfx.observer.Observable;

import java.util.*;

public class FriendshipService implements Service<Friendship>, Observable {
    private final FriendshipDBRepository repository;
    private final List<Observer> observers = new ArrayList<>();

    public FriendshipService(FriendshipDBRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long generateID() {
        return -1L;
    }

    @Override
    public Optional<Friendship> delete(Long ID) {
        Optional<Friendship> result = repository.delete(ID);
        notifyObservers();
        return result;
    }

    @Override
    public Iterable<Friendship> findAll() {
        return repository.findAll();
    }

    public List<User> findFriends(Long userId, int page, int pageSize) {
        return repository.findFriends(userId, page, pageSize);
    }

    public void deleteGUI(Long currentUserId, Long friendId) {
        List<Friendship> friendships = new ArrayList<>((Collection<? extends Friendship>) repository.findAll());
        Optional<Friendship> friendship = friendships.stream()
                .filter(f -> (f.getFirstFriend().getID().equals(currentUserId) && f.getSecondFriend().getID().equals(friendId)) ||
                        (f.getFirstFriend().getID().equals(friendId) && f.getSecondFriend().getID().equals(currentUserId)))
                .findFirst();

        friendship.ifPresent(f -> {
            repository.delete(f.getID());
            notifyObservers();
        });
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
}