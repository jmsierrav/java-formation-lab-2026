package com.indra.notifications.infrastructure.adapters.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@DisplayName("SmtpEmailSender")
class SmtpEmailSenderTest {

    @ParameterizedTest(name = "{index}: simula SMTP con {3} reintentos configurados")
    @CsvSource({
            "ana@example.com, Bienvenida, Hola Ana, 10",
            "equipo@example.com, Alerta, Servicio disponible, 3",
            "usuario@example.com, '', '', 0"
    })
    @DisplayName("Simula SMTP imprimiendo el mensaje y los reintentos sin conectarse a la red")
    void logsSimulatedSmtpEmail(String to, String subject, String body, int retries, CapturedOutput output) {
        SmtpEmailSender sender = new SmtpEmailSender();
        ReflectionTestUtils.setField(sender, "retries", retries);

        sender.send(to, subject, body);

        assertThat(output.getOut()).isEqualTo(
                "[SMTP] Conectando a servidor real y enviando a " + to + ": " + subject + " -> " + body
                        + System.lineSeparator()
                        + "[SMTP] Número de reintentos configurado: " + retries + System.lineSeparator());
    }
}
