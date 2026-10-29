# Auditoría académica OWASP MASVS 2.1.0

La rama version-original contiene problemas de seguridad conservados para comparación. No usar en producción.

## Preparación de publicación aplicada por igual a ambas ramas

El código fuente se conserva sin cambios en version-original. Se omiten .gradle, .kotlin, .idea, build, APK compilados y local.properties: son cachés, productos generados o configuración privada del equipo. El ZIP original permanece intacto.

app/google-services.json conserva la estructura con valores de demostración para no publicar los identificadores del servicio real. Las claves cliente Firebase no son contraseñas ni se consideran aquí un hallazgo. No se encontró una clave privada de servicio en los archivos fuente.

Para ejecutar: configura tu propio proyecto Firebase de pruebas, reemplaza app/google-services.json y crea local.properties con sdk.dir. Usa JDK 21 y el SDK Android 36.1. No apuntes las pruebas a producción.
