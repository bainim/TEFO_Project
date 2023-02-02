package com.talan.empreintecarbone.api;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.constant.Constants;
import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.model.DataDashboard;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.service.TripService;
import com.talan.empreintecarbone.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL}, maxAge = 3600)
@RequestMapping("/api/data")
@RestController
public class ReportController {
    private final UserService userService;
    private final TripService tripService;

    public ReportController(UserService userService, TripService tripService) {
        this.userService = userService;
        this.tripService = tripService;
    }

    @GetMapping(value = "/{range}/{startDate}")
    public ResponseEntity<List<DataDashboard>> getReports(
            Authentication authentication,
            @ApiParam(
                    name = "range",
                    type = "String",
                    value = "The range date, it should be one of these values : week, month, trimester or year",
                    required = true)
            @PathVariable("range") String range,
            @ApiParam(
                    name = "startDate",
                    type = "LocalDate",
                    value = "The starting date for the result e.g.: 2021-09-29",
                    required = true)
            @PathVariable("startDate") String startDate
    ) {
        User currentUser = userService.findOne(authentication.getName());
        List<DataDashboard> data;
        try {
            data = tripService.getMyDashboard(currentUser, range, LocalDate.parse(startDate));
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping(value = "/phasesDates")
    public ResponseEntity<List<String>> getPhasesDates() {
		List<String> phasesDates = new ArrayList<String>();
		phasesDates.add(Constants.PHASE1_END_DATE);
		phasesDates.add(Constants.PHASE2_START_DATE);
		return ResponseEntity.ok(phasesDates);
    }
}
