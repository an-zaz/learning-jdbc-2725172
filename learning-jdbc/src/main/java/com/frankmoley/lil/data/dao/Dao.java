package com.frankmoley.lil.data.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, D, ID> {
  List<T> findAll(int pageNumber, int limit);
  T create(D draft);
  Optional<T> findById(ID id);
  void delete(ID id);
}
