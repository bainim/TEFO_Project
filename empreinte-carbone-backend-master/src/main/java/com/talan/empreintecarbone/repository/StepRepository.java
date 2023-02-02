package com.talan.empreintecarbone.repository;

import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.Step;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface StepRepository extends CrudRepository<Step, Long> {
    void deleteByRoute(Route route);
}
