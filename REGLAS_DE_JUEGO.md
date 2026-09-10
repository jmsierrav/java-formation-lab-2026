# Reglas de juego — java-formation-lab-2026

## Modelo de participación

Este repositorio sigue el modelo de **sesiones autocontenidas**: cada semana es independiente, diseñada para que quien no asistió pueda completarla con el material publicado.

> **Cada participante trabaja desde su propio fork.** No se requieren permisos en el repo central.

---

## Configuración inicial (una sola vez)

```bash
# 1. Hacer fork desde https://github.com/robinson8406/java-formation-lab-2026
#    (botón Fork en GitHub)

# 2. Clonar tu fork
git clone https://github.com/tu-alias/java-formation-lab-2026.git
cd java-formation-lab-2026

# 3. Agregar el repo central como upstream
git remote add upstream https://github.com/robinson8406/java-formation-lab-2026.git
```

---

## Ramas y flujo de trabajo

| Rama | Propósito | Quién la crea |
|------|-----------|---------------|
| `main` | Estado estable del repo | Formadores |
| `week-XX-start` | Código inicial de cada sesión | Formadores (jueves de la sesión) |
| `week-XX-solution` | Solución de referencia | Formadores (jueves siguiente, al inicio de esa sesión) |
| `week-XX/<tu-alias>` | Tu entrega personal | Participante (antes del jueves siguiente) |

### Flujo para participar en una sesión

```bash
# 1. Trae las ramas nuevas del repo central (upstream)
git fetch upstream

# 2. Crea tu rama personal directamente desde el start del formador
git checkout -b week-XX/tu-alias upstream/week-XX-start

# 3. Resuelve el reto
# ... implementa, escribe tests, verifica mvn verify verde localmente ...

# 4. Haz push a TU FORK y abre un PR cross-fork
git push origin week-XX/tu-alias
# PR: TU-ALIAS/java-formation-lab-2026:week-XX/tu-alias
#  → robinson8406/java-formation-lab-2026:week-XX-start
```

Abre el PR **desde tu fork hacia el repo central** y asigna a **1 compañero** como reviewer.

> ⚠️ **Los PRs nunca se mergean.** Sirven para: ejecutar CI · peer review · badge. La rama `week-XX-start` queda congelada como skeleton para todos.

---

## PR y peer review

- Usa el template en `.github/PULL_REQUEST_TEMPLATE.md`.
- El reviewer debe validar el checklist de `criterios-evaluacion.md` de la sesión.
- **Sin PR aprobado = sesión pendiente** (no bloquea asistencia ni acceso a la siguiente sesión; sí bloquea el badge).

---

## Badges

Al completar una sesión con PR aprobado, abre un issue con:
- **Título**: `✅ [week-XX] tu-alias completó la sesión`
- **Label**: `completado`
- El formador responsable cerrará el issue como evidencia.

---

## Convenciones de commit

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

```
feat:     nueva funcionalidad
fix:      corrección de bug
test:     añadir o modificar tests
refactor: refactorización sin cambio de comportamiento
docs:     documentación
chore:    tareas de mantenimiento (deps, config)
session:  cambios propios de una sesión de formación
```

---

## Convenciones de código

- Inyección **siempre por constructor** (nunca `@Autowired` en campo).
- Sin lógica de negocio en controladores — solo delegación.
- Mínimo **1 test por clase nueva** introducida en el reto.
- Nombres en inglés para código; español para docs y comentarios del reto.

---

## Preguntas frecuentes

**¿Puedo resolver sesiones pasadas?** Sí, `git fetch upstream` y creá tu rama desde el `week-XX-start` correspondiente.  
**¿Puedo mirar la solución antes de intentarlo?** Está publicada, pero te privás del aprendizaje 🙂  
**¿Qué stack necesito tener instalado?** Ver [docs/onboarding-java21.md](docs/onboarding-java21.md).  
**¿Necesito permisos en el repo central?** No. Solo necesitás tu cuenta GitHub gratuita y hacer fork.

---

## Para formadores

### Estructura de cada sesión

Cada sesión dura **1 hora** y sigue este formato fijo. El reto **ya no se resuelve en vivo**: se resuelve entre semana y se revisa en la sesión siguiente.

| Segmento | Tiempo | Contenido |
|----------|--------|-----------|
| Revisión de la solución anterior | 0–20' | Se muestra y explica `week-(XX-1)-solution`, se resuelven dudas del reto ya entregado |
| Contexto + demo del reto nuevo | 20–40' | Qué vamos a mejorar, por qué importa, demo rápida del `week-XX-start` |
| Q&A | 40–60' | Dudas sobre el reto de la semana antes de resolverlo entre semana |

> El `week-XX-start` se publica el jueves de la sesión. El PR con el reto resuelto se entrega **antes del jueves siguiente**, cuando se revisa en vivo como `week-XX-solution`.

### Filosofía del código inicial (`week-XX-start`)

El `week-XX-start` **nunca llega vacío**. Llega con el andamiaje necesario para que el reto quepa en 35 minutos:

| Qué viene dado | Qué hace el participante |
|----------------|--------------------------|
| Proyecto compilando con dependencias correctas | Implementa la lógica del hueco concreto |
| Entidades / interfaces / clases base ya creadas | Completa los métodos vacíos o `// TODO` |
| Tests de andamiaje (compilando, fallando en rojo) | Hace los tests pasar en verde |
| Estructura de paquetes definida | Sigue la convención, no decide la arquitectura base |

**Regla de oro**: si al abrir el `week-XX-start` un participante tarda más de 2 minutos en entender qué tiene que hacer, el start está incompleto.

### Andamiaje por sesión (semanas clave)

| Semana | Qué viene dado en el start |
|--------|---------------------------|
| 03 | `StockValidator` y `OrderNotifier` ya extraídos; falta `DiscountCalculator` y el tipo `LOYALTY` |
| 04 | `ShippingStrategy` + `StandardShipping` ya implementados; faltan `ExpressShipping`, `StorePickup` y `Factory` |
| 10 | `TransferService` con `@Transactional` básico; falta `rollbackFor` y `TransferAuditService` con `REQUIRES_NEW` |
| 12 | Query JPQL sin `Pageable` y entidad con `@Version` ya presente; falta paginación y manejo de `OptimisticLockException` |
| 13 | Proyecto `notification-service` con `pom.xml` y clase `main` creados; falta el endpoint y la integración en el monolito |

### Criterio de aceptación del reto

Cada reto incluye siempre:
1. **1 cambio funcional** — historia de usuario o requerimiento concreto.
2. **1 prueba** — al menos 1 test que valide el comportamiento esperado.
3. **1 criterio no funcional** — calidad, performance, operación o seguridad.

### Escala de madurez esperada

| Seniority | Expectativa |
|-----------|-------------|
| Junior | Implementa los requerimientos funcionales, tests básicos |
| Semi-senior | Aplica principios SOLID, tests significativos |
| Senior | Diseño desacoplado, tests robustos, criterio no funcional cubierto |
| Experto | Identifica trade-offs, propone mejoras, deja el código mejor de como lo encontró |

