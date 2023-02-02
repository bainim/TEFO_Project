package com.talan.empreintecarbone.api;

import javax.mail.MessagingException;
import javax.ws.rs.core.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.dto.SupportMailDto;
import com.talan.empreintecarbone.service.SupportService;

@CrossOrigin(origins = { ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL }, maxAge = 3600)
@RestController
public class SupportController {
    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping(value = "/contactAdmin", consumes = { MediaType.MULTIPART_FORM_DATA })
    public ResponseEntity<?> contactAdmin(@ModelAttribute SupportMailDto supportMail, Authentication authentication)
            throws MessagingException, MailException {
        try {
            supportService.sendSupportMail(supportMail, authentication);
        } catch (MailSendException e) {
            return ResponseEntity.status(401).body(e.toString());
        } catch (MailAuthenticationException e) {
            return ResponseEntity.status(500).body(e.toString());
        }
        return ResponseEntity.ok("Success");

    }

}
