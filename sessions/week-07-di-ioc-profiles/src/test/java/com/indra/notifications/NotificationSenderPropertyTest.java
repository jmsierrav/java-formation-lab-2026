package com.indra.notifications;

import com.indra.notifications.application.ports.output.EmailSender;
import com.indra.notifications.application.services.NotificationService;
import com.indra.notifications.audit.NotificationAuditLog;
import com.indra.notifications.infrastructure.adapters.email.FakeEmailSender;
import com.indra.notifications.infrastructure.adapters.email.SmtpEmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Configuración de NotificationSender")
class NotificationSenderPropertyTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(FakeEmailSender.class, SmtpEmailSender.class,
                    NotificationService.class, NotificationAuditLog.class)
            .withPropertyValues("notification.retry-attempts=1");

    @ParameterizedTest(name = "{index}: selecciona {0} sin activar perfiles")
    @CsvSource({
            "fake, FakeEmailSender",
            "smtp, SmtpEmailSender"
    })
    @DisplayName("Selecciona el sender configurado sin activar perfiles")
    void selectsConfiguredSenderWithoutActiveProfile(String sender, String expectedSenderType) {
        contextRunner.withPropertyValues("notification.sender=" + sender).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(EmailSender.class);
            assertThat(context.getBean(EmailSender.class).getClass().getSimpleName())
                    .isEqualTo(expectedSenderType);
            assertThat(context.getEnvironment().getActiveProfiles()).isEmpty();
        });
    }

    @Test
    @DisplayName("Falla cuando falta la propiedad del sender")
    void failsWhenSenderPropertyIsMissing() {
        contextRunner.run(context -> assertThat(context).hasFailed()
                .getFailure().isInstanceOf(UnsatisfiedDependencyException.class)
                .hasMessageContaining(EmailSender.class.getName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "unknown"})
    @DisplayName("Falla cuando la propiedad del sender no es válida")
    void failsWhenSenderPropertyIsInvalid(String sender) {
        contextRunner.withPropertyValues("notification.sender=" + sender)
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().isInstanceOf(UnsatisfiedDependencyException.class)
                        .hasMessageContaining(EmailSender.class.getName()));
    }
}
