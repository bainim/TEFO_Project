package com.talan.empreintecarbone.api;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.dto.SurveyDto;
import com.talan.empreintecarbone.model.Survey;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.service.SurveyService;
import com.talan.empreintecarbone.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = {ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL}, maxAge = 3600)
@RequestMapping("/api/survey")
@RestController
public class SurveyController {

    private final UserService userService;
    private final SurveyService surveyService;

    public SurveyController(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @PostMapping()
    public ResponseEntity<String> saveAnswer(@RequestBody List<SurveyDto> surveyDtos, Authentication authentication) {
        String name = authentication.getName();
        User currentUser = userService.findOne(name);
        surveyService.save(surveyDtos, currentUser);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/userHasAnswer")
    public ResponseEntity<Boolean> hasAnswer(Authentication authentication) {
        /*SimpleKeycloakAccount account = (SimpleKeycloakAccount) authentication.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        User currentUser = userService.findOne(token.getId());
        Optional<List<Survey>> answer = surveyService.findAnswer(currentUser);*/
        String name = authentication.getName();
        User one = userService.findOne(name);
        Optional<List<Survey>> answer = surveyService.findAnswer(one);
        return ResponseEntity.ok(answer.isPresent());
    }
}
