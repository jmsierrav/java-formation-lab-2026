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
@DisplayName("FakeEmailSender")
class FakeEmailSenderTest {

    @ParameterizedTest(name = "{index}: simula el envio con {3} reintentos configurados")
    @CsvSource({
            "ana@example.com, Bienvenida, Hola Ana, 3",
            "equipo@example.com, Alerta, Servicio disponible, 10",
            "usuario@example.com, '', '', 0"
    })
    @DisplayName("Solo imprime el mensaje y los reintentos configurados")
    void logsSimulatedEmail(String to, String subject, String body, int retries, CapturedOutput output) {
        FakeEmailSender sender = new FakeEmailSender();
        ReflectionTestUtils.setField(sender, "retries", retries);

        sender.send(to, subject, body);

        assertThat(output.getOut()).isEqualTo(
                "[FAKE] Simulando envío a " + to + ": " + subject + " -> " + body + System.lineSeparator()
                        + "[FAKE] Número de reintentos configurado: " + retries + System.lineSeparator());
    }
}
