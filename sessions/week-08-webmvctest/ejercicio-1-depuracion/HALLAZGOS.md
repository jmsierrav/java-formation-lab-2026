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
