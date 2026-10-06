package com.indra.notifications.infrastructure.adapters.email;

import com.indra.notifications.application.ports.output.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class SmtpEmailSender implements EmailSender {

    @Value("${notification.retry-attempts}")
    private int retries;

    @Override
    public void send(String to, String subject, String body) {
        System.out.println("[SMTP] Conectando a servidor real y enviando a " + to + ": " + subject + " -> " + body);
        System.out.println("[SMTP] Número de reintentos configurado: " + retries);
    }

}
