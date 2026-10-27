## Decisiones de cascade

No pondría `ON DELETE CASCADE` en ninguna clave foránea. El cascade de JPA (`PERSIST`, `MERGE`) propaga operaciones del ciclo de vida entre entidades dentro del contexto de persistencia; no implica que la base de datos deba borrar filas automáticamente.

- **`Order` → `Customer`: sin cascade.** El pedido referencia a un cliente existente, pero no es dueño de su ciclo de vida. Borrar un pedido no debe borrar al cliente; borrar un cliente con pedidos se rechaza.
- **`Order` → `OrderItem`: cascade `PERSIST` y `MERGE`, con `orphanRemoval = true`.** Los ítems pertenecen al pedido: se pueden persistir o actualizar junto con este, y quitar uno de la colección implica eliminarlo mediante JPA. La FK `order_id NOT NULL` también impide guardar ítems sin pedido. Para borrar un pedido completo, la aplicación debe eliminar primero sus ítems —por ejemplo, con una operación de dominio/transacción— o usar un borrado explícito; no añadiría una cascada SQL implícita.
- **`OrderItem` → `Product`: sin cascade.** El producto es compartido y tiene vida independiente. Eliminar o actualizar un ítem no debe modificar el producto; además, `RESTRICT` evita borrar un producto que todavía aparece en pedidos.

Los `CHECK` de cantidades, precios, stock y estados añaden integridad en la base de datos, más allá de lo que expresan hoy las anotaciones. La precisión decimal y los tamaños de texto son decisiones explícitas de esta propuesta: deberían reflejarse en las entidades y revisarse según las reglas reales del negocio.