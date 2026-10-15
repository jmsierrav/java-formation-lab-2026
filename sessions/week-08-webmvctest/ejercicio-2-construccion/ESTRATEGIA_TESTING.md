# Estrategia de testing — Catálogo de productos

> Completa esta plantilla. Es el entregable que diferencia el nivel **Experto**: no basta con que los
> tests pasen, hay que justificar **qué se prueba dónde y por qué**.

## 1. Pirámide propuesta

| Nivel | Herramienta | Qué valida | Qué NO valida | Nº de tests | Tiempo aprox. |
|-------|-------------|------------|---------------|-------------|---------------|
| Unitario (dominio) | JUnit 5 | Invariantes de `Product`: identidad, nombre, precio exacto y stock | Spring, HTTP, persistencia | 4 | 0.018 s |
| Unitario (aplicación) | JUnit 5 + Mockito | IDs, delegación al puerto, búsqueda, borrado y ausencia de productos | Configuración Spring y adaptadores reales | 5 | 1.353 s |
| Slice web | `@WebMvcTest` + `MockMvc` | JSON, validación, códigos HTTP, `Location` y reglas de autenticación/autorización con la cadena de seguridad real | Repositorio y cableado de la aplicación | 7 | 0.994 s en `verify`; 4.848 s aislado |
| Integración | `@SpringBootTest` + `MockMvc` | Cableado real de API, servicio, repositorio en memoria y seguridad, de POST a DELETE | Persistencia durable, despliegue y carga | 1 | 3.340 s |

Los tiempos son los reportados por Surefire en una ejecución local; dependen de la máquina y de si
el contexto de Spring está caliente. El slice carga el contexto una vez, pero los siete escenarios
comparten ese contexto.

## 2. Matriz de decisión

Para cada regla, marca en qué nivel se prueba y justifica.

| Regla / comportamiento | Dominio | Aplicación | Slice web | Integración | Justificación |
|------------------------|:-------:|:----------:|:---------:|:-----------:|---------------|
| El precio debe ser > 0 | ✓ | ✓ | — | — | El modelo protege la invariante; el test de aplicación verifica que el objeto inválido no llega al puerto. La request también declara `@DecimalMin`. |
| El nombre vacío devuelve 400 | — | — | ✓ | — | Es una regla de entrada HTTP (`@NotBlank`); el slice verifica además el esquema de error. |
| Producto inexistente devuelve 404 | — | ✓ | ✓ | — | Aplicación valida la excepción de dominio; slice valida su traducción al contrato HTTP. |
| Se genera un id al crear | — | ✓ | ✓ | ✓ | Prueba unitaria de secuencia; slice e integración comprueban el resultado expuesto. |
| `Location` apunta al recurso creado | — | — | ✓ | ✓ | Es responsabilidad del adaptador web y se verifica en ambos niveles HTTP. |
| `DELETE` exige rol `ADMIN` | — | — | ✓ | ✓ | El slice importa la configuración de seguridad de producción; integración comprueba el recorrido completo con admin. |
| Las capas quedan cableadas | — | — | — | ✓ | El smoke test instancia la aplicación real y recorre el ciclo POST/GET/DELETE. |

## 3. Decisiones y trade-offs

- ¿Por qué `@MockBean` en el slice test y `@Mock` en el test del servicio?\
  `@MockBean` reemplaza el puerto de entrada `ProductService` dentro del contexto MVC. Así el slice
  prueba el controller, sus validaciones, el advice y la configuración de seguridad sin levantar
  servicio ni persistencia. `@Mock` de Mockito se usa en el test puro del servicio para sustituir
  `ProductRepository` sin iniciar Spring.
- ¿Qué pasa con el tiempo del pipeline si cada test de controller usa `@SpringBootTest`? (cita tus mediciones)\
  La ejecución medida de `ProductControllerTest` aislada tardó **4.848 s en Surefire** (7 tests);
  durante `verify` el slice tardó **0.994 s**. La suite completa tardó
  **11.933 s de Maven** (17 tests; 5.705 s acumulados por clase reportados
  por Surefire). Levantar un contexto completo para cada controller aumentaría el coste; por eso
  hay un solo smoke test de integración y las comprobaciones HTTP rutinarias usan slice.
- ¿Cómo aprovechas el caché de contexto de Spring entre clases de test? ¿Qué lo invalida?\
  Spring reutiliza un contexto entre tests con la misma configuración. Diferencias en clases
  importadas, propiedades, perfiles, mocks (`@MockBean`) o configuración dinámica pueden producir
  otra clave de caché y levantar un contexto nuevo. En particular el smoke test necesita el contexto
  completo y no comparte el slice MVC.
- ¿Qué riesgos quedan sin cubrir y cómo los cubrirías (contract tests, tests de carga, etc.)?\
  El adaptador actual es en memoria: no cubre durabilidad, concurrencia entre instancias, rollback
  ni consultas reales. Para evolucionar, añadir tests de integración contra el motor elegido,
  contract tests para consumidores y pruebas de carga para latencia/throughput. La autenticación
  Basic requiere TLS en despliegue y gestión de secretos en el entorno.

## 4. Mediciones

| Comando | Tests | Tiempo reportado por Surefire |
|---------|-------|-------------------------------|
| `mvn -pl ejercicio-2-construccion test -Dtest='ProductControllerTest*'` | 7, 0 fallos | 4.848 s |
| `mvn -pl ejercicio-2-construccion verify` | 17, 0 fallos | 5.705 s acumulados; 11.933 s total Maven |
