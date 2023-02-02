package com.talan.empreintecarbone.repository;

import com.talan.empreintecarbone.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.talan.empreintecarbone.model.Route;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RouteRepository extends CrudRepository<Route, Long> {
    List<Route> findAllByUser(User currentUser, Pageable pageable);
}
