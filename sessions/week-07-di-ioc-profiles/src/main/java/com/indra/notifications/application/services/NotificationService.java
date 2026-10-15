package com.indra.notifications.application.services;

import com.indra.notifications.application.ports.output.EmailSender;
import com.indra.notifications.audit.NotificationAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailSender emailSender;
    private final NotificationAuditLog auditLog;

    @Autowired
    public NotificationService(EmailSender emailSender, NotificationAuditLog auditLog) {
        this.emailSender = emailSender;
        this.auditLog = auditLog;
    }

    public void notify(String to, String subject, String body) {
        emailSender.send(to, subject, body);
        auditLog.record(to, subject);
    }

}
