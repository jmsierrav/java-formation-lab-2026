# Week 08 — WebMvcTest: Slice Tests de Controllers

**Fecha**: 2026-08-20 · **Paquete**: Spring Web / Testing

## Objetivo

Probar controllers REST con `@WebMvcTest` y `MockMvc` sin levantar el contexto completo de Spring: tests rápidos, enfocados y que no requieren base de datos.

## Contexto técnico

**Brecha QC atacada**: Testing (79%)  
Los tests de controller con `@SpringBootTest` tardan 15–30 segundos. Con `@WebMvcTest` tardan menos de 2. Esta diferencia define si el equipo ejecuta los tests frecuentemente o los evita.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-07-solution`: 12-factor app aplicado |
| 20–40' | Contexto + demo: @SpringBootTest vs @WebMvcTest, diferencia de tiempo — slice tests del controller de pedidos (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
