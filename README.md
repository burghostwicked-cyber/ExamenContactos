# ExamenContactos

Aplicación Android (Java + XML) del examen de Programación para Dispositivos Móviles I.

## Incluye
- **Pantalla 1:** guardar contactos con país, nombre, teléfono, nota e imagen; valida campos vacíos y entrada inválida.
- **Pantalla 2:** consultar y buscar contactos de SQLite; seleccionarlos, editarlos, eliminarlos, compartirlos y ver sus imágenes.
- **Pantalla 3:** mostrar un número con prefijo internacional y realizar una llamada usando ACTION_CALL y permiso CALL_PHONE solicitado en ejecución. Diálogo Sí/No antes de llamar.

## Abrir y ejecutar
1. Abrir Android Studio y elegir **Get from VCS** (o File > New > Project from Version Control).
2. URL: `https://github.com/burghostwicked-cyber/ExamenContactos.git`
3. Abrir la raíz del proyecto y dejar que termine **Gradle Sync**.
4. Si solicita JDK, usar **Embedded JDK** (17 o 21). Instalar Android SDK Platform 35 si no está disponible.
5. Seleccionar un emulador Android o dispositivo físico y pulsar **Run ▶**.
6. Registrar un contacto y comprobar la lista, compartir, editar, eliminar y llamar.

**Aviso:** La llamada directa ACTION_CALL solo funciona si existe servicio de telefonía y se concede el permiso. Muchos emuladores no pueden llamar a un número real. La imagen se guarda en el almacenamiento privado de la aplicación y su ruta se registra en SQLite.

No se incluye `local.properties`, ya que contiene la ruta del SDK de cada computadora.
