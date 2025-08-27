package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final MailSender mailSender;

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine springTemplateEngine;

    public EmailServiceImpl(
            MailSender mailSender,
            JavaMailSender javaMailSender,
            SpringTemplateEngine springTemplateEngine
    ) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    public void sendSimpleEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("ngucxovai2k4@gmail.com");
        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World from Spring Boot Email");
        this.mailSender.send(msg);
    }

    @Override
    public void sendEmailSync(
            String to,
            String subject,
            String content,
            boolean isMultipart,
            boolean isHtml
    ) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException ex) {
            System.out.println("ERROR SEND EMAIL: " + ex);
        }
    }

    @Override
    public void sendEmailWithTemplateSync(
            String to,
            String subject,
            String templateName,
            String userName,
            Object value
    ) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("jobs", value);

        String content = this.springTemplateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
}
