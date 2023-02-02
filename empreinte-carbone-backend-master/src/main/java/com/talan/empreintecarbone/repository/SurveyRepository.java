package com.talan.empreintecarbone.repository;

import com.talan.empreintecarbone.model.Survey;
import com.talan.empreintecarbone.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SurveyRepository extends CrudRepository<Survey, Long> {
    List<Survey> findAllByUser(User currentUser);
}
