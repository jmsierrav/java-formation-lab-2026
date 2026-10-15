## Fallo 1 — Spring no puede crear SupplierController por falta de un bean
- **Síntoma**: fallan todos los tests con `Parameter 1 of constructor in com.indra.catalog.suppliers.web.SupplierController required a bean of type 'com.indra.catalog.suppliers.web.SupplierWebMapper' that could not be found.`
- **Causa raíz**: @WebMvcTest solo carga la capa web, no todos los componentes de la aplicación.
- **Corrección**: agregué la anotación `@Import(SupplierWebMapper.class)` a la clase SupplierControllerTest para cargar el bean SupplierWebMapper.
- **Prevención**: tener en cuenta que @WebMvcTest no carga todos los beans y usar @Import para los necesarios.

## Fallo 2 — URI mal formada en la respuesta de creación
- **Síntoma**: el test que verifica la ubicación del recurso creado falla.
- **Causa raíz**: se estaba construyendo la URI con `.path("{id}")` en lugar de `.path("/{id}")`.
- **Corrección**: cambié `.path("{id}")` por `.path("/{id}")` en SupplierController.
- **Prevención**: revisar las rutas y URI construidas en los controladores y tests.

## Fallo 3 — Validación de request fallida
- **Síntoma**: el test que verifica la creación de un proveedor con datos inválidos falla con un error de validación.
- **Causa raíz**: el request no estaba siendo validado automáticamente porque faltaba la anotación @Valid en el parámetro del método create.
- **Corrección**: agregué la anotación @Valid al parámetro CreateSupplierRequest en el método create de SupplierController.
- **Prevención**: siempre usar @Valid en los parámetros de los métodos de los controladores que reciben requests que deben ser validados.
