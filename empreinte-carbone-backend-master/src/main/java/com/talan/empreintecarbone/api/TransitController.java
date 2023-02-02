package com.talan.empreintecarbone.api;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.model.Transit;
import com.talan.empreintecarbone.service.TransitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = { ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL }, maxAge = 3600)
@RequestMapping("/api/transits")
@RestController
public class TransitController {

    private final TransitService transitService;

    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }

    @GetMapping()
    public ResponseEntity<List<Transit>> getTransits() {
        return ResponseEntity.ok(transitService.getNoRemoteTransits());
    }

}
