package com.jobHunter.demoAPI.service;

public interface EmailService {

    void sendSimpleEmail();

    void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailWithTemplateSync(String to, String subject, String templateName, String userName, Object value);
}
