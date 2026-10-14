# Pruebas funcionales: pasos 2 y 3

Prefijo de los registros nuevos: QA-20261013-d16ec48a. Los registros anteriores se conservaron.

Las pruebas se ejecutaron contra los JAR corregidos en puertos temporales 19081 a 19084, conectados a las cuatro bases MySQL de Laragon.

| N | Caso | Servicio | Metodo | Esperado | Recibido | Resultado |
|---|---|---|---|---|---|---|
| 1 | login-staff | usuarios | POST | 200 | 200 | OK |
| 2 | registro-invalido | usuarios | POST | 400 | 400 | OK |
| 3 | usuario-creado | usuarios | POST | 201 | 201 | OK |
| 4 | correo-duplicado | usuarios | POST | 409 | 409 | OK |
| 5 | login-comprador | usuarios | POST | 200 | 200 | OK |
| 6 | perfil-comprador | usuarios | GET | 200 | 200 | OK |
| 7 | usuario-actualizado | usuarios | PUT | 200 | 200 | OK |
| 8 | usuario-consultado | usuarios | GET | 200 | 200 | OK |
| 9 | rol-staff | usuarios | PATCH | 200 | 200 | OK |
| 10 | rol-comprador | usuarios | PATCH | 200 | 200 | OK |
| 11 | usuarios-listados | usuarios | GET | 200 | 200 | OK |
| 12 | comprador-sin-permiso | usuarios | GET | 403 | 403 | OK |
| 13 | sin-token | usuarios | GET | 401 | 401 | OK |
| 14 | usuario-inexistente | usuarios | GET | 404 | 404 | OK |
| 15 | comprador-no-crea-evento | eventos | POST | 403 | 403 | OK |
| 16 | evento-invalido | eventos | POST | 400 | 400 | OK |
| 17 | evento-creado | eventos | POST | 201 | 201 | OK |
| 18 | evento-actualizado | eventos | PUT | 200 | 200 | OK |
| 19 | evento-consultado | eventos | GET | 200 | 200 | OK |
| 20 | eventos-listados | eventos | GET | 200 | 200 | OK |
| 21 | aforo-inicial | eventos | GET | 200 | 200 | OK |
| 22 | orden-creada | ordenes | POST | 201 | 201 | OK |
| 23 | cantidad-invalida | ordenes | POST | 400 | 400 | OK |
| 24 | orden-idempotente | ordenes | POST | 201 | 201 | OK |
| 25 | clave-con-otros-datos | ordenes | POST | 409 | 409 | OK |
| 26 | orden-actualizada | ordenes | PUT | 200 | 200 | OK |
| 27 | orden-consultada | ordenes | GET | 200 | 200 | OK |
| 28 | ordenes-del-comprador | ordenes | GET | 200 | 200 | OK |
| 29 | orden-de-otro-comprador | ordenes | GET | 403 | 403 | OK |
| 30 | orden-confirmada | ordenes | POST | 200 | 200 | OK |
| 31 | aforo-reservado | eventos | GET | 200 | 200 | OK |
| 32 | tickets-generados | tickets | GET | 200 | 200 | OK |
| 33 | procesamiento-idempotente | ordenes | POST | 200 | 200 | OK |
| 34 | tickets-sin-duplicados | tickets | GET | 200 | 200 | OK |
| 35 | ticket-actualizado | tickets | PUT | 200 | 200 | OK |
| 36 | ticket-consultado | tickets | GET | 200 | 200 | OK |
| 37 | comprador-no-valida | tickets | POST | 403 | 403 | OK |
| 38 | ticket-otro-evento | tickets | POST | 409 | 409 | OK |
| 39 | orden-sin-aforo-creada | ordenes | POST | 201 | 201 | OK |
| 40 | compra-sin-aforo | ordenes | POST | 409 | 409 | OK |
| 41 | aforo-tras-rechazo | eventos | GET | 200 | 200 | OK |
| 42 | orden-pendiente-cancelada | ordenes | DELETE | 204 | 204 | OK |
| 43 | orden-cancelable-creada | ordenes | POST | 201 | 201 | OK |
| 44 | orden-cancelable-confirmada | ordenes | POST | 200 | 200 | OK |
| 45 | aforo-agotado | eventos | GET | 200 | 200 | OK |
| 46 | ticket-cancelable-consultado | tickets | GET | 200 | 200 | OK |
| 47 | ticket-cancelado | tickets | DELETE | 204 | 204 | OK |
| 48 | ticket-baja-logica | tickets | GET | 200 | 200 | OK |
| 49 | orden-confirmada-cancelada | ordenes | DELETE | 204 | 204 | OK |
| 50 | orden-baja-logica | ordenes | GET | 200 | 200 | OK |
| 51 | aforo-devuelto | eventos | GET | 200 | 200 | OK |
| 52 | ticket-cancelado-no-ingresa | tickets | POST | 409 | 409 | OK |
| 53 | ingreso-exitoso | tickets | POST | 200 | 200 | OK |
| 54 | segundo-ingreso-rechazado | tickets | POST | 409 | 409 | OK |
| 55 | ticket-usado-no-se-cancela | tickets | DELETE | 409 | 409 | OK |
| 56 | orden-con-ingreso-no-se-cancela | ordenes | DELETE | 409 | 409 | OK |
| 57 | ticket-inexistente | tickets | GET | 404 | 404 | OK |
| 58 | evento-desactivado-con-reservas | eventos | DELETE | 204 | 204 | OK |
| 59 | evento-baja-logica | eventos | GET | 200 | 200 | OK |
| 60 | usuario-desactivado | usuarios | DELETE | 204 | 204 | OK |
| 61 | usuario-baja-logica | usuarios | GET | 200 | 200 | OK |
| 62 | usuario-desactivado-no-inicia-sesion | usuarios | POST | 401 | 401 | OK |
| 63 | usuario-antes-reinicio | usuarios | GET | 200 | 200 | OK |
| 64 | evento-antes-reinicio | eventos | GET | 200 | 200 | OK |
| 65 | orden-antes-reinicio | ordenes | GET | 200 | 200 | OK |
| 66 | ticket-antes-reinicio | tickets | GET | 200 | 200 | OK |
| 67 | ticket-editado-antes-reinicio | tickets | GET | 200 | 200 | OK |
| 68 | usuario-despues-reinicio | usuarios | GET | 200 | 200 | OK |
| 69 | evento-despues-reinicio | eventos | GET | 200 | 200 | OK |
| 70 | orden-despues-reinicio | ordenes | GET | 200 | 200 | OK |
| 71 | ticket-despues-reinicio | tickets | GET | 200 | 200 | OK |
| 72 | ticket-editado-despues-reinicio | tickets | GET | 200 | 200 | OK |

Resultado: **72 solicitudes HTTP verificadas**, mas comprobaciones de estado, totales, aforo, idempotencia y comparacion exacta de registros antes/despues. Persistencia en las cuatro bases: **OK**.

## Evidencia y limites

Cada JSON contiene una solicitud y respuesta HTTP real. Los JWT y contrasenas se omitieron del archivo.

La evidencia versionada corresponde a solicitudes y respuestas HTTP, junto con consultas JDBC. La evidencia disponible son las respuestas HTTP reales y los informes JDBC.

El CRUD usa bajas logicas: usuarios se desactivan, ordenes y tickets se cancelan; el evento con reservas se desactiva conservando sus referencias.

Para demostrar la persistencia se consultaron las cuatro bases mediante JDBC antes y despues de reiniciar los cuatro procesos. Los informes mysql-antes-reinicio.md y mysql-despues-reinicio.md incluyen las tablas, relaciones y valores reales.
