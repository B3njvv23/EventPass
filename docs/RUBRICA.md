# Correspondencia con la rubrica

| Criterio | Puntos | Evidencia del proyecto |
|---|---:|---|
| Endpoints RESTful | 20 | Controladores y Swagger de los cuatro servicios; respuestas HTTP documentadas |
| Arquitectura por capas | 10 | controller, service, repository, model, dto, config, exception; inyeccion por constructor |
| JPA/Hibernate y relaciones | 5 | Reserva-Evento, Lote-Ticket, CompraPendiente-Orden; claves foraneas en los informes SQL |
| pom.xml | 5 | Dependencias, Java 21, plugins y Maven Wrapper por proyecto |
| Git y ramas | 10 | main, develop, feature/*, commits descriptivos y merges; historial de importacion reconstruido y declarado |
| Pruebas y errores | 10 | 72 solicitudes reales con resultados esperados y comprobaciones de estado; ver evidencias |
| Base de datos relacional | 10 | MySQL de Laragon, cuatro bases y configuracion local separada |
| CRUD y persistencia | 10 | Altas, consultas, actualizaciones, bajas logicas y comparacion antes/despues del reinicio |
| Maven y artefactos | 5 | Compilacion y JAR por proyecto; workflow de compilacion sin pruebas |
| README y ejecucion reproducible | 5 | Clonado, requisitos, configuracion, arranque, Swagger y Maven documentados |
| Estructura del video | 5 | Guion preparado; falta grabar la presentacion |
| Calidad del audio | 5 | Depende de la grabacion final; no se acredita con el codigo |

## Alcance

Las pruebas funcionales se ejecutaron en un entorno local y los resultados estan versionados como evidencia. Los tests de contexto generados por Spring son distintos de esas pruebas HTTP.

La cronologia de autor fue reconstruida al importar el codigo. No debe presentarse como evidencia de commits frecuentes efectuados durante el desarrollo original.

El diagrama incluye infraestructura AWS cuyo despliegue sigue pendiente. No se atribuye un despliegue cloud a la demostracion local.
