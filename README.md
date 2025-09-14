![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/apptolast/FamilyFilmApp/build.yml)
![GitHub top language](https://img.shields.io/github/languages/top/apptolast/FamilyFilmApp)

![GitHub contributors](https://img.shields.io/github/contributors/apptolast/FamilyFilmApp)
![GitHub issues](https://img.shields.io/github/issues/apptolast/FamilyFilmApp)
![GitHub pull requests](https://img.shields.io/github/issues-pr/apptolast/FamilyFilmApp)


# FamilyFilmApp

### Description

The application aims to solve the problem of deciding what movies or series to watch when several people gather in a group. It's difficult to get people to agree on what to watch since there's always someone who has already seen what the majority wants to watch, preferences are very different, etc.

The application allows you to add the movies you've watched and the ones you want to watch, and the fantastic algorithm it incorporates takes care of filtering that information for each group member and recommending the movie that everyone would like to watch at that friends' gathering or family get-together.

# Architecture and Technologies

## Design Pattern
**MVVM** (Model-View-ViewModel): Follows the standards established by Google and widely used by the developer community. This ensures a clear separation between UI logic, business logic, and the data model, facilitating maintenance and scalability.

## State Management
**Flow and Coroutines**: For reactive state management, Flow is used, which allows efficient and safe UI updates in response to data changes.

## Dependency Injection
**Hilt**: Dependency injection is handled through Hilt, simplifying object construction and promoting looser coupling and more testable code.

## Firebase
**Firestore**: Used to manage users, movies, and the groups they belong to.

**Auth**: Firebase **Authentication** is used to obtain the UID that will be sent to the backend to identify users. The registration providers used are: **email/password** and **Google**

**Crashlytics**: Crashlytics is used for early detection of errors that may occur in the application and to address them as soon as possible to prevent them from affecting more users.

## Main Libraries
**Jetpack Compose**: This project is built using Jetpack Compose, Android's modern UI toolkit for creating native interfaces declaratively and efficiently. It simplifies and accelerates UI development on Android.

**Retrofit**: For API calls, Retrofit is used for its efficiency and ease of integration with JSON converters like Gson.

**Navigation Component**: For navigation management in the application, the Android Jetpack navigation component is used, together with a [third-party library to manage navigation arguments](https://github.com/dilrajsingh1997/safe-compose-args).

**Coroutines**: For asynchronous programming and background task management, Kotlin Coroutines are used.

**StateFlow**: For communication between ViewModel and UI, StateFlow is used.

**CI/CD**: GitHub Actions is used for Continuous Integration

**Ktlint**: Used for code checking and auto-formatting to maintain a standard among all members collaborating on the project. In particular, the plugin from [jlleitschuh](https://github.com/JLLeitschuh/ktlint-gradle) is being used.

## How to participate?
### Fork the project
To participate in this project, I recommend:

*  **Fork** the project and give it a **star** to support and give visibility to the repo.
*  **Create a branch** in which to develop improvements or new functionalities. Why in a branch? It's better to develop your changes in a branch to keep the fork unchanged, so you can update the changes we continue to make in the main repository.
*  Merge them into your branch, fixing any conflicts that may arise.
*  Create a **Pull Request** from your branch to our **develop** branch.
*  Next, we will proceed to review your PR, suggesting changes or accepting it to add your changes to the main repo.

### Configuration
Due to the use of **Firebase** and **Github Actions**, the project will not compile automatically, so a series of steps are necessary:

#### Firebase
First, create a Firebase project and configure it. Don't forget to:

* Add your SHA-1 key in the project configuration, in the "General" tab.
* Download the `google-services.json` file and add it inside your app folder of your Android project.

#### TMDB API Token
To get the TMDB API token, register on TMDB, go to Settings > API, and create a new API key.
Then, add it to your project's gradle.properties file with the name `TMDB_ACCESS_TOKEN`

## Testing resources:
Koin Unit tests: https://insert-koin.io/docs/reference/koin-test/testing
Koin Android tests: https://insert-koin.io/docs/reference/koin-android/instrumented-testing/

## Contributors

### Android:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-hgarciaalberto-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/hgarciaalberto)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Coshiloco-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Coshiloco)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-rndevelo-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/rndevelo)

### Backend:
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-TuColegaDev-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/TuColegaDev)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-Isabel9422-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/Isabel9422)
[<img alt="GitHub" height="35" src="https://img.shields.io/badge/-El3auti-181717?style=flat-square&amp;logo=github&amp;logoColor=white"/>](https://github.com/El3auti)

## You can find us at:

[<img alt="Discord" height="35" src="https://img.shields.io/badge/-Discord-7289DA?style=flat-square&amp;logo=discord&amp;logoColor=white"/>](https://discord.gg/wyPDmk6Fda)
<img alt="Twitch Status" height="35" src="https://img.shields.io/twitch/status/AndroidZen"/>


