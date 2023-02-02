package com.talan.empreintecarbone.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.talan.empreintecarbone.dto.SupportMailDto;
import com.talan.empreintecarbone.service.UserService;

class SupportServiceImplTest {

	private JavaMailSender javaMailSender;
	private SupportServiceImpl supportServiceImpl;
	private SupportMailDto supportMail;
	private UserService mockUserService;

	@BeforeEach
	void setUp() throws Exception {
		javaMailSender = new JavaMailSenderImpl();
		mockUserService = Mockito.mock(UserService.class);
		supportServiceImpl = new SupportServiceImpl(javaMailSender, "contactMail", mockUserService);
		supportMail = new SupportMailDto();
		supportMail.setSenderEmail("senderEmailTest");
		supportMail.setSubject("subjectTest");
		supportMail.setContent("contentTest");
	}

	@Test
	void testCreateSupportEmail() throws MessagingException {
		supportMail.setCategory("categoryTest");
		supportMail.setOperatingSystem("systemTest");
		MimeMessage expectedMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper expectedHelper = new MimeMessageHelper(expectedMimeMessage, true);
		expectedHelper.setTo("contactMail");
		expectedHelper.setSubject("[systemTest/categoryTest] senderEmailTest - subjectTest");
		expectedHelper.setText("contentTest");
		expectedHelper.setFrom("contactMail");
		expectedHelper.setReplyTo("senderEmailTest");

		MimeMessage actualMimeMessage = supportServiceImpl.createSupportMail(supportMail, null);

		Assertions.assertThat(actualMimeMessage.getFrom()).isEqualTo(expectedMimeMessage.getFrom());
		Assertions.assertThat(actualMimeMessage.getReplyTo()).isEqualTo(expectedMimeMessage.getReplyTo());
		Assertions.assertThat(actualMimeMessage.getSubject()).isEqualTo(expectedMimeMessage.getSubject());
	}

	@Test
	void testCreateSupportEmailIfNoCategory() throws MessagingException {
		supportMail.setOperatingSystem("systemTest");
		MimeMessage expectedMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper expectedHelper = new MimeMessageHelper(expectedMimeMessage, true);
		expectedHelper.setTo("contactMail");
		expectedHelper.setSubject("[systemTest] senderEmailTest - subjectTest");
		expectedHelper.setText("contentTest");
		expectedHelper.setFrom("contactMail");
		expectedHelper.setReplyTo("senderEmailTest");

		MimeMessage actualMimeMessage = supportServiceImpl.createSupportMail(supportMail, null);

		Assertions.assertThat(actualMimeMessage.getFrom()).isEqualTo(expectedMimeMessage.getFrom());
		Assertions.assertThat(actualMimeMessage.getReplyTo()).isEqualTo(expectedMimeMessage.getReplyTo());
		Assertions.assertThat(actualMimeMessage.getSubject()).isEqualTo(expectedMimeMessage.getSubject());
	}

	@Test
	void testCreateSupportEmailIfNoOS() throws MessagingException {
		supportMail.setCategory("categoryTest");
		MimeMessage expectedMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper expectedHelper = new MimeMessageHelper(expectedMimeMessage, true);
		expectedHelper.setTo("contactMail");
		expectedHelper.setSubject("[categoryTest] senderEmailTest - subjectTest");
		expectedHelper.setText("contentTest");
		expectedHelper.setFrom("contactMail");
		expectedHelper.setReplyTo("senderEmailTest");

		MimeMessage actualMimeMessage = supportServiceImpl.createSupportMail(supportMail, null);

		Assertions.assertThat(actualMimeMessage.getFrom()).isEqualTo(expectedMimeMessage.getFrom());
		Assertions.assertThat(actualMimeMessage.getReplyTo()).isEqualTo(expectedMimeMessage.getReplyTo());
		Assertions.assertThat(actualMimeMessage.getSubject()).isEqualTo(expectedMimeMessage.getSubject());
	}

	@Test
	void testCreateSupportEmailIfNoCategoryAndNoOS() throws MessagingException {
		MimeMessage expectedMimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper expectedHelper = new MimeMessageHelper(expectedMimeMessage, true);
		expectedHelper.setTo("contactMail");
		expectedHelper.setSubject("senderEmailTest - subjectTest");
		expectedHelper.setText("contentTest");
		expectedHelper.setFrom("contactMail");
		expectedHelper.setReplyTo("senderEmailTest");

		MimeMessage actualMimeMessage = supportServiceImpl.createSupportMail(supportMail, null);

		Assertions.assertThat(actualMimeMessage.getFrom()).isEqualTo(expectedMimeMessage.getFrom());
		Assertions.assertThat(actualMimeMessage.getReplyTo()).isEqualTo(expectedMimeMessage.getReplyTo());
		Assertions.assertThat(actualMimeMessage.getSubject()).isEqualTo(expectedMimeMessage.getSubject());
	}
}