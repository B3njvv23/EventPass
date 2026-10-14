# MySQL: antes-reinicio

Consultas JDBC reales sobre MySQL de Laragon. No se muestran hashes ni contrasenas.

## usuarios

Base: eventpass_usuarios. Motor: MySQL 8.4.3.

### Tablas existentes

| TABLE_NAME |
| --- |
| usuarios |

### Relaciones persistidas

| TABLE_NAME | COLUMN_NAME | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
| --- | --- | --- | --- |

### Registros de evidencia

| id | nombre | email | rol | activo |
| --- | --- | --- | --- | --- |
| 3 | Comprador actualizado QA-20261013-d16ec48a | qa-20261013-d16ec48a@eventpass.cl | COMPRADOR | 0 |

## eventos

Base: eventpass_eventos. Motor: MySQL 8.4.3.

### Tablas existentes

| TABLE_NAME |
| --- |
| evento |
| reserva |

### Relaciones persistidas

| TABLE_NAME | COLUMN_NAME | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
| --- | --- | --- | --- |
| reserva | evento_id | evento | id |

### Registros de evidencia

| id | nombre | lugar | capacidad | reservados | precio | activo |
| --- | --- | --- | --- | --- | --- | --- |
| 2 | Evento QA-20261013-d16ec48a | Recinto QA actualizado | 3 | 2 | 1000.00 | 0 |

## ordenes

Base: eventpass_ordenes. Motor: MySQL 8.4.3.

### Tablas existentes

| TABLE_NAME |
| --- |
| compra_pendiente |
| ordenes |

### Relaciones persistidas

| TABLE_NAME | COLUMN_NAME | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
| --- | --- | --- | --- |
| compra_pendiente | orden_id | ordenes | id |

### Registros de evidencia

| id | usuario_id | evento_id | cantidad | total | estado | observacion |
| --- | --- | --- | --- | --- | --- | --- |
| 2 | 3 | 2 | 2 | 2000.00 | CONFIRMADA | Persistencia QA-20261013-d16ec48a |

## tickets

Base: eventpass_tickets. Motor: MySQL 8.4.3.

### Tablas existentes

| TABLE_NAME |
| --- |
| lote |
| ticket |

### Relaciones persistidas

| TABLE_NAME | COLUMN_NAME | REFERENCED_TABLE_NAME | REFERENCED_COLUMN_NAME |
| --- | --- | --- | --- |
| ticket | orden_id | lote | orden_id |

### Registros de evidencia

| id | codigo | orden_id | titular | estado | usado_en | validado_por |
| --- | --- | --- | --- | --- | --- | --- |
| 3 | f5eca2a7-4b41-4566-aab5-f11d624c4d76 | 2 | NULL | USADO | 2026-10-13 22:37:41.955005 | 1 |
| 4 | 3873802b-38e1-4230-8ffc-5f63127bea26 | 2 | Comprador actualizado QA-20261013-d16ec48a | EMITIDO | NULL | NULL |
