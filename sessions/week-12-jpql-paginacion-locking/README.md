# Week 12 — JPQL: Paginación y Locking

**Fecha**: 2026-09-17 · **Paquete**: Hibernate / JPA

## Objetivo

Escribir queries JPQL robustas con `Pageable`, filtros dinámicos y control de concurrencia con `@Lock` (optimistic/pessimistic) para evitar condiciones de carrera en inventario.

## Contexto técnico

**Brecha QC atacada**: Hibernate/ORM (64%)  
Paginación incorrecta y ausencia de locking son las dos causas principales de bugs en sistemas con carga concurrente. Esta sesión cubre ambas con escenarios reales.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-11-solution`: JOIN FETCH vs @EntityGraph vs batch |
| 20–40' | Contexto + demo: query sin paginación que carga 50k registros, race condition en inventario — búsqueda paginada con locking (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
