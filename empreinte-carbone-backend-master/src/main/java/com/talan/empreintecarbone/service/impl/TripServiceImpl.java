package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.constant.Constants;
import com.talan.empreintecarbone.constant.Range;
import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.model.*;
import com.talan.empreintecarbone.repository.TripRepository;
import com.talan.empreintecarbone.service.TripService;
import org.apache.commons.math3.util.Precision;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
public class TripServiceImpl implements TripService {
	private final TripRepository tripRepository;

	public TripServiceImpl(TripRepository tripRepository) {
		this.tripRepository = tripRepository;
	}

	@Override
	public Trip saveTrip(Route route, String date, User user) {
		AtomicReference<Float> empreinteCarbonne = new AtomicReference<>((float) 0);
		Trip trip = new Trip();
		List<Step> steps = (List<Step>) route.getSteps();
		steps.forEach(step -> {
			empreinteCarbonne.updateAndGet(v -> v + step.getCo2());
		});
		trip.setFootprintCarbone(empreinteCarbonne.get());
		trip.setDate(LocalDate.parse(date));
		trip.setUser(user);
		trip.setRoute(route);
		return tripRepository.save(trip);
	}

	@Override
	public List<Trip> getTrips(User currentUser, Integer pageNo, Integer pageSize) {
		List<Trip> allByUser = tripRepository.findAllByUser(currentUser,
				PageRequest.of(pageNo, pageSize, Sort.by("date").descending()));
		allByUser.forEach(trip -> {
			trip.setSumDistances(trip.getRoute().getSteps().stream().mapToDouble(Step::getDistance).sum());
			trip.setFootprintCarbone(Precision.round(trip.getFootprintCarbone(), 2));
			trip.setCo2ByKilometre(Precision.round(trip.getFootprintCarbone() / trip.getSumDistances(), 2));
		});
		return allByUser;
	}

	@Override
	public List<DataDashboard> getMyDashboard(User currentUser, String range, LocalDate startDate) {
		LocalDate now = LocalDate.now();
		List<DataDashboard> dataDashboards;
		if (Range.DAY.getValue().equals(range)) {
			dataDashboards = dashboard(startDate, currentUser, Range.DAY);
		} else if (Range.WEEK.getValue().equals(range)) {
			dataDashboards = dashboard(startDate.with(TemporalAdjusters.previousOrSame(MONDAY)), currentUser,
					Range.WEEK);
		} else if (Range.TRIMESTER.getValue().equals(range)) {
			dataDashboards = getDashboard(now.minusMonths(3), now.plusMonths(3), currentUser, Range.TRIMESTER);
		} else if (Range.YEAR.getValue().equals(range)) {
			dataDashboards = getDashboard(now.minusYears(1), now.plusYears(1), currentUser, Range.YEAR);
		} else {
			throw new InvalidFieldException("The range is invalid, it should be : week, month, trimester or year");
		}
		return dataDashboards;
	}

	public static List<LocalDate> addDaysSkippingWeekends(LocalDate date, int days) {
		LocalDate result = date;
		int addedDays = 0;
		List<LocalDate> dates = new ArrayList<>();
		while (addedDays < days) {
			result = result.plusDays(1);
			if (!(result.getDayOfWeek() == SATURDAY || result.getDayOfWeek() == SUNDAY)) {
				dates.add(result);
				++addedDays;
			}
		}
		return dates;
	}

	public static List<LocalDate> subtractDaysSkippingWeekends(LocalDate date, int days) {
		LocalDate result = date;
		int subtractedDays = 0;
		List<LocalDate> dates = new ArrayList<>();
		while (subtractedDays < days) {
			result = result.minusDays(1);
			if (!(result.getDayOfWeek() == SATURDAY || result.getDayOfWeek() == SUNDAY)) {
				dates.add(result);
				++subtractedDays;
			}
		}
		return dates;
	}

	public static List<LocalDate> subtractWeeks(LocalDate date, int weeks) {
		LocalDate result = date;
		int subtractedWeeks = 0;
		List<LocalDate> dates = new ArrayList<>();
		while (subtractedWeeks < weeks) {
			result = result.minusWeeks(1);
			dates.add(result);
			++subtractedWeeks;
		}
		return dates;
	}

	public static List<LocalDate> addWeeks(LocalDate date, int weeks) {
		LocalDate result = date;
		int addedWeeks = 0;
		List<LocalDate> dates = new ArrayList<>();
		while (addedWeeks < weeks) {
			result = result.plusWeeks(1);
			dates.add(result);
			++addedWeeks;
		}
		return dates;
	}

	private List<DataDashboard> dashboard(LocalDate startDate, User currentUser, Range range) {
		List<LocalDate> firstDates;
		LocalDate lastDate;
		startDate = getStartDate(startDate);
		if (range == Range.DAY) {
			firstDates = subtractDaysSkippingWeekends(startDate, 4);
			lastDate = startDate;
		} else {
			firstDates = subtractWeeks(startDate, 4);
			lastDate = startDate;
			lastDate = startDate.plusDays(4);
		}

		List<Trip> trips = tripRepository.findAllByRange(currentUser, firstDates.get(firstDates.size() - 1), lastDate);

		firstDates.add(startDate);
		Collections.sort(firstDates);

		Map<LocalDate, Double> collectData = trips.stream().collect(groupingBy(
				trip -> trip.getDate().with(range.getTemporalAdjusters()), summingDouble(Trip::getFootprintCarbone)));

		List<DataDashboard> points = new ArrayList<>();
		for (LocalDate v : firstDates) {
			Double aDouble = collectData.get(v);
			DataDashboard dataDashboard;
			if (aDouble != null) {
				dataDashboard = new DataDashboard(v.toString(), aDouble);
			} else {
				dataDashboard = new DataDashboard(v.toString(), 0);
			}
			points.add(dataDashboard);
		}
		return points;
	}

	private LocalDate getStartDate(LocalDate startDate) {
		if (startDate.getDayOfWeek() == SATURDAY || startDate.getDayOfWeek() == SUNDAY) {
			int minus = startDate.getDayOfWeek().getValue() - FRIDAY.getValue();
			startDate = startDate.minusDays(minus);
		}
		return startDate;
	}

	@Override
	public void deleteTrip(Long id) {
		Optional<Trip> trip = tripRepository.findById(id);
		if (trip.isPresent()) {
			tripRepository.delete(trip.get());
		} else {
			throw new InvalidFieldException("the trip ID is invalid");
		}
	}

	private List<DataDashboard> getDashboard(LocalDate firstDate, LocalDate lastDate, User currentUser, Range range) {
		List<Trip> trips = tripRepository.findAllByRange(currentUser, firstDate, lastDate);
		if (trips.isEmpty())
			return Collections.emptyList();
		Map<LocalDate, Double> mapDeplacemts = trips.stream().collect(groupingBy(
				trip -> trip.getDate().with(range.getTemporalAdjusters()), summingDouble(Trip::getFootprintCarbone)));
		return mapDeplacemts.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.map(m -> new DataDashboard(m.getKey().toString(), m.getValue())).collect(Collectors.toList());
	}

	@Override
	public float getUserFootprint(User currentUser, String firstDate, String lastDate) {
		LocalDate localFirstDate = LocalDate.parse(firstDate);
		LocalDate localLastDate = LocalDate.parse(lastDate);
		UUID currentUserID = currentUser.getId();
		List<Object[]> userValuesInList = tripRepository.getUserFootprint(currentUserID, localFirstDate, localLastDate);
		if (userValuesInList == null) {
			return 0f;
		}
		Object[] userValues = userValuesInList.get(0);
		float footprint = userValues[0] != null ? (float) userValues[0] : (float) (userValues[0] = 0f);
		float distance = userValues[1] != null ? (float) userValues[1] : (float) (userValues[1] = 0f);
		if (distance == 0f || footprint == 0f) {
			return 0f;
		} else {
			return footprint / distance;
		}
	}

	@Override
	public float getAverageFootprint(User currentUser, String firstDate, String lastDate) {
		UUID currentUserID = currentUser.getId();
		LocalDate localFirstDate = LocalDate.parse(firstDate);
		LocalDate localLastDate = LocalDate.parse(lastDate);
		List<Object[]> listAllUserFootprint;

		LocalDate phase1EndDate = LocalDate.parse(Constants.PHASE1_END_DATE);
		LocalDate phase2StartDate = LocalDate.parse(Constants.PHASE2_START_DATE);
		LocalDate today = LocalDate.now();

		int daysBetween = countDaysBetweenTwoDatesExceptWeekEnd(phase2StartDate, today);

		if (daysBetween <= 5) {
			listAllUserFootprint = tripRepository.getAverageFootprintFirstWeek(localFirstDate, phase1EndDate,
					phase2StartDate, localLastDate);
		} else {
			listAllUserFootprint = tripRepository.getAverageFootprint(localFirstDate, localLastDate);
		}
		
		if (listAllUserFootprint.size()==0) {
			return 0f;
			}

		String currentUserSite = tripRepository.getTalanSite(currentUserID);
//    	for (int i = 0; i < listAllUserFootprint.size(); i++) {
//    		Object[] object = listAllUserFootprint.get(i); // each lign of the query result
//    		currentUserSite = (String) object[1]; // answer column of each lign
//    		if (object[0].toString().equalsIgnoreCase(currentUserID.toString())) { // search id of the current user
//				break;
//			}
//		}

		List<Object[]> participantsWithSameSiteAsCurrentUser = new ArrayList();
		for (int i = 0; i < listAllUserFootprint.size(); i++) {
			Object[] object = listAllUserFootprint.get(i); // each lign of the query result
			if (object[1] != null && object[1].toString().equalsIgnoreCase(currentUserSite)) { // search the site of the
																								// current user
				participantsWithSameSiteAsCurrentUser.add(object);
			}
		}

		float cumulatedFootprint = 0f;
		if (participantsWithSameSiteAsCurrentUser.size() < 5) { // Participants less than 5 === National comparaison
			for (int i = 0; i < listAllUserFootprint.size(); i++) {
				Object[] object = listAllUserFootprint.get(i);
				float footprint = (float) object[3];
				float distance = (float) object[4];
				if (distance != 0 || footprint != 0) {
					cumulatedFootprint += footprint / distance;
				}
			}
			return cumulatedFootprint / listAllUserFootprint.size();
		} else { // Participants greater than or equal 5 === Local Comparaison
			for (int i = 0; i < participantsWithSameSiteAsCurrentUser.size(); i++) {
				Object[] object = participantsWithSameSiteAsCurrentUser.get(i);
				float footprint = (float) object[3];
				float distance = (float) object[4];
				if (distance != 0 || footprint != 0) {
					cumulatedFootprint += footprint / distance;
				}
			}
			return cumulatedFootprint / participantsWithSameSiteAsCurrentUser.size();
		}

	}

	@Override
	public int countDaysBetweenTwoDatesExceptWeekEnd(LocalDate startDate, LocalDate today) {
		int count = 0;
		while (startDate.isBefore(today) || startDate.isEqual(today)) {
			DayOfWeek dayOfWeek = startDate.getDayOfWeek();
			if (!(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
				count++;
			}
			startDate = startDate.plusDays(1);
		}
		return count;
	}

//    @Override
//    public Map<String, Double> getPhase2Elements() {
//    	LocalDate phase2Start = LocalDate.parse("2022-03-07");
//    	List<Object[]>listAllUserFootprint = tripRepository.getAverageFootprintByDate();
//    	Map<String, Object[]> mapList = new HashMap<String, Object[]>();
//    	for (Object[] objects : listAllUserFootprint) {
//			if (!mapList.containsKey(objects[3])) {
//				mapList.put((String) objects[3], objects);
//			} else {
//				Object object = mapList.get(objects[3]);
//				object.getC
//			}
//		}
//    	return mapList;
//    }

	public Trip saveTripWitchBackRoad(Route route, Route backRoute, String date, User user) {
		AtomicReference<Float> empreinteCarbonne = new AtomicReference<>((float) 0);
		Trip trip = new Trip();
		List<Step> steps = (List<Step>) route.getSteps();
		if (backRoute != null) {
			steps.addAll((List<Step>) backRoute.getSteps());
			 trip.setBackRoute(backRoute);
		}
		steps.forEach(step -> {
			empreinteCarbonne.updateAndGet(v -> v + step.getCo2());
		});
		trip.setFootprintCarbone(empreinteCarbonne.get());
		trip.setDate(LocalDate.parse(date));
		trip.setUser(user);
		trip.setRoute(route);

		return tripRepository.save(trip);
	}

}
