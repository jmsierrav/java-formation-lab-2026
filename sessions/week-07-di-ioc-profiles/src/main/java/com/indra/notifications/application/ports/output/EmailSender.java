package com.indra.notifications.application.ports.output;

public interface EmailSender {

    void send(String to, String subject, String body);

}
