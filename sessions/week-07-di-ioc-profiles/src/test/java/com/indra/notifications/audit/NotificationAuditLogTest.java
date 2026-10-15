package com.indra.notifications.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@DisplayName("NotificationAuditLog")
class NotificationAuditLogTest {

    @ParameterizedTest(name = "{index}: registra el destinatario {0}")
    @ValueSource(strings = {"ana@example.com", "equipo@example.com", ""})
    @DisplayName("Registra exactamente el destinatario y el asunto")
    void recordsRecipientAndSubject(String to, CapturedOutput output) {
        new NotificationAuditLog().record(to, "Alerta");

        assertThat(output.getOut()).isEqualTo(
                "[AUDIT] Notificación registrada para " + to + " - Alerta" + System.lineSeparator());
    }
}
