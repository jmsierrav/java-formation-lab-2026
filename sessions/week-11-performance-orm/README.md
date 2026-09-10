# Week 11 — Performance ORM: Eliminar N+1

**Fecha**: 2026-09-10 · **Paquete**: Hibernate / JPA

## Objetivo

Detectar y corregir el problema N+1 usando `JOIN FETCH`, `@EntityGraph` y batch size. Medir el impacto antes y después.

## Contexto técnico

**Brecha QC atacada**: Hibernate/ORM (64%)  
El N+1 es el problema de performance más frecuente en aplicaciones con ORM. Es fácil de introducir y difícil de detectar sin las herramientas correctas. Esta sesión enseña a detectarlo y eliminarlo de forma sistemática.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-10-solution`: checklist de transacciones |
| 20–40' | Contexto + demo: habilitar log de SQL, ver N+1 en acción — detectar y corregir N+1 en listado de órdenes (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
