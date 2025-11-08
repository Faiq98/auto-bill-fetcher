package com.fhm.bills.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body);

        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        javaMailSender.send(message);
        System.out.println("Email Send Successfully with attachment : " + attachmentPath);
    }
}
