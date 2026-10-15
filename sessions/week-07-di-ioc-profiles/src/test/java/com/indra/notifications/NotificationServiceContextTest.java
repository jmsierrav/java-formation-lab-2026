package com.indra.notifications;

import com.indra.notifications.application.ports.output.EmailSender;
import com.indra.notifications.application.services.NotificationService;
import com.indra.notifications.infrastructure.adapters.email.FakeEmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(properties = {
        "notification.sender=fake",
        "notification.retry-attempts=1"
})
@Import(NotificationServiceContextTest.TestBeans.class)
@ExtendWith(OutputCaptureExtension.class)
@DisplayName("Contexto de NotificationService")
class NotificationServiceContextTest {

    private final NotificationService service;
    private final EmailSender emailSender;
    private final ApplicationContext context;

    @BeforeEach
    void clearPreviousInvocations() {
        clearInvocations(emailSender);
    }

    @Autowired
    NotificationServiceContextTest(NotificationService service, EmailSender emailSender,
                                   ApplicationContext context) {
        this.service = service;
        this.emailSender = emailSender;
        this.context = context;
    }

    @ParameterizedTest(name = "{index}: delega el envío a {0}")
    @CsvSource({
            "user@example.com, Aviso, Mensaje",
            "team@example.com, Alerta, Servicio disponible"
    })
    @DisplayName("Delega al bean de prueba y registra la auditoría")
    void delegatesToTestBeanAndRecordsAudit(String recipient, String subject, String message,
                                            CapturedOutput output) {
        assertThat(context.getBeansOfType(EmailSender.class)).hasSize(2);
        assertThat(context.getBean(EmailSender.class)).isSameAs(emailSender);
        assertThat(context.getBeansOfType(FakeEmailSender.class)).hasSize(1);

        service.notify(recipient, subject, message);

        verify(emailSender).send(recipient, subject, message);
        verifyNoMoreInteractions(emailSender);
        assertThat(output.getOut())
                .contains("[AUDIT] Notificación registrada para " + recipient + " - " + subject)
                .doesNotContain("[FAKE]", "[SMTP]");
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestBeans {

        @Bean
        @Primary
        EmailSender testEmailSender() {
            return mock(EmailSender.class);
        }
    }
}
