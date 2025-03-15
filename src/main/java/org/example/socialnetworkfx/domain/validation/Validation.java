package org.example.socialnetworkfx.domain.validation;

public interface Validation<T>{
    void validate(T entity) throws ValidationException;
    }

