# Week 06 — REST Limpio: DTO y Manejo de Errores

**Fecha**: 2026-08-06 · **Paquete**: Spring Web / Testing

## Objetivo

Construir una API REST que sea **consistente y validada**: DTOs separados del modelo de dominio, validación con Bean Validation, y respuestas de error uniformes con `@ExceptionHandler`.

## Contexto técnico

**Brecha QC atacada**: Spring avanzado (77–83%)  
La API mal diseñada es la principal fuente de bugs difíciles de encontrar en producción. En esta sesión el foco está en la capa de entrada: cómo recibir, validar y responder correctamente.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-05-solution`: anti-patrones de mocking |
| 20–40' | Contexto + demo: API sin DTOs vs con DTOs, error sin handler vs con handler — endpoint de creación de pedido (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
