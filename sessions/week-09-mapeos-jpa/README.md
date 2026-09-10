# Week 09 — Mapeos JPA

**Fecha**: 2026-08-27 · **Paquete**: Hibernate / JPA

## Objetivo

Modelar relaciones entre entidades con JPA correctamente: fetch types, cardinalidades, `@JoinColumn`, y evitar los errores de mapeo más comunes que aparecen en producción.

## Contexto técnico

**Brecha QC atacada**: Hibernate/ORM (64%) — la brecha más grande del QC.  
El 80% de los bugs de producción relacionados con ORM vienen de mapeos incorrectos: EAGER donde debería ser LAZY, relaciones sin `cascade` correcto, o `@ManyToMany` sin tabla intermedia explícita.

## Agenda de la sesión

| Tiempo | Actividad |
|--------|-----------|
| 0–20' | Revisión `week-08-solution`: WebMvcTest vs test unitario |
| 20–40' | Contexto + demo: EAGER vs LAZY, mapeos que explotan en producción — modelar Pedido/Producto/Cliente (ver `enunciado.md`) |
| 40–60' | Q&A (se resuelve entre semana, PR antes del jueves siguiente) |
