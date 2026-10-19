# Week 08 — WebMvcTest: Slice Tests de Controllers

**Fecha**: 2026-10-08 · **Paquete**: Spring Web / Testing

## Objetivo

Probar controllers REST con `@WebMvcTest` y `MockMvc` sin levantar el contexto completo de Spring: tests rápidos, enfocados y que no requieren base de datos.

## Contexto técnico

**Brecha QC atacada**: Testing (79%)  
Los tests de controller con `@SpringBootTest` tardan 15–30 segundos. Con `@WebMvcTest` tardan menos de 2. Esta diferencia define si el equipo ejecuta los tests frecuentemente o los evita.

## Estructura

```
week-08-webmvctest/
├── pom.xml                      ← agregador (el pipeline ejecuta mvn verify aquí)
├── enunciado.md                 ← reto de la semana
├── criterios-evaluacion.md      ← checklist del reviewer + escala de madurez
├── ejercicio-1-depuracion/      ← API de proveedores: terminada pero falla → corregir + refactorizar
└── ejercicio-2-construccion/    ← API de productos: solo el main → construir con arquitectura hexagonal
```

| Ejercicio | Punto de partida | Resultado esperado |
|-----------|------------------|--------------------|
| 1 — Depuración | Código completo con 4 fallos y deuda de diseño | Tests en verde, refactor SOLID, `HALLAZGOS.md` |
| 2 — Construcción | `ProductCatalogApplication.java` | API completa, slice tests, unitarios, `ESTRATEGIA_TESTING.md` |

## Cómo ejecutar

```bash
cd sessions/week-08-webmvctest
mvn verify                                   # ambos ejercicios (lo que corre el pipeline)
mvn -pl ejercicio-1-depuracion test          # solo ejercicio 1
mvn -pl ejercicio-2-construccion test        # solo ejercicio 2
```

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-07-solution`: 12-factor app aplicado |
| 20–40' | Contexto + demo: @SpringBootTest vs @WebMvcTest, diferencia de tiempo — se ejecuta el ejercicio 1 en vivo para ver el primer fallo y qué carga (y qué no) un slice test (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
