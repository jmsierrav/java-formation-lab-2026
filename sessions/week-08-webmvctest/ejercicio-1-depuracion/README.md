# Ejercicio 1 — Depuración y refactor: catálogo de proveedores

> **Tipo**: código terminado que **falla**. Tu trabajo es diagnosticar, corregir y luego refactorizar.

## Contexto

El equipo de Compras publicó la API de proveedores (`/api/suppliers`) con sus slice tests. El desarrollador
original se fue de vacaciones y dejó el PR "terminado", pero el pipeline está en rojo. Además, el arquitecto
dejó un comentario en el PR: *"funciona, pero no pasaría una revisión de diseño"*.

```
com.indra.catalog.suppliers
├── SupplierCatalogApplication.java
├── domain/          → Supplier, SupplierRepository, SupplierNotFoundException
├── application/     → SupplierService, SupplierServiceImpl, CreateSupplierCommand
├── infrastructure/  → InMemorySupplierRepository
└── web/             → SupplierController, SupplierWebMapper, ApiExceptionHandler, dto/
```

## Cómo ejecutarlo

```bash
cd sessions/week-08-webmvctest
mvn -pl ejercicio-1-depuracion test
```

## Parte A — Encuentra y corrige los fallos (obligatorio)

Hay **4 fallos** que hacen que `SupplierControllerTest` no pase. Aparecen en cascada: al corregir el
primero se revelan los siguientes.

Reglas:

- **No** cambies `@WebMvcTest` por `@SpringBootTest` para "hacerlo pasar".
- **No** debilites las aserciones de los tests (cambiar el status esperado, borrar un `andExpect`, etc.).
  El contrato que validan los tests es correcto; si crees que un test está mal, argumenta por qué en `HALLAZGOS.md`.
- Cada fallo corregido debe ir en **un commit separado** (`fix: ...`) para que el reviewer pueda seguirlo.

## Parte B — Refactoriza con SOLID y buenas prácticas (obligatorio para Senior/Experto)

Con los tests en verde, refactoriza. El código tiene problemas de diseño que **no** rompen los tests. Pistas
(sin decirte dónde ni cómo):

| Área | Pregunta guía |
|------|---------------|
| DIP | ¿De qué depende el controller: de una abstracción o de una implementación? ¿Y el servicio? |
| SRP | ¿Hay reglas de negocio viviendo en la capa web? |
| Dominio | ¿Puede un `Supplier` existir en un estado inválido? ¿Quién protege sus invariantes? |
| Seguridad | ¿Qué ve un cliente externo cuando ocurre un error inesperado? |
| Diseño web | ¿El mapper necesita ser un bean de Spring? ¿Qué implica eso para el slice test? |
| Tests | ¿Los nombres de los tests documentan el comportamiento? ¿Las aserciones son frágiles? ¿Se verifica la interacción con el servicio cuando el body está vacío? |

> 💡 Al mover lógica fuera del controller, algún test del controller **debe** cambiar. Pregúntate en qué
> capa debe probarse cada regla y crea el test unitario que corresponda.

### Responsabilidades tras el refactor

- El controller depende de `SupplierService` y solo adapta HTTP; `SupplierWebMapper` es una utilidad
  sin estado ni bean de Spring, por lo que el slice no necesita `@Import`.
- `SupplierServiceImpl` recibe `SupplierRepository` por constructor, normaliza razón social
  (`Locale.ROOT`) y NIT y crea los metadatos antes de persistir. Estas reglas se prueban con
  `SupplierServiceImplTest`, sin levantar Spring.
- `Supplier` es inmutable y exige identificador, razón social, NIT canónico y correo con estructura
  básica válida. `SupplierTest` comprueba sus invariantes incluso fuera de la API.
- `SupplierControllerTest` verifica contratos HTTP, delegación de datos sin modificar, rechazo de
  bodies vacíos sin llamar al servicio y respuestas 500 genéricas. El detalle técnico solo se registra
  en el servidor. Los errores de dominio y los bodies ausentes o ilegibles devuelven 400.

## Entregable: `HALLAZGOS.md`

Crea `ejercicio-1-depuracion/HALLAZGOS.md` con esta estructura:

```markdown
## Fallo N — <título corto>
- **Síntoma**: qué test falla y con qué mensaje
- **Causa raíz**: por qué ocurre (no solo "faltaba X")
- **Corrección**: qué cambiaste y por qué esa opción y no otra
- **Prevención**: qué práctica o test evita que vuelva a ocurrir

## Refactors aplicados
| Principio / práctica | Antes | Después | Test que lo respalda |
```
