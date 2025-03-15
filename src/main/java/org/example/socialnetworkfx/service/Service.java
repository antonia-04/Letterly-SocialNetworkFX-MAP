package org.example.socialnetworkfx.service;
import java.util.Optional;

public interface Service<E> {
    Optional<E> delete(Long ID);
    Long generateID();
    Iterable<E> findAll();

}
