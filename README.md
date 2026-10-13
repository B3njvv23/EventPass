# EventPass

Sistema de preventa y control de ingreso a eventos con Java 21, Spring Boot 4.1.1, JPA/Hibernate y MySQL. Incluye cuatro microservicios REST y un procesador AWS Lambda escrito en Java.

## Servicios

| Proyecto | Puerto | Base de datos | Responsabilidad |
|---|---:|---|---|
| usuarios | 8081 | eventpass_usuarios | Registro, login JWT, roles y usuarios |
| eventos | 8082 | eventpass_eventos | Catalogo, capacidad y reservas de aforo |
| ordenes | 8083 | eventpass_ordenes | Solicitudes de compra, procesamiento y cancelacion |
| tickets | 8084 | eventpass_tickets | Emision, titular, cancelacion y validacion del ingreso |
| lambda-procesador | No aplica | No aplica | Consumir mensajes SQS y solicitar procesar la orden |

Cada API separa controller, service, repository, model, dto, config y exception. La inyeccion de dependencias usa constructores. Las relaciones JPA pertenecen al interior de cada servicio; entre servicios se intercambian identificadores mediante HTTP.

## Requisitos

- JDK 21, con JAVA_HOME y java disponibles en la terminal.
- Git.
- MySQL 8, por ejemplo el servidor incluido en Laragon.
- Cuatro terminales, una por API.
- No es necesario instalar Maven: cada proyecto incluye Maven Wrapper.

Comprobar:

```powershell
java -version
git --version
```

## Clonar

```powershell
git clone https://github.com/B3njvv23/EventPass.git
cd EventPass
```

La rama main contiene la version integrada; develop es la rama de integracion y feature/* conserva las incorporaciones por componente.

## Configurar MySQL y las credenciales locales

1. Iniciar MySQL en Laragon.
2. En cada servicio, copiar el ejemplo desde la raiz del repositorio:

```powershell
Copy-Item usuarios/application-local.properties.example usuarios/application-local.properties
Copy-Item eventos/application-local.properties.example eventos/application-local.properties
Copy-Item ordenes/application-local.properties.example ordenes/application-local.properties
Copy-Item tickets/application-local.properties.example tickets/application-local.properties
```

3. Editar esos cuatro archivos. Configurar usuario y contrasena de MySQL, y reemplazar las claves de ejemplo.
4. Usar exactamente la misma clave JWT y la misma clave interna en los cuatro servicios. JWT necesita al menos 32 caracteres.
5. En usuarios, elegir el correo y la contrasena de la cuenta STAFF inicial.

Para generar cada clave local en PowerShell:

```powershell
[guid]::NewGuid().ToString("N") + [guid]::NewGuid().ToString("N")
```

Los archivos application-local.properties estan excluidos de Git y se cargan desde el directorio del servicio. No se incluyen credenciales reales en el repositorio. Los ejemplos son plantillas y deben configurarse.

Las URL predeterminadas apuntan a localhost:3306 y usan createDatabaseIfNotExist=true. El usuario de MySQL necesita permisos para crear las bases y tablas. Si no tiene esos permisos, crearlas antes:

```sql
CREATE DATABASE IF NOT EXISTS eventpass_usuarios;
CREATE DATABASE IF NOT EXISTS eventpass_eventos;
CREATE DATABASE IF NOT EXISTS eventpass_ordenes;
CREATE DATABASE IF NOT EXISTS eventpass_tickets;
```

Hibernate usa ddl-auto=update para este entorno de desarrollo. Las bases conservan los datos al reiniciar las aplicaciones.

## Ejecutar localmente

Abrir una ventana o terminal independiente por proyecto. Por ejemplo, desde la raiz:

```powershell
cd usuarios
.\mvnw.cmd spring-boot:run
```

Repetir desde la raiz en las otras terminales con cd eventos, cd tickets y cd ordenes. Esperar Started ...Application en cada una. Iniciar eventos y tickets antes de procesar una compra.

En Linux/macOS, utilizar ./mvnw en lugar de .\mvnw.cmd.

Swagger:

- [Usuarios](http://localhost:8081/swagger-ui.html)
- [Eventos](http://localhost:8082/swagger-ui.html)
- [Ordenes](http://localhost:8083/swagger-ui.html)
- [Tickets](http://localhost:8084/swagger-ui.html)

Estado del servicio: GET /actuator/health en su respectivo puerto.

## Probar el flujo con Swagger

1. En usuarios, POST /api/auth/login con la cuenta STAFF configurada en el archivo local.
2. Copiar accessToken y colocarlo en Authorize > bearerAuth. Pegar solo el token, sin el prefijo Bearer.
3. En eventos, crear un evento con la cuenta STAFF y guardar su id.
4. En usuarios, registrar un COMPRADOR e iniciar sesion con esa cuenta.
5. En ordenes, autorizarse con el token COMPRADOR y crear una orden.
6. Ejecutar POST /api/ordenes/{id}/procesar: reserva aforo y emite tickets.
7. En tickets, consultar los tickets del comprador.
8. Con token STAFF, ejecutar POST /api/tickets/validaciones usando codigo y eventoId. El primer ingreso devuelve USADO; el segundo devuelve 409.

Cada Swagger conserva su propia autorizacion. Los JWT duran una hora.

Ejemplo de evento (usar una fecha futura):

```json
{
  "nombre": "Concierto de demostracion",
  "lugar": "Teatro Central",
  "fecha": "2027-08-20T20:00:00-04:00",
  "capacidad": 100,
  "precio": 15000
}
```

Ejemplo de orden (reemplazar eventoId por el real):

```json
{
  "eventoId": 1,
  "cantidad": 2,
  "claveSolicitud": "compra-demo-001"
}
```

La cantidad admitida por orden es de 1 a 20. claveSolicitud evita crear ordenes duplicadas para el mismo comprador; reutilizarla con otros datos produce 409. Reprocesar una orden confirmada no genera entradas nuevas.

Paginacion: page=0, size=20 y sort=id,asc. En Swagger son campos separados. Un campo de ordenamiento inexistente devuelve 400.

Para cancelar, usar otra orden cuyos tickets no se hayan utilizado: DELETE /api/ordenes/{id} devuelve 204, cancela sus tickets y libera el aforo. Una orden con ingresos utilizados devuelve 409. La actualizacion de una orden pendiente modifica observacion; la cantidad permanece inmutable.

Los endpoints /internal/* son para comunicacion entre servicios mediante X-Internal-Key; no requieren colocar esa clave en un cliente publico.

## Compilar, comprobar y empaquetar con Maven

Desde la carpeta de cada uno de los cinco proyectos:

```powershell
.\mvnw.cmd compile
.\mvnw.cmd test
.\mvnw.cmd "-Dmaven.test.skip=true" package
```

Los tests de contexto generados por Spring necesitan MySQL y la configuracion local. La opcion maven.test.skip=true omite compilar y ejecutar pruebas al generar el JAR; no equivale a haberlas aprobado.

Las verificaciones funcionales documentadas se hicieron mediante HTTP contra servicios reales y consultas JDBC, y se describen en [docs/evidencias/INFORME.md](docs/evidencias/INFORME.md). No se presentan como una suite JUnit.

Los JAR quedan en target/. Para ejecutar una API empaquetada, desde su carpeta:

```powershell
java -jar target/usuarios-1.0.0.jar
```

Cambiar el nombre por eventos, ordenes o tickets segun el servicio. El directorio actual debe contener el archivo de configuracion local.

El workflow de GitHub Actions compila y empaqueta los cinco proyectos sin iniciar MySQL y sin ejecutar pruebas de integracion.

## Persistencia y evidencia

Se verificaron 72 solicitudes HTTP con el resultado esperado, incluyendo CRUD, validaciones, permisos, aforo, cancelaciones, idempotencia y control de ingresos. Se reiniciaron los cuatro procesos y se compararon los mismos registros antes y despues tanto por HTTP como por JDBC.

- [Matriz y alcance de las pruebas](docs/evidencias/INFORME.md)
- [Respuestas HTTP](docs/evidencias/resultados.json)
- [MySQL antes del reinicio](docs/evidencias/mysql-antes-reinicio.md)
- [MySQL despues del reinicio](docs/evidencias/mysql-despues-reinicio.md)
- [Resultado de persistencia](docs/evidencias/persistencia-verificada.md)

Son evidencias de una ejecucion local, con los identificadores de esa ejecucion; no sustituyen ejecutar las pruebas en un entorno nuevo.

Las bajas son logicas cuando hay referencias: usuarios se desactivan, ordenes y tickets se cancelan, y los eventos con reservas se desactivan conservando sus relaciones.

## Integracion asincrona preparada

Ordenes permite tres modos mediante QUEUE_MODE:

- manual (predeterminado): procesar explicitamente desde el endpoint REST.
- local: procesar trabajos pendientes periodicamente.
- sqs: publicar identificadores de orden en la cola configurada por SQS_QUEUE_URL y AWS_REGION.

La Lambda usa el handler com.preventa.lambda.CompraHandler::handleRequest y requiere ORDENES_URL e INTERNAL_KEY. El trigger SQS debe habilitar ReportBatchItemFailures. La funcion necesita conectividad con ordenes y una politica IAM apropiada.

El despliegue de API Gateway, SQS, Lambda y sus permisos no esta realizado en este repositorio. El flujo demostrado es local mediante HTTP.

## Git y evaluacion

- [Politica de ramas e historial reconstruido](docs/GIT.md)
- [Correspondencia con la rubrica](docs/RUBRICA.md)
- [Guion de presentacion de hasta 8 minutos](docs/GUION_VIDEO.md)

El codigo existia antes de iniciar este repositorio. Las fechas de autor del 13 y 14 de octubre de 2026 fueron asignadas retrospectivamente por solicitud; la fecha real de registro de los commits se conserva. Este historial organiza la importacion y no acredita commits efectuados durante esos dias.
