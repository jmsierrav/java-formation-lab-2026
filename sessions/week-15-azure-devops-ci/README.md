# Week 15 — Azure DevOps Pipelines (CI)

**Fecha**: 2026-10-08 · **Paquete**: Micro + Cloud + DevOps

## Objetivo

Crear un pipeline CI en Azure DevOps que ejecute build, tests y quality gate en cada PR, bloqueando el merge si algún paso falla.

## Contexto técnico

**Brecha QC atacada**: Azure DevOps (60%) — la brecha más grande del QC.  
Un pipeline CI que funciona es la diferencia entre un equipo que entrega con confianza y uno que descubre bugs en producción. Esta sesión construye ese pipeline desde cero.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-14-solution`: optimización de layers y tamaño de imagen |
| 20–40' | Contexto + demo: anatomía de `azure-pipelines.yml`, triggers y stages — pipeline CI con build + test + quality gate (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
