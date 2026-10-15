package com.indra.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@DisplayName("Punto de entrada de NotificationApplication")
class NotificationApplicationTest {

    @Test
    @DisplayName("La clase de configuracion puede instanciarse")
    void createsApplicationConfiguration() {
        assertThat(new NotificationApplication()).isNotNull();
    }

    @Test
    @DisplayName("Main delega el arranque y los argumentos a Spring Boot")
    void delegatesStartupToSpringBoot() {
        String[] args = {"--spring.profiles.active=dev"};

        try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
            NotificationApplication.main(args);

            application.verify(() -> SpringApplication.run(NotificationApplication.class, args));
            application.verifyNoMoreInteractions();
        }
    }
}
