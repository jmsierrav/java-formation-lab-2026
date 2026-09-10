# Week 18 — Observabilidad + Cierre

**Fecha**: 2026-10-29 · **Paquete**: Micro + Cloud + DevOps

## Objetivo

Agregar logging estructurado con correlation ID, métricas con Micrometer y conectar Application Insights para tener visibilidad completa de la aplicación en producción. Cierre del plan de formación 2026.

## Contexto técnico

**Brecha QC atacada**: Cloud Azure (76%), operación  
Un sistema sin observabilidad es una caja negra. Esta sesión cierra el ciclo: la app está en Azure, el pipeline CI/CD funciona, y ahora le damos "ojos" para saber qué está pasando en producción en tiempo real.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-17-solution`: costos y buenas prácticas |
| 20–40' | Contexto + demo: buscar un error en producción sin correlation ID vs con él — correlation ID + métricas + Application Insights (ver `enunciado.md`) |
| 40–60' | Q&A + retrospectiva del plan 2026, próximos pasos |
