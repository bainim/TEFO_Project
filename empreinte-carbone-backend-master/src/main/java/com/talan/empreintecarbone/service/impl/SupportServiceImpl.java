package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.dto.SupportMailDto;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.service.SupportService;
import com.talan.empreintecarbone.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupportServiceImpl implements SupportService {

    private final JavaMailSender javaMailSender;
    private String contactMail;
    private Set<String> allowedContentTypes;
    private UserService userService;

    public SupportServiceImpl(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String contactMail,
            UserService userService) {
        this.javaMailSender = javaMailSender;
        this.contactMail = contactMail;
        this.allowedContentTypes = new HashSet<>(Arrays.asList("image/png", "image/jpg", "image/jpeg"));
        this.userService = userService;
    }

    private boolean isAttachmentAllowed(MultipartFile attachedFile) {
        if (attachedFile != null && !allowedContentTypes.contains(attachedFile.getContentType())) {
            return false;
        }
        return true;
    }

    private String extension(MultipartFile file) {
        String contentType = file.getContentType();
        int index = contentType.lastIndexOf('/');
        String extension = "." + contentType.substring(index + 1);
        return extension;
    }

    private List<MultipartFile> getAllAttachedFiles(SupportMailDto supportMail) {
        List<MultipartFile> attachedFiles = new ArrayList<MultipartFile>();
        if (supportMail.getAttachedFile1() != null) {
            attachedFiles.add(supportMail.getAttachedFile1());
        }
        if (supportMail.getAttachedFile2() != null) {
            attachedFiles.add(supportMail.getAttachedFile2());
        }
        if (supportMail.getAttachedFile3() != null) {
            attachedFiles.add(supportMail.getAttachedFile3());
        }
        return attachedFiles;
    }

    @Override
    public MimeMessage createSupportMail(SupportMailDto supportMail, Authentication authentication)
            throws MessagingException {
        if (authentication != null) {
            User currentUser = userService.findOne(authentication.getName());
            supportMail.setSenderEmail(currentUser.getUsername());
        }
        MimeMessage helpMsg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(helpMsg, true);
        helper.setTo(contactMail);
        String mailTitle = "";
        if (supportMail.getOperatingSystem() != null) {
            mailTitle += supportMail.getOperatingSystem();
        }
        if (mailTitle.length() > 0 && supportMail.getCategory() != null) {
            mailTitle += "/";
        }
        if (supportMail.getCategory() != null) {
            mailTitle += supportMail.getCategory();
        }
        if (mailTitle.length() > 0) {
            mailTitle = "[" + mailTitle + "] ";
        }
        mailTitle += supportMail.getSenderEmail() + " - " + supportMail.getSubject();
        helper.setSubject(mailTitle);
        helper.setText(supportMail.getContent());
        helper.setReplyTo(supportMail.getSenderEmail());
        helper.setFrom(contactMail);

        List<MultipartFile> attachedFiles = getAllAttachedFiles(supportMail);

        if (attachedFiles.size() > 0) {
            for (MultipartFile file : attachedFiles) {
                if (isAttachmentAllowed(file)) {
                    helper.addAttachment(file.getName() + extension(file), file);
                } else {
                    MailSendException e = new MailSendException("wrong attachment format");
                    throw e;
                }
            }
        }
        return helpMsg;

    }

    @Override
    public void sendSupportMail(SupportMailDto supportMail, Authentication authentication)
            throws MailException, MessagingException {
        javaMailSender.send(createSupportMail(supportMail, authentication));

    }
}
