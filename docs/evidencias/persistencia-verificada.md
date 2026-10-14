# Persistencia verificada

Se reiniciaron los cuatro procesos de los JAR corregidos, manteniendo las mismas bases MySQL.

Las consultas JDBC antes y despues recuperaron exactamente los mismos registros y valores: usuario actualizado, evento con aforo reservado, orden confirmada con observacion, ticket usado y ticket con titular actualizado.

Resultado: comparacion de las cuatro bases **OK**. Las capturas de Swagger despues del reinicio complementan los informes SQL.
