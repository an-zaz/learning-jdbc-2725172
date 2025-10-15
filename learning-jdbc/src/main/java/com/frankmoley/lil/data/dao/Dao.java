package com.frankmoley.lil.data.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Dao <T, Id> {
  List<T> findAll(int pageNumber, int limit);
  T create(T entity);
  Optional<T> findById(Id id);
  void delete(Id id);
}
