package com.indra.notifications.infrastructure.adapters.email;

import com.indra.notifications.application.ports.output.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "notification", name = "sender", havingValue = "fake")
public class FakeEmailSender implements EmailSender {

    @Value("${notification.retry-attempts}")
    private int retries;

    @Override
    public void send(String to, String subject, String body) {
        System.out.println("[FAKE] Simulando envío a " + to + ": " + subject + " -> " + body);
        System.out.println("[FAKE] Número de reintentos configurado: " + retries);
    }

}
