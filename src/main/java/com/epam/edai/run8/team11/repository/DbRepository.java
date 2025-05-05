package com.epam.edai.run8.team11.repository;

import java.util.Optional;

public interface DbRepository<T> {
    boolean save(T t);
    Optional<T> get(String email);
}
