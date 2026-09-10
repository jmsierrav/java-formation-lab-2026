# Week 10 — Transacciones

**Fecha**: 2026-09-03 · **Paquete**: Hibernate / JPA

## Objetivo

Entender y aplicar correctamente `@Transactional`: propagación, rollback rules, aislamiento, y la trampa del proxy de Spring que hace que las transacciones fallen silenciosamente en producción.

## Contexto técnico

**Brecha QC atacada**: Hibernate/ORM (64%)  
"Funciona en local, falla en producción" es el síntoma clásico de transacciones mal configuradas. Esta sesión cubre los 3 escenarios que más frecuentemente causan ese problema.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-09-solution`: reglas de oro de mapeo JPA |
| 20–40' | Contexto + demo: @Transactional self-invocation, rollback solo en RuntimeException — transferencia bancaria (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
