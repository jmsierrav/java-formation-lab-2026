# Criterios de evaluación — Week 08

## Checklist del reviewer

### Ejercicio 1 — Depuración (`SupplierController`)
- [ ] Los 4 fallos corregidos, uno por commit (`fix: ...`)
- [ ] Ningún test migrado a `@SpringBootTest` ni con aserciones debilitadas
- [ ] `HALLAZGOS.md` explica síntoma, causa raíz, corrección y prevención de cada fallo
- [ ] Controller depende de una interfaz de servicio (DIP)
- [ ] Normalización de datos fuera del controller y cubierta por un test unitario
- [ ] Errores inesperados no exponen detalles internos al cliente
- [ ] Tests con nombres descriptivos y sin aserciones del body como String

### Ejercicio 2 — Construcción (`ProductController`)

#### Setup
- [ ] `@WebMvcTest(ProductController.class)` en la clase de test
- [ ] `@MockBean ProductService` presente
- [ ] Sin `@SpringBootTest`, sin contexto de base de datos

#### Tests
- [ ] GET con producto existente → 200 + jsonPath correcto
- [ ] GET con producto inexistente → 404
- [ ] POST válido → 201 + header Location
- [ ] POST inválido → 400 con mensaje de error
- [ ] DELETE → 204

#### Arquitectura
- [ ] `domain` sin dependencias de Spring ni de `web`
- [ ] `application` no recibe DTOs web
- [ ] `ProductService` es una interfaz y el controller depende de ella
- [ ] Mínimo 1 test por clase nueva

### Calidad
- [ ] `jsonPath` usado para verificar campos del body
- [ ] Sin assertions manuales del body como String (usar jsonPath)
- [ ] Tiempo de ejecución < 3s (verificar con `mvn -pl ejercicio-2-construccion test -Dtest='ProductControllerTest*'`)
- [ ] `mvn verify` en verde

## Escala de madurez

| Junior | Semi-senior | Senior | Experto |
|--------|-------------|--------|---------|
| 3/5 tests con @WebMvcTest | 5 tests, jsonPath correcto, <3s | Content-Type, Location header, auth básica | Propone estrategia de testing: qué va en WebMvcTest vs unitario vs integración |

### Qué significa "Experto" en esta semana

Además de todo lo de Senior, el participante:

| Ejercicio | Evidencia esperada |
|-----------|--------------------|
| 1 | Explica la **causa raíz** de cada fallo en términos del funcionamiento del slice (qué beans carga `@WebMvcTest` y cuáles no), no solo el parche. |
| 1 | Refactor completo con SOLID: DIP en controller y servicio, SRP (reglas de negocio fuera de `web`), dominio que protege sus invariantes, handler de errores seguro. |
| 1 | Reubica los tests según la capa: la normalización se prueba en un unitario, el controller solo prueba traducción HTTP. |
| 2 | Arquitectura hexagonal con reglas de dependencia respetadas. |
| 2 | Seguridad probada en el slice (`401`/`403`) importando su propia configuración de seguridad y entendiendo por qué es necesario. |
| 2 | `ESTRATEGIA_TESTING.md` con pirámide, matriz de decisión justificada, mediciones reales y riesgos no cubiertos. |
| 2 | Un único `@SpringBootTest` como smoke test; el resto de casos de borde en niveles más bajos. |
