package com.indra.notifications;

import com.indra.notifications.application.ports.output.EmailSender;
import com.indra.notifications.application.services.NotificationService;
import com.indra.notifications.infrastructure.adapters.email.FakeEmailSender;
import com.indra.notifications.infrastructure.adapters.email.SmtpEmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(OutputCaptureExtension.class)
@DisplayName("Wiring de notificaciones con el perfil dev")
class NotificationDevProfileTest {

    private final ApplicationContext context;
    private final NotificationService notificationService;

    @Autowired
    NotificationDevProfileTest(ApplicationContext context, NotificationService notificationService) {
        this.context = context;
        this.notificationService = notificationService;
    }

    @Test
    @DisplayName("FakeEmailSender es el unico EmailSender activo y SMTP no esta registrado")
    void activatesOnlyFakeEmailSender() {
        assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("dev");
        assertThat(context.getBeansOfType(EmailSender.class)).hasSize(1);
        assertThat(context.getBean(EmailSender.class))
                .isSameAs(context.getBean(FakeEmailSender.class));
        assertThat(context.getBeansOfType(SmtpEmailSender.class)).isEmpty();
    }

    @Test
    @DisplayName("El servicio usa el fake con la configuracion dev y registra la auditoria")
    void notifiesUsingDevConfiguration(CapturedOutput output) {
        notificationService.notify("dev@example.com", "Alerta dev", "Mensaje de prueba");

        assertThat(output.getOut())
                .contains("[FAKE] Simulando envío a dev@example.com: Alerta dev -> Mensaje de prueba")
                .contains("[FAKE] Número de reintentos configurado: 10")
                .contains("[AUDIT] Notificación registrada para dev@example.com - Alerta dev")
                .doesNotContain("[SMTP]");
    }
}
