package com.talan.empreintecarbone.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.talan.empreintecarbone.model.Transit;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TransitRepository extends CrudRepository<Transit, Long> {

    List<Transit> findByNameNot(String name);

}
