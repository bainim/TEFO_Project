package com.talan.empreintecarbone.repository;

import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.Trip;
import com.talan.empreintecarbone.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface TripRepository extends CrudRepository<Trip, Long> {
    List<Trip> findAllByUser(User currentUser, Pageable pageable);

    @Query("select t from Trip t where t.user = ?1 and t.date between ?2 and ?3 order by t.date DESC")
    List<Trip> findAllByRange(User currentUser, LocalDate firstDate, LocalDate lastDate);
    
    List<Trip> findByUserAndDate(User currentUser, LocalDate tripDate);

    int countAllByRoute(Route route);

    //@Query(value = "SELECT SUM(footprint_carbone) as footprint, SUM(distance) as distance FROM (SELECT * from trip WHERE trip.date BETWEEN ?2 AND ?3 AND trip.user_id=?1) AS table1 LEFT JOIN step ON table1.route_id=step.route_id", nativeQuery = true)
    @Query(value = "SELECT sum(co2) as footprint, sum(distance) as distance "
    		+ "from trip "
    		+ "left join step on step.route_id = trip.route_id "
    		+ "where trip.user_id=?1 "
    		+ "and trip.date between ?2 and ?3", nativeQuery = true)
    List<Object[]> getUserFootprint(UUID currentUserID, LocalDate firstDate, LocalDate lastDate);
    
    @Query(value = "SELECT footprint, distance, survey_answers.answers FROM (SELECT SUM(footprint_carbone) as footprint, SUM(distance) as distance, user_id FROM (SELECT * from trip WHERE trip.date BETWEEN ?2 AND ?3 AND NOT trip.user_id=?1) AS table1 "
            + "LEFT JOIN step ON table1.route_id=step.route_id group by user_id) AS table2 LEFT JOIN survey ON table2.user_id = survey.user_id LEFT JOIN survey_answers ON survey.id = survey_answers.survey_id WHERE survey.question='talan_site'", nativeQuery = true)
    List<Object[]> getAverageFootprint(UUID currentUserID, LocalDate firstDate, LocalDate lastDate);

    @Query(value = "SELECT CAST(user_id as varchar), table3.answers, u.username, sum(co2) as co2, sum(distance) as total_distance, count (*) "
    		+ "from trip tr "
    		+ "left join step on step.route_id = tr.route_id "
    		+ "left join users u on u.id = tr.user_id "
    		+ "left join (SELECT username, answers, users.id "
    		+ "			FROM survey "
    		+ "			LEFT JOIN users ON users.id = survey.user_id "
    		+ "			LEFT JOIN survey_answers ON survey_answers.survey_id = survey.id "
    		+ "			WHERE survey.question = 'talan_site') table3 ON table3.id = u.id "
    		+ "where tr.date between ?1 AND ?2 "
    		+ "group by user_id, u.username, table3.answers", nativeQuery = true)
    List<Object[]> getAverageFootprint(LocalDate firstDate, LocalDate lastDate);
    
    @Query(value = "SELECT CAST(user_id as varchar), table3.answers, u.username, sum(co2) as co2, sum(distance) as total_distance, count (*) "
    		+ "from trip tr "
    		+ "left join step on step.route_id = tr.route_id "
    		+ "left join users u on u.id = tr.user_id "
    		+ "left join (SELECT username, answers, users.id "
    		+ "			FROM survey "
    		+ "			LEFT JOIN users ON users.id = survey.user_id "
    		+ "			LEFT JOIN survey_answers ON survey_answers.survey_id = survey.id "
    		+ "			WHERE survey.question = 'talan_site') table3 ON table3.id = u.id "
    		+ "where tr.date >= ?1 and tr.date <= ?2 "
    		+ "or tr.date >= ?3 and tr.date <= ?4 "
    		+ "group by user_id, u.username, table3.answers", nativeQuery = true)
    List<Object[]> getAverageFootprintFirstWeek(LocalDate firstDate, LocalDate phase1EndDate, LocalDate phase2StartDate, LocalDate lastDate);    
    
    
    @Query(value = "SELECT CAST(user_id as varchar), table3.answers, u.username, sum(co2) as co2, sum(distance) as total_distance, count (*) "
    		+ "from trip tr "
    		+ "left join step on step.route_id = tr.route_id "
    		+ "left join users u on u.id = tr.user_id "
    		+ "left join (SELECT username, answers, users.id "
    		+ "			FROM survey "
    		+ "			LEFT JOIN users ON users.id = survey.user_id "
    		+ "			LEFT JOIN survey_answers ON survey_answers.survey_id = survey.id "
    		+ "			WHERE survey.question = 'talan_site') table3 ON table3.id = u.id "
    		+ "group by user_id, u.username, table3.answers", nativeQuery = true)
    List<Object[]> getAverageFootprintByDate();
    
    @Query(value = "SELECT survey_answers.answers FROM survey LEFT JOIN survey_answers ON survey.id = survey_answers.survey_id WHERE survey.question = 'talan_site' AND survey.user_id = ?1", nativeQuery = true)
    String getTalanSite(UUID currentUserID);

    @Query(value = "SELECT COUNT(*) FROM survey LEFT JOIN survey_answers ON survey.id = survey_answers.survey_id WHERE survey.question = 'talan_site' AND survey_answers.answers = ?1", nativeQuery = true)
    int getCountUsersInTalanSite(String TalanSite);

}
