package com.talan.empreintecarbone.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.Authentication;

import com.talan.empreintecarbone.dto.SupportMailDto;

public interface SupportService {

    MimeMessage createSupportMail(SupportMailDto supportMail, Authentication authentication) throws MessagingException;

    void sendSupportMail(SupportMailDto supportMail, Authentication authentication)
            throws MailSendException, MessagingException, MailException;

}
