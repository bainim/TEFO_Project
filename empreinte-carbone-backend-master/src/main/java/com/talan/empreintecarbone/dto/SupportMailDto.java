package com.talan.empreintecarbone.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class SupportMailDto {
    private String senderEmail;
    private String category;
    private String operatingSystem;
    private String subject;
    private String content;
    private MultipartFile attachedFile1;
    private MultipartFile attachedFile2;
    private MultipartFile attachedFile3;

}
