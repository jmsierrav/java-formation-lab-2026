# Enunciado — Week 08: WebMvcTest (Slice Tests)

## Contexto del reto

El área de Catálogo tiene dos APIs REST: **proveedores** y **productos**. Los tests actuales usan
`@SpringBootTest` y levantan el contexto completo, lo que hace que el pipeline tarde 4 minutos en la fase de
test. Además, la API de proveedores llegó "terminada" pero con el pipeline en rojo y con deuda de diseño.

Esta semana trabajas en dos frentes:

| Ejercicio | Carpeta | Tipo | Qué demuestras |
|-----------|---------|------|----------------|
| 1 | `ejercicio-1-depuracion/` | Código terminado que **falla** | Diagnóstico de slice tests + refactor SOLID |
| 2 | `ejercicio-2-construccion/` | Construcción **desde el `main`** | Diseño hexagonal + slice tests + estrategia de testing |

Cada ejercicio tiene su propio `README.md` con el detalle.

## Lo que debes implementar

### Ejercicio 1 — Depuración y refactor (`SupplierController`)

1. Corregir los **4 fallos** que impiden que `SupplierControllerTest` pase, sin cambiar a `@SpringBootTest`
   y sin debilitar las aserciones.
2. Refactorizar aplicando SOLID (DIP, SRP, dominio que protege sus invariantes) y buenas prácticas de testing.
3. Documentar diagnóstico y refactors en `HALLAZGOS.md`.

### Ejercicio 2 — Construcción (`ProductController`)

Con `@WebMvcTest(ProductController.class)` y `@MockBean` para `ProductService`:

1. Test: `GET /api/products/{id}` con producto existente → `200` con body correcto (verificar `jsonPath`).
2. Test: `GET /api/products/{id}` con producto inexistente → `404` (service lanza `ProductNotFoundException`).
3. Test: `POST /api/products` con body válido → `201` con `Location` header.
4. Test: `POST /api/products` con body inválido (nombre vacío) → `400` con mensaje de error.
5. Test: `DELETE /api/products/{id}` → `204 No Content`.

Y el código de producción completo siguiendo la arquitectura hexagonal descrita en
`ejercicio-2-construccion/README.md`, con seguridad HTTP Basic y `ESTRATEGIA_TESTING.md`.

## Restricciones técnicas (para todos)

- Mínimo 1 test por clase nueva.
- Sin lógica de negocio en controladores.
- Usar `@WebMvcTest` — **no** `@SpringBootTest` — para los tests de controller.
- Usar `@MockBean` para el service — no instanciar el service real en el slice test.
- Usar `MockMvc` con `perform/andExpect` — no `RestTemplate`.
- El controller depende de una **abstracción** (`ProductService` es una interfaz).
- **Criterio no funcional (performance)**: los tests de `ProductControllerTest` deben ejecutarse en menos de
  3 segundos en total (tiempo reportado por Surefire).

## Criterio de aceptación del PR

- [ ] Ejercicio 1: los 4 fallos corregidos en commits separados y `HALLAZGOS.md` completo
- [ ] Ejercicio 1: ningún test debilitado ni migrado a `@SpringBootTest`
- [ ] Ejercicio 2: 5 tests obligatorios implementados con `@WebMvcTest`
- [ ] Sin `@SpringBootTest` ni base de datos en los tests de controller
- [ ] `jsonPath` usado para verificar el body de la respuesta
- [ ] Header `Location` verificado en el test de creación
- [ ] Tiempo de ejecución < 3s (reportado en el PR)
- [ ] `mvn verify` en verde (ambos módulos)

## Bonus (opcional)

- Tests de seguridad del ejercicio 2 con `MockMvc.with(httpBasic(...))`: `401` anónimo y `403` sin rol `ADMIN`.
- Verificar el `Content-Type` de la respuesta en todos los tests.
- Agregar al ejercicio 1 un test de arquitectura (p. ej. con ArchUnit) que impida que `web` dependa de una
  implementación concreta del servicio.
