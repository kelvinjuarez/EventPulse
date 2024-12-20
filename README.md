# EventPulse

EventPulse es una aplicación móvil desarrollada en Android con Kotlin que permite a una comunidad local gestionar eventos y actividades comunitarias. Proporciona herramientas para la creación, organización y seguimiento de eventos, mejorando la interacción y participación de los usuarios en su comunidad.

---

## Integrantes del Proyecto

- Desarrollo de Software para Móviles DSM941 G01T
- **GJ111587 Kelvin Vladimir García Juárez**
- Roles: Programación, diseño, lógica, QA
---

## Características Principales

1. **Autenticación**:
   - Inicio de sesión mediante correo electrónico y contraseña.
   - Integración con Google para un acceso rápido y seguro.
   - Opción de cerrar sesión y seleccionar una nueva cuenta.

2. **Gestión de Eventos**:
   - Creación de eventos con detalles como título, descripción, fecha, hora, ubicación e imagen.
   - Visualización de todos los eventos creados por los administradores.
   - Edición y eliminación de eventos para administradores.

3. **Interacción Social**:
   - Confirmación de asistencia a eventos.
   - Posibilidad de compartir eventos en redes sociales.
   - Comentarios con calificación de estrellas por parte de los usuarios.
   - Vista ampliada de imágenes asociadas al evento.

4. **Historial y Estadísticas**:
   - Historial de eventos pasados a los que el usuario asistió.
   - Estadísticas de participación por evento.

5. **Pantallas Dedicadas**:
   - **Acerca de**: Información sobre la aplicación y el desarrollador.
   - **Mi Cuenta**: Permite visualizar la información personal, actualizar el correo electrónico y cambiar la contraseña.

6. **Diseño UI/UX Mejorado**:
   - Layouts adaptados para ofrecer una experiencia de usuario intuitiva y agradable.
   - Compatibilidad con modo oscuro y claro.

---

## Recursos del Proyecto

0. **Video**
   - [Youtube](https://youtu.be/pSzV58VsgOQ)

1. **Gestión del Proyecto**:
   - [Trello o Notion](https://trello.com/b/VB6122hF/event-pulse-dsm941)

2. **Diseños Mockups**:
   - [Link a Mockups](https://drive.google.com/file/d/1utMk1nKA-wjzzgEYNINh4r65bes70XCK/view?usp=sharing)

3. Licencia
   Este proyecto está protegido bajo una Licencia Privada Personalizada con los siguientes términos:

   **Uso privado**: Solo se permite el uso del software con fines personales o privados.
   
   **No comercial**: Queda estrictamente prohibido el uso comercial del software.
   
   **Modificaciones permitidas**: Los usuarios pueden modificar el software únicamente para su uso privado.
   
   **Prohibida la distribución**: No está permitida la redistribución del software ni de sus versiones modificadas.

5. **Documentación del Proyecto**:
   - [Guía de Usuario](https://github.com/kelvinjuarez/EventPulse/blob/main/Manual%20de%20Usuario%20EventPulse.pdf)

---

## Requisitos Previos

1. **SDK y Herramientas**:
   - Android SDK 34.
   - IntelliJ IDEA o Android Studio.
   - API 35.

2. **Dependencias Principales**:
   - `com.google.firebase:firebase-auth:23.1.0`
   - `com.google.firebase:firebase-firestore:25.1.1`
   - `com.google.firebase:firebase-storage:21.0.1`
   - `com.google.android.gms:play-services-auth:21.2.0`
   - `com.github.bumptech.glide:glide:4.15.1`

3. **Otras Herramientas**:
   - Gradle para la gestión de dependencias.
   - Firebase Console para la configuración de autenticación, Firestore y almacenamiento.

---

## Instalación y Ejecución

1. Clona este repositorio en tu máquina local:
   ```bash
   git clone https://github.com/tu-usuario/eventpulse.git
