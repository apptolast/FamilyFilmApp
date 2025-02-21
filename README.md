![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/apptolast/FamilyFilmApp/build.yml)
![GitHub top language](https://img.shields.io/github/languages/top/apptolast/FamilyFilmApp)

![GitHub contributors](https://img.shields.io/github/contributors/apptolast/FamilyFilmApp)
![GitHub issues](https://img.shields.io/github/issues/apptolast/FamilyFilmApp)
![GitHub pull requests](https://img.shields.io/github/issues-pr/apptolast/FamilyFilmApp)


# FamilyFilmApp

### Descripción

La aplicación trata de resolver el problema a la hora de decir que películas o series ver cuando se juntan varias personas en un grupo. Cuesta poner a la gente de acuderdo para decidir que ver ya que siempre hay alguien que ha visto lo que la mayoría quiere ver, las preferencias son muy distintas, etc.

La aplicación te permite añadir las películas que has visto y las que quieres ver y el fantástico algoritmno que incorpora se encarga de filtrar esa información, para cada miembro del grupo, y recomerdar la película que a todos les gustaría ver en esa reunión de amigos o quedada familiar.

# Arquitectura y Tecnologías

## Patrón de Diseño
**MVVM** (Model-View-ViewModel): Sigue los estándares establecidos por Google y utilizados ampliamente por la comunidad de desarrolladores. Esto asegura una separación clara entre la lógica de la UI, la lógica de negocios y el modelo de datos, facilitando el mantenimiento y la escalabilidad.

## Gestión del Estado
**Flow y Coroutines**: Para la gestión reactiva del estado, se utiliza Flow, lo que permite una actualización eficiente y segura de la UI en respuesta a los cambios de datos.

## Inyección de Dependencias
**Hilt**: La inyección de dependencias se maneja a través de Hilt, simplificando la construcción de objetos y promoviendo un acoplamiento más débil y un código más testeable.

## Backend propio (API REST)
Hemos implementado un doble sistema de autenticación mediante una API REST utilizando las siguientes tecnologías:

* TypeScript
* Express (NodeJS)
* SGBD (PostgreSQL)
* Contenedores en Docker
* Deploy en [Render](https://render.com/)

La URL de la API es: [https://ffa-develop-back.onrender.com/api/](https://ffa-develop-back.onrender.com/api/)

Para atacar a nuestra API REST utilizamos un cliente de [Postman](https://www.postman.com/) y puedes encontrar el projecto de backend en el siguiente repo: [FamilyFilmAppBack](https://github.com/apptolast/FamilyFilmAppBack.git)

## Firebase
**Auth**: Se utiliza Firebase **Authentication** para obtener el UID que se enviará al backend para identifciar a los usuarios. Los proveedores de registro utilizados son: **emial/pass** y **Google**

**Crashlytics**: Se utiliza Crashlytics para la detección temprana de errores que puedan ocurrir en la aplicación y poder atajarlos los antes posible evitando que afecte a más usuarios.

## Librerías Principales
**Jetpack Compose**: Este proyecto está construido utilizando Jetpack Compose, el moderno toolkit de UI de Android para crear interfaces nativas de forma declarativa y eficiente. Simplifica y acelera el desarrollo de la IU en Android.

**Retrofit**: Para las llamadas a la API, Retrofit es utilizado por su eficiencia y facilidad de integración con los convertidores de JSON como Gson.

**Navigation Component**: Para la gestión de la navegación en la aplicación, se emplea el componente de navegación de Android Jetpack, en conjunto con una [libreria de terceros para gestionar los argumentos de navegacion](https://github.com/dilrajsingh1997/safe-compose-args).

**Coroutines**: Para la programación asíncrona y la gestión de tareas en segundo plano, se utilizan las Coroutines de Kotlin.

**StateFlow**: Para la comunicación entre ViewModel y UI se utiliza StateFlow.

**CI/CD**: Se utiliza Github Actions para la Integración Continua

**Ktlint**: Se utiliza para la comprobación y autoformato del código para mantener un estandar entre todos los miembros que colaboran en el proyecto. En particular, se está utilizando el plugin de [jlleitschuh](https://github.com/JLLeitschuh/ktlint-gradle).

## ¿Cómo participar?
### Fork del proyecto
Para participar en este proyecto recomiendo:

*  **Hacer un fork** del proyecto y darle a la **estrellita** para apoyar y dar visibilidad al repo.
*  **Crear una rama** en la que desarrollar las mejoras o nuevas funcionalidades. ¿Por qué en una rama? Es mejor desarollar tus cambios en una rama para mantener el fork sin cambios, así podrás actualizar los cambios que sigamos haciendo en el repositorio principal. Cuando acabes acabes tu cambios asegúrate de:
* Actualizar el fork para que tenga los últimos cambios que hayamos añadido en el repo principal.
*  Mergearlos en tu rama, arreglando los conflictos que puedan surgir.
*  Crear un **Pull Request** desde tu rama a nuestra rama de **develop**.
*  A continuación, procederemos a revisar tu PR, sugiriendo cambios o aceptándolo pra añadir tus cambios al repo principal.

### Configuración
Debido a la utilización de **Firebase** y **Github Actions** el proyecto no compilará automáticamente, por lo que es necesario una serie de paso:

#### Firebase
En primer lugar, crea un proyecto de firebase y configúralo. No olvides:

* Añadir tu clave SHA-1 en la configuración del proyecto, en la pestaña "General".
* Descargar el fichero `google-services.json` y añadirlo dentro de tu carpeta app de tu proyecto de android.

#### Proveedor de Google
Para utilizar el proveedor de Google en firebase auth, se require añadir un **ID_TOKEN** que se utilizará en el código de nuestro proyecto. Por lo tanto, sigue los siguientes pasos:
* Abre el fichero `gradle.properties` y añade o modifica la siguiente linea:

```
WEB_ID_CLIENT=your_token_id
```
* Este token id se puede conseguir aqui:
![image](https://github.com/apptolast/FamilyFilmApp/assets/51128924/e923b26f-b33b-4c04-8c95-ec38dab557fb)



## Contribuidores

### Android:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-hgarciaalberto-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/hgarciaalberto)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Coshiloco-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Coshiloco)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-rndevelo-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/rndevelo)

### Backend:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-TuColegaDev-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/TuColegaDev)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Isabel9422-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Isabel9422)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-El3auti-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/El3auti)

## Puedes encontrarnos en:

[<img alt="Discord" height="35" src="https://img.shields.io/badge/-Discord-7289DA?style=flat-square&amp;logo=discord&amp;logoColor=white"/>](https://discord.gg/eM25JGk3TC)
<img alt="Twitch Status" height="35" src="https://img.shields.io/twitch/status/AndroidZen"/>


