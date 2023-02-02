package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.model.DataDashboard;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.Trip;
import com.talan.empreintecarbone.model.User;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
	Trip saveTrip(Route route, String date, User user);

	List<Trip> getTrips(User currentUser, Integer pageNo, Integer pageSize);

	List<DataDashboard> getMyDashboard(User currentUser, String range, LocalDate startDate);

	void deleteTrip(Long id);

	float getUserFootprint(User currentUser, String firstDate, String lastDate);

	float getAverageFootprint(User currentUser, String firstDate, String lastDate);

	int countDaysBetweenTwoDatesExceptWeekEnd(LocalDate startDate, LocalDate today);

	public Trip saveTripWitchBackRoad(Route route, Route backRoute, String date, User user);

}
