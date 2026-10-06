package com.indra.notifications.audit;

import org.springframework.stereotype.Component;

@Component
public class NotificationAuditLog {

    public void record(String to, String subject) {
        System.out.println("[AUDIT] Notificación registrada para " + to + " - " + subject);
    }

}
