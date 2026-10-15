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
@ActiveProfiles("prod")
@ExtendWith(OutputCaptureExtension.class)
@DisplayName("Wiring de notificaciones con el perfil prod")
class NotificationProdProfileTest {

    private final ApplicationContext context;
    private final NotificationService notificationService;

    @Autowired
    NotificationProdProfileTest(ApplicationContext context, NotificationService notificationService) {
        this.context = context;
        this.notificationService = notificationService;
    }

    @Test
    @DisplayName("SMTP es el unico EmailSender activo y usa la configuracion prod")
    void activatesOnlySmtpWithProdConfiguration(CapturedOutput output) {
        assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("prod");
        assertThat(context.getBeansOfType(EmailSender.class)).hasSize(1);
        assertThat(context.getBean(EmailSender.class))
                .isSameAs(context.getBean(SmtpEmailSender.class));
        assertThat(context.getBeansOfType(FakeEmailSender.class)).isEmpty();

        notificationService.notify("prod@example.com", "Alerta prod", "Mensaje de prueba");

        assertThat(output.getOut())
                .contains("[SMTP] Conectando a servidor real y enviando a prod@example.com: Alerta prod -> Mensaje de prueba")
                .contains("[SMTP] Número de reintentos configurado: 3")
                .contains("[AUDIT] Notificación registrada para prod@example.com - Alerta prod")
                .doesNotContain("[FAKE]");
    }
}
