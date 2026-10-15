package com.indra.notifications.application.services;

import com.indra.notifications.application.ports.output.EmailSender;
import com.indra.notifications.audit.NotificationAuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService")
class NotificationServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private NotificationAuditLog auditLog;

    @InjectMocks
    private NotificationService service;

    @ParameterizedTest(name = "{index}: envia y audita la notificacion para {0}")
    @CsvSource({
            "ana@example.com, Bienvenida, Hola Ana",
            "equipo@example.com, Alerta, Servicio disponible",
            "usuario@example.com, '', ''"
    })
    @DisplayName("Envia los datos sin modificarlos y audita solo despues del envio")
    void sendsThenAudits(String to, String subject, String body) {
        service.notify(to, subject, body);

        InOrder order = inOrder(emailSender, auditLog);
        order.verify(emailSender).send(to, subject, body);
        order.verify(auditLog).record(to, subject);
        verifyNoMoreInteractions(emailSender, auditLog);
    }

    @Test
    @DisplayName("Propaga el error del envio sin registrar una auditoria de exito")
    void propagatesSenderFailureWithoutAuditing() {
        IllegalStateException failure = new IllegalStateException("Envio fallido");
        doThrow(failure).when(emailSender).send("ana@example.com", "Alerta", "Mensaje");

        assertThatThrownBy(() -> service.notify("ana@example.com", "Alerta", "Mensaje"))
                .isSameAs(failure);

        verify(emailSender).send("ana@example.com", "Alerta", "Mensaje");
        verifyNoInteractions(auditLog);
        verifyNoMoreInteractions(emailSender);
    }

    @Test
    @DisplayName("Propaga el error de auditoria sin repetir el envio")
    void propagatesAuditFailureWithoutResending() {
        IllegalStateException failure = new IllegalStateException("Auditoria no disponible");
        doThrow(failure).when(auditLog).record("ana@example.com", "Alerta");

        assertThatThrownBy(() -> service.notify("ana@example.com", "Alerta", "Mensaje"))
                .isSameAs(failure);

        InOrder order = inOrder(emailSender, auditLog);
        order.verify(emailSender).send("ana@example.com", "Alerta", "Mensaje");
        order.verify(auditLog).record("ana@example.com", "Alerta");
        verifyNoMoreInteractions(emailSender, auditLog);
    }
}
