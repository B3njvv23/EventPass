# Ramas e historial

## Organizacion

- main: version integrada y lista para la demostracion.
- develop: integracion de cambios.
- feature/usuarios: autenticacion, roles y usuarios.
- feature/eventos: catalogo y reservas.
- feature/tickets: emision y validacion de ingresos.
- feature/ordenes: compras y coordinacion HTTP.
- feature/procesamiento-asincrono: SQS y Lambda.
- feature/documentacion-evidencias: instrucciones, rubrica y resultados.
- feature/compilacion: workflow de compilacion.

Cada feature se integra mediante un merge --no-ff, conservando el limite entre componentes. No borrar ramas ni reescribir historia compartida durante la evaluacion.

## Fechas

El repositorio fue creado e importado el 15 de octubre de 2026. El trabajo ya existia en el directorio local. Por solicitud se asignaron fechas de autor del 13 y 14 de octubre como reconstruccion organizativa. Las fechas de committer reflejan el momento real de registro. Los mensajes explicitan la importacion retrospectiva; no representan commits hechos durante el desarrollo original.

Para revisar ambas fechas:

```bash
git log --all --graph --decorate --pretty=fuller
```

## Desarrollo a partir de ahora

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feature/nombre-del-cambio
# Implementar un cambio concreto y verificarlo.
git add ruta/de/archivos
git commit -m "fix(tickets): describir el comportamiento corregido"
git push -u origin feature/nombre-del-cambio
```

Integrar primero en develop y luego publicar una version en main cuando este comprobada. Los siguientes commits deben registrar las fechas reales y explicar un cambio coherente por commit.
