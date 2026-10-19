## Fallo 1 — Spring no puede crear SupplierController por falta de un bean
- **Síntoma**: fallan todos los tests con `Parameter 1 of constructor in com.indra.catalog.suppliers.web.SupplierController required a bean of type 'com.indra.catalog.suppliers.web.SupplierWebMapper' that could not be found.`
- **Causa raíz**: @WebMvcTest solo carga la capa web, no todos los componentes de la aplicación.
- **Corrección**: agregué la anotación `@Import(SupplierWebMapper.class)` a la clase SupplierControllerTest para cargar el bean SupplierWebMapper.
- **Prevención**: tener en cuenta que @WebMvcTest no carga todos los beans y usar @Import para los necesarios.

## Fallo 2 — URI mal formada en la respuesta de creación
- **Síntoma**: el test que verifica la ubicación del recurso creado falla.
- **Causa raíz**: se estaba construyendo la URI con `.path("{id}")` en lugar de `.path("/{id}")`.
- **Corrección**: cambié `.path("{id}")` por `.path("/{id}")` en SupplierController.
- **Prevención**: revisar las rutas y URI construidas en los controladores y tests.

## Fallo 3 — Validación de request fallida
- **Síntoma**: el test que verifica la creación de un proveedor con datos inválidos falla con un error de validación.
- **Causa raíz**: el request no estaba siendo validado automáticamente porque faltaba la anotación @Valid en el parámetro del método create.
- **Corrección**: agregué la anotación @Valid al parámetro CreateSupplierRequest en el método create de SupplierController.
- **Prevención**: siempre usar @Valid en los parámetros de los métodos de los controladores que reciben requests que deben ser validados.

## Fallo 4 — Respuesta de eliminación incorrecta
- **Síntoma**: el test que verifica la eliminación de un proveedor falla porque la respuesta no es la esperada.
- **Causa raíz**: el método delete estaba devolviendo `ResponseEntity.ok().build()` en lugar de `ResponseEntity.noContent().build()`.
- **Corrección**: cambié `ResponseEntity.ok().build()` por `ResponseEntity.noContent().build()` en el método delete de SupplierController.
- **Prevención**: asegurarse de que los métodos de eliminación devuelvan el código de estado HTTP correcto (204 No Content) según las convenciones REST.

## Refactors aplicados

| Principio / práctica | Antes | Después | Test que lo respalda |
|----------------------|-------|---------|---------------------|
| DIP en web | Controller acoplado a `SupplierServiceImpl` | Constructor recibe `SupplierService`; el slice mockea la interfaz | `delegatesUnmodifiedInputAndReturnsCreatedSupplierWithLocation` |
| DIP en aplicación | Servicio instancia `InMemorySupplierRepository` | Inyección de `SupplierRepository`; implementación registrada con `@Repository` | `SupplierServiceImplTest` usa un repositorio mock |
| SRP | Controller normaliza datos y crea notas | Servicio normaliza con `Locale.ROOT`, asigna ID y notas; web solo adapta DTOs | `normalizesNameAndTaxIdBeforeSavingSupplier` |
| Invariantes de dominio | Constructor vacío y setters permiten estados incompletos | `Supplier` inmutable y constructor con validación de campos requeridos, NIT canónico y estructura básica de correo | `SupplierTest` |
| Errores seguros | 500 contiene `exception.toString()` | Mensaje público genérico y detalle técnico en log del servidor | `returnsSafeErrorWhenAnUnexpectedExceptionOccurs` |
| Mapper sin framework | Bean sin estado que requiere `@Import` en el slice | Utilidad estática; eliminación del import y de la dependencia del constructor | `SupplierControllerTest` sin `@Import` |
| Tests expresivos y robustos | `test1`...`test5`, JSON comparado como String y dependencia del orden de errores | Nombres de comportamiento, `jsonPath` y búsqueda de errores sin depender del índice | `returnsSupplierWithoutInternalNotesWhenFound`, `rejectsBlankNameWithoutCallingService` |
| Body inválido | No se verifica delegación; el catch genérico convierte errores de lectura en 500 | Handler de body ausente/ilegible devuelve 400 y no invoca el servicio | `rejectsMissingOrInvalidBodyWithoutCallingService` |
| Validación tras normalizar | El patrón web permite un NIT compuesto solo de puntos | El dominio rechaza el NIT inválido antes de persistir y la web lo traduce a 400 | `rejectsInvalidTaxIdAfterNormalizationWithoutCallingRepository`, `returnsValidationErrorWhenDomainRejectsSupplier` |

La prueba de normalización pertenece a aplicación porque es una regla compartida por cualquier
entrada al caso de uso. El slice no ejecuta esa regla: verifica que los datos originales llegan al
servicio y que la respuesta devuelta por este se adapta al contrato HTTP. Las notas ya no mencionan
el canal API, pues su creación pertenece al caso de uso y no al transporte.
