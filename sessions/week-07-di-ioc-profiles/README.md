# Week 07 — DI/IoC: Profiles y Wiring

**Fecha**: 2026-08-13 · **Paquete**: Spring Web / Testing

## Objetivo

Configurar el contenedor Spring correctamente para múltiples entornos: inyección por constructor obligatoria, uso de `@Profile`, `@Conditional` y separación de configuración por entorno.

## Contexto técnico

**Brecha QC atacada**: Spring avanzado / DI (77–83%)  
El error más frecuente en proyectos: configuración hardcodeada, beans que se comportan diferente en local vs producción, o `@Autowired` en campo que ocultan dependencias. Esta sesión lo corrige en raíz.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-06-solution`: contrato de error estándar |
| 20–40' | Contexto + demo: @Autowired en campo vs constructor, profiles en acción — configurar beans por perfil (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
