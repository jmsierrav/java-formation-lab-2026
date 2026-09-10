# Prompt: Crear repositorio java-formation-lab-2026

## Contexto
Plan de formación Java 2026 para comunidad Indra Colombia (~200 personas).
Basado en resultados del test QC 2026 COL y CA&C. GitHub user: robinson8406

Modelo: **formación práctica y estandarizada con repo único**.
Sesiones **autocontenidas** (week-xx-start / week-xx-solution) para que las ausencias no bloqueen.
Cada jueves, 1 hora, **1 reto intermedio único** para todos los seniorities.

---

## Evidencia QC 2026 — Brechas y fortalezas

### Brechas clave (menor % → mayor prioridad)
| Área | Score QC |
|------|----------|
| Azure DevOps | 60% |
| Hibernate/ORM | 64% |
| Cloud Azure | 76% |
| SOLID + TDD | 77% |
| Spring avanzado / Testing | 77–83% / 79% |
| Microservicios | 86% |

### Fortalezas (no priorizar)
Java EE básico, Spring básico, Hibernate básico, SQL, Git, Maven

### Lo que la comunidad quiere fortalecer (encuesta)
| Deseo | % |
|-------|---|
| Microservicios y arquitecturas en la nube | 23% |
| Familiaridad con herramientas de CI/CD | 15% |
| Desarrollo de apps web con Spring Framework | 13% |
| Mejora en pruebas unitarias y de integración | 13% |
| Dominio de Java avanzado / nuevas características | 11% |
| Habilidades de diseño y patrones de software | 9% |
| Optimización de rendimiento y gestión de memoria | 8% |

### Áreas de mejora por seniority (resumen)
Las áreas con mayor necesidad de formación en niveles **Básico/Intermedio**:
Azure DevOps, Docker, Hibernate, Microservicios, JUnit, Mockito, Patrones de diseño, Spring Framework, Arquitectura Hexagonal/Limpia, Inyección de dependencias.

---

## Filosofía del reto
**Un único reto intermedio por sesión para todos los seniorities.**
- Nivel calibrado: accesible para junior-avanzado, retador para senior/experto.
- Seniors/expertos demuestran madurez en **CÓMO** resuelven (diseño, pruebas, patrones), no en **QUÉ** resuelven.
- Cada reto incluye siempre: **1 cambio funcional + 1 prueba + 1 criterio no funcional** (calidad / performance / operación / seguridad).
- Esto elimina la sobrecarga de mantener N versiones del mismo problema.

---

## Formato de sesión (1 hora, jueves)
El reto ya no se resuelve en vivo: se resuelve entre semana y se revisa la sesión siguiente.

| Segmento | Tiempo | Contenido |
|----------|--------|-----------|
| Revisión solución anterior | 0–20' | Se muestra y explica `week-(XX-1)-solution`, dudas del reto ya entregado |
| Contexto + demo del reto nuevo | 20–40' | Qué vamos a mejorar, por qué, demo del `week-xx-start` |
| Q&A | 40–60' | Dudas sobre el reto de la semana antes de resolverlo entre semana |

> `week-xx-start` se publica el jueves de la sesión; PR se entrega antes del jueves siguiente, donde se revisa como `week-xx-solution`.

---

## Calendario completo (jueves, 2026-07-16 → 2026-11-12)

### Paquete: Arranque
| # | Fecha | Sesión | Objetivo |
|---|-------|--------|----------|
| 01 | 2026-07-16 | Kickoff + setup Java 21 | Ejecutar repo + week-start/solution |

### Paquete: SOLID + TDD
| # | Fecha | Sesión | Objetivo |
|---|-------|--------|----------|
| 02 | 2026-07-23 | TDD con JUnit 5 | Requerimiento → tests |
| 03 | 2026-07-30 | SOLID (SRP/OCP) | Refactor con mantenibilidad |
| 04 | 2026-08-06 | Patrones (Strategy/Factory) | Desacoplar y testear mejor |
| 05 | 2026-08-13 | Mockito (buenas prácticas) | Tests confiables (sin fragilidad) |

### Paquete: Spring Web / Testing
| # | Fecha | Sesión | Objetivo |
|---|-------|--------|----------|
| 06 | 2026-08-20 | REST limpio (DTO/errores) | API consistente y validada |
| 07 | 2026-08-27 | DI/IoC (profiles/wiring) | Configuración correcta por entorno |
| 08 | 2026-09-03 | WebMvcTest (slice tests) | Pruebas rápidas de controllers |

### Paquete: Hibernate / JPA
| # | Fecha | Sesión | Objetivo |
|---|-------|--------|----------|
| 09 | 2026-09-10 | Mapeos JPA | Modelo persistente correcto |
| 10 | 2026-09-17 | Transacciones | Evitar fallos "local vs prod" |
| 11 | 2026-09-24 | Performance ORM | Eliminar N+1 / optimizar |
| 12 | 2026-10-01 | JPQL + paginación + locking | Consultas robustas y escalables |

### Paquete: Micro + Cloud + DevOps
| # | Fecha | Sesión | Objetivo |
|---|-------|--------|----------|
| 13 | 2026-10-08 | Microservicios | Límites + contratos claros |
| 14 | 2026-10-15 | Docker + compose | Entorno reproducible dev/CI |
| 15 | 2026-10-22 | Azure DevOps Pipelines (CI) | Build + tests + quality gate |
| 16 | 2026-10-29 | Azure DevOps Release | Entrega controlada por entorno |
| 17 | 2026-11-05 | Azure App Service | Deploy + config + troubleshooting |
| 18 | 2026-11-12 | Observabilidad + cierre | Logs + correlation + release |

*Total: 18 sesiones semanales (jul–nov). Sesiones eliminadas por ser fortalezas o menor impacto en brechas QC: Git/PR hygiene, Maven multi-módulo, DataJpaTest, Spring Security, OpenAPI, Projections/read models.*

---

## Reglas de juego
- Cada sesión tiene rama `week-XX-start` (código inicial) y `week-XX-solution` (referencia).
- PR obligatorio para entregar el reto + peer review de 1 compañero.
- Badge por sesión completada (issue con label `✅ completado`).
- Sin PR aprobado = la sesión queda pendiente (no bloquea asistencia, sí el badge).
- Sesiones autocontenidas: quien no asistió puede resolver con `week-XX-start` + solución publicada.

---

## Estructura de carpetas
```
java-formation-lab-2026/
├── README.md
├── REGLAS_DE_JUEGO.md
├── CALENDARIO_2026.md
├── .github/
│   ├── PULL_REQUEST_TEMPLATE.md
│   ├── ISSUE_TEMPLATE/badge-completado.md
│   └── labels.yml
├── docs/
│   ├── analisis-qc-2026.md
│   ├── formato-sesion.md
│   └── onboarding-java21.md
└── sessions/
    ├── week-01-kickoff/
    │   ├── README.md          ← contexto de la sesión
    │   ├── enunciado.md       ← reto intermedio único
    │   ├── criterios-evaluacion.md
    │   └── solucion-referencia/.gitkeep
    └── [misma estructura week-02 … week-18]
```

> ⚠️ Sin carpeta `tracks/` por seniority. El nivel se evidencia en la calidad de la solución.

---

## Contenido de enunciado.md (estructura estándar)
```markdown
## Contexto del reto
[Escenario de negocio realista, 5-8 líneas]

## Lo que debes implementar
[3-5 requerimientos funcionales concretos]

## Restricciones técnicas (para todos)
- Mínimo 1 test por clase nueva
- Sin lógica de negocio en controladores
- [1 criterio no funcional: calidad / performance / operación / seguridad]

## Criterio de aceptación del PR
[Checklist que el reviewer debe validar]

## Bonus (opcional, no obligatorio)
[1-2 desafíos extra para quien quiera ir más lejos]
```

---

## Instrucciones de creación local
```bash
# 1. Crear e inicializar
mkdir java-formation-lab-2026 && cd java-formation-lab-2026
git init && git branch -M main

# 2. Estructura de carpetas
mkdir -p .github/ISSUE_TEMPLATE docs
for week in $(seq -w 1 18); do
  mkdir -p sessions/week-${week}/solucion-referencia
  touch sessions/week-${week}/solucion-referencia/.gitkeep
done

# 3. Conectar con GitHub (crear repo vacío primero en github.com)
git remote add origin https://github.com/robinson8406/java-formation-lab-2026.git

# 4. Primer commit
git add .
git commit -m "chore: estructura inicial del repositorio de formación Java 2026"
git push -u origin main
```

---

## Convenciones de commit (Conventional Commits)
feat: | fix: | docs: | chore: | session: | refactor:

## Convención de ramas
- `main` — estado estable del repo
- `week-XX-start` — código inicial de cada sesión (se publica antes del jueves)
- `week-XX-solution` — solución de referencia (se publica al cierre, min 55')
- `week-XX/<alias-participante>` — rama personal para entregar el reto

---

## .gitignore mínimo
```
*.class
target/
.idea/
*.iml
.DS_Store
```

---

## README.md debe incluir
- Badges: Java 21, Spring Boot 3.x, Azure DevOps
- Explicación del modelo "un reto para todos" + formato de sesión (4 segmentos)
- Tabla de calendario completo con paquete, sesión y estado (⏳/✅)
- Link a REGLAS_DE_JUEGO.md y docs/onboarding-java21.md
- Sección "¿Cómo empiezo?" con 3 pasos

---

## Próximos pasos (hoja de ruta operativa)
1. **Seleccionar formadores** (5–8 personas): definir responsabilidades — contenido, repo, CI/CD, App Service y comunicación.
2. **Crear el repo en GitHub** con estructura `week-XX-start` / `week-XX-solution` + guía de onboarding Java 21.
3. **Conectar Azure DevOps Pipelines**: build + tests + quality gate (costos controlados con self-hosted agent o ejecución limitada).
4. **Publicar el calendario** 2026-05-21 → 2026-11-12 y ejecutar sesiones con reto intermedio único por jueves (autocontenidas para ausencias).
5. **Monitorear adopción**: pipeline verde, % de sesiones completadas, temas con más fallos → ajustar backlog de sesiones según evidencia.

---

## Stack
Java 21 | Spring Boot 3.x | Hibernate 6 | JUnit 5 | Mockito | Azure DevOps | Docker | Azure App Service
Fortalezas (no priorizar): Java EE básico, SQL, Git, Maven