# Week 03 — SOLID: SRP y OCP

**Fecha**: 2026-07-16 · **Paquete**: SOLID + TDD

## Objetivo

Identificar violaciones de **Single Responsibility Principle** y **Open/Closed Principle** en código real y refactorizarlas manteniendo los tests en verde.

## Contexto técnico

**Brecha QC atacada**: SOLID + TDD (77%)  
El código inicial tiene una clase que hace demasiado (viola SRP) y requiere modificación para agregar nuevos comportamientos (viola OCP). Refactorizar sin romper los tests existentes es la habilidad clave.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-02-solution`: ciclo TDD, anti-patrones frecuentes |
| 20–40' | Contexto + demo: identificar SRP/OCP violations en código real — refactorizar `OrderProcessor` (ver `enunciado.md`) |
| 40–60' | Q&A sobre el reto (se resuelve entre semana, PR antes del jueves siguiente) |
