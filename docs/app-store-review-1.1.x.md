# Guía de resubmisión a App Store — Fliksy 1.1.x

> Contexto: Apple rechazó la build **1.1.3 (8)** (Submission ID `dae9a9d1-71b1-4f23-bbdd-70506cc4e957`) por **cuatro** motivos. Esta guía cubre (A) los cambios de código ya realizados y (B) los pasos manuales que **tú** debes hacer en Xcode y App Store Connect, más (C) los textos exactos para responder a Apple y (D) un checklist final.

Rama de trabajo: `fix/ios-appstore-review-1.1.x`.

---

## Resumen de los 4 motivos y su solución

| Guideline | Motivo de Apple | Solución |
|-----------|-----------------|----------|
| **2.1** | No encuentran el prompt de ATT en iOS/iPadOS 26.5 | ATT ahora se pide **lo primero al activarse la app**, en main thread, desacoplado de red/notificaciones (código). + grabación nueva (tú). |
| **5.2.1** | Contenido tipo Disney/Pixar (Toy Story, Shrek) sin autorización | Capturas de la ficha con **datos ficticios y arte original** (pipeline nuevo). Metadatos sin títulos con copyright. + justificación escrita (tú). |
| **3.1.2(c)** #1 | Falta precio + enlaces a EULA y privacidad en la app y en metadatos | Paywall rediseñado con **precio, periodo, EULA y privacidad** (código). EULA estándar de Apple añadida a la descripción (metadatos). |
| **3.1.2(c)** #2 | El importe cobrado no es el elemento más claro/visible | El **importe facturado** es ahora el elemento de precio más prominente del paywall (código). |

---

## PARTE A — Cambios de código (ya hechos en esta rama)

### A1. App Tracking Transparency (Guideline 2.1)
**Problema raíz:** el prompt de ATT estaba encadenado detrás del permiso de notificaciones → consentimiento UMP (llamada de **red**) → y solo entonces se pedía. Desde iOS 17/18 y iPadOS 26, `requestTrackingAuthorization` **se descarta en silencio** si la app no está `.active` o si hay otra alerta del sistema en pantalla. Cualquier latencia o fallo de red en el dispositivo del revisor hacía que el prompt no apareciera.

**Cambio:** `iosApp/iosApp/iOSApp.swift` + `ConsentManager.swift`
- Nuevo `AppBootstrap` que se ejecuta **una sola vez al pasar la escena a `.active`** (`scenePhase`).
- Orden nuevo: **ATT primero** (en main thread, con `DispatchQueue.main.async`, solo si el estado es `.notDetermined`) → luego notificaciones → luego consentimiento UMP → luego AdMob.
- ATT ya **no depende** de la red ni de UMP, por lo que no puede "perderse".
- Se fija el consentimiento por defecto de Firebase Analytics (`adStorage/adUserData/adPersonalization = denied`) hasta que se resuelve ATT, y se actualiza según la respuesta.

### A2. Suscripción / paywall (Guideline 3.1.2(c))
**Problema:** el único paywall personalizado (el diálogo de Chat Premium en el chat) **no mostraba precio, ni periodo, ni enlaces** a privacidad/EULA.

**Cambios:**
- **Precio expuesto desde RevenueCat a la UI** (antes solo se usaba para analítica):
  - `purchases/SubscriptionPricing.kt` (nuevo): modelo con `priceString`, `periodUnit`, `periodCount`.
  - `purchases/PurchaseManager.kt`: nuevo `getChatPremiumPricing()`.
  - `RevenueCatPurchaseManager.kt` (Android): lo resuelve de las offerings.
  - `IosRevenueCatPurchaseBridge.kt` + `IosRevenueCatPurchaseManager.kt` + `RevenueCatPurchaseBridgeImpl.swift`: nuevo método `fetchChatPremiumPricing` (precio + periodo desde StoreKit).
- **Paywall rediseñado** (`ui/screens/chat/ChatContent.kt`, `ChatViewModel.kt`, `ChatUiState.kt`):
  - Bloque de precio destacado: **el importe facturado (`4,99 €/mes`) es el texto de precio más grande y prominente** (resuelve 3.1.2(c) #2). No se promociona ninguna prueba/precio introductorio por encima del importe.
  - Texto de **auto-renovación** (cargo a la cuenta de Apple, renueva salvo cancelación 24 h antes, gestión en Ajustes).
  - Enlaces **funcionales** a Política de privacidad y Términos de uso (EULA), vía `LocalUriHandler`.
- **Strings** nuevos en `values/strings.xml` y `values-es/strings.xml`.

> Nota: en Perfil, "Chat Premium" y "Quitar anuncios" abren **directamente la hoja de compra nativa de Apple**, que ya muestra toda la información obligatoria; por eso el foco del arreglo es el diálogo personalizado del chat, que es el que Apple revisó.

### A3. Propiedad intelectual (Guideline 5.2.1)
- **Metadatos:** la descripción (en-GB y es-ES) ya **no** menciona títulos con copyright; se añadió el enlace a la **EULA estándar de Apple** y a la política de privacidad.
- **Pipeline de capturas con datos ficticios** (ver `docs/screenshots-pipeline.md`): modo demo activado por launch-arg `-FFADemoMode YES` que sustituye el datasource de TMDB por `FakeTmdbDatasource` (12 títulos inventados con **pósters originales generados** por código, sin imágenes de terceros). Tú generas y subes las capturas (Parte B3).

### A4. EULA estándar en metadatos (Guideline 3.1.2(c) #1)
- Añadido a `iosApp/fastlane/metadata/{en-GB,es-ES}/description.txt`:
  - `Terms of Use (EULA): https://www.apple.com/legal/internet-services/itunes/dev/stdeula/`
  - `Privacy Policy: https://apptolast.github.io/FamilyFilmApp/privacy-policy`
  - Cláusula de suscripción auto-renovable.

---

## PARTE B — Pasos manuales que debes hacer

### B1. App Tracking Transparency (Guideline 2.1)
1. **En el dispositivo físico de prueba** (no simulador): Ajustes ▸ Privacidad y seguridad ▸ Seguimiento ▸ activa **"Permitir que las apps soliciten seguimiento"**. (Si está desactivado, NINGÚN prompt de ATT puede mostrarse — es una causa habitual del rechazo.)
2. Borra la app o resetea permisos de seguimiento, e instala la build nueva.
3. **Graba un screen recording** que muestre, en orden:
   - Lanzamiento desde instalación limpia.
   - **Aparece el prompt de ATT** ("Permitir que Fliksy rastree…") **antes** de cualquier recolección de datos.
   - El flujo posterior (permiso de notificaciones, etc.).
4. Sube ese vídeo en **App Store Connect ▸ (versión) ▸ App Review Information ▸ Notes** y adjúntalo también en la respuesta al revisor.
5. **App Privacy:** como la app usa AdMob/IDFA, en App Store Connect ▸ App Privacy debe declararse el **tracking** correctamente (Identifiers / Usage Data → "Used to Track You"). Si decidieras NO rastrear, habría que quitar el framework ATT y la declaración; pero mantenemos ATT.

### B2. Suscripción (Guideline 3.1.2(c))
1. Compila la build nueva (Parte B4) y abre el paywall de Chat Premium (Chat ▸ agota la cuota gratuita o pulsa "Suscríbete").
2. Verifica visualmente que se ve: **título, "Chat Premium", el precio `X,XX €/mes` destacado**, el texto de auto-renovación, y los enlaces **Política de privacidad** y **Términos de uso (EULA)** (compruébalos: deben abrir en el navegador).
3. **Graba un screen recording** del paywall mostrando precio + ambos enlaces funcionando, para subir a las Notes.
4. **Metadatos (ya en el repo, pero verifica en App Store Connect tras subir):**
   - El enlace a la EULA estándar aparece en la **Descripción** (ambos idiomas).
   - El campo **Privacy Policy URL** está relleno: `https://apptolast.github.io/FamilyFilmApp/privacy-policy`.
   - *(Alternativa a la EULA en la descripción:* si prefieres, puedes dejar el campo "License Agreement" de App Store Connect en la EULA estándar de Apple — es la opción por defecto — y entonces el enlace en la descripción es un refuerzo.)*
5. Asegúrate de que en App Store Connect el producto **Chat Premium** está marcado como **suscripción auto-renovable** con su precio y duración (1 mes) configurados, y en estado "Ready to Submit" adjunto a esta versión.

### B3. Propiedad intelectual / capturas (Guideline 5.2.1)
1. **Crea el target de UI test (un comando, sin pasos manuales en Xcode)** — ya hecho en esta rama, pero es idempotente:
   ```bash
   cd iosApp && bundle exec ruby scripts/setup_screenshots_target.rb
   ```
   Añade el target `iosAppUITests` al `.xcodeproj` y lo registra en el scheme `iosApp`. Verifica con `xcodebuild -list -project iosApp/iosApp.xcodeproj` (debe listar `iosApp` y `iosAppUITests`). Ver `docs/screenshots-pipeline.md`.
2. **Login: se salta automáticamente.** El modo demo (`-FFADemoMode YES`) activa `ScreenshotMode`, que hace que `AuthViewModel` emita una sesión demo (sin Firebase) y arranca en Home. Además el host iOS salta el bootstrap de ATT/notificaciones/UMP/AdMob, así que no aparecen diálogos del sistema ni anuncios. **No necesitas cuenta de demo.** Home y Discover muestran títulos ficticios; Groups/Chat/Profile salen con estado vacío (sin datos sincronizados) — válido porque no llevan material de terceros. Si quieres capturas más ricas de Groups/Profile, inicia sesión una vez con una cuenta de demo real.
3. Genera las capturas: `cd iosApp && bundle exec fastlane screenshots` (o `fastlane snapshot`). Quedan en `iosApp/fastlane/screenshots/{en-GB,es-ES}/`.
4. **Revisa una a una**: ninguna debe contener pósters/títulos reales con copyright. Solo los títulos ficticios con arte generado.
5. **Sube las capturas nuevas** a App Store Connect (reemplazando las que mostraban Toy Story/Shrek), para iPhone 6.9"/6.5" y iPad 13", en ambos idiomas. Puedes usar el lane de fastlane de subida o subirlas a mano.
6. Revisa también que **App Preview (vídeos)** y el **icono** no contengan material de terceros.

### B4. Build, versión y subida
1. Sube el número de build (y versión si procede). El proyecto deriva versión del tag y build del +1 de App Store Connect vía Fastlane; para una subida manual desde Xcode incrementa `CFBundleVersion`.
2. Archiva en Xcode (o usa el lane de release) y sube a App Store Connect.
3. Adjunta esta versión: binario + capturas nuevas + metadatos + productos de suscripción.
4. En **App Review Information ▸ Notes** pega el texto de la Parte C y adjunta los **dos vídeos** (ATT y paywall).
5. Envía a revisión y responde al hilo del revisor con el texto de la Parte C.

---

## PARTE C — Textos para App Store Connect

### C1. Notes (App Review Information) — pégalo tal cual (EN)

```
Thank you for the detailed review. We addressed all four points in this build.

1) Guideline 2.1 — App Tracking Transparency
The ATT permission request was being triggered behind a network-dependent consent
chain, which on iOS/iPadOS 26 could be dropped if the app was not yet foreground-active.
We now request App Tracking Transparency FIRST, on the main thread, as soon as the app
becomes active on a fresh launch — before any tracking data is collected, and decoupled
from the notification and ad-consent prompts. A screen recording from a physical device
(fresh install, ATT prompt appearing, and the following flow) is attached to these notes.
Note: please ensure Settings ▸ Privacy & Security ▸ Tracking ▸ "Allow Apps to Request to
Track" is enabled on the review device, otherwise iOS suppresses all ATT prompts system-wide.

2) Guideline 5.2.1 — Intellectual Property
We removed all third-party copyrighted artwork from the App Store screenshots and metadata.
The screenshots now use entirely fictional titles with original, app-generated cover art.
Inside the app, title metadata and artwork are retrieved at runtime from The Movie Database
(TMDB) via its official public API (an industry-standard metadata provider). This is a
nominative/editorial use so users can identify and organize the titles they track; the app
hosts/streams no media, claims no affiliation with any studio, and displays a TMDB
attribution notice as required by TMDB's terms. We are happy to make any specific change you
can point to.

3) Guideline 3.1.2(c) — Subscriptions
The Chat Premium purchase flow now clearly displays the subscription title, billing period,
and the actual billed amount as the most prominent pricing element, plus an auto-renewal
disclosure and functional links to the Privacy Policy and to the Terms of Use (Apple's
standard EULA). The Terms of Use (EULA) link and the Privacy Policy link are also included
in the App Store description/metadata. A screen recording of the updated paywall is attached.

Test account (for Home/Groups/Profile and the paywall): <email> / <password>
```

> Sustituye `<email>`/`<password>` por la cuenta de demo. Puedes responder al revisor en español si prefieres (Apple lo permite), pero el inglés suele agilizar.

### C2. Versión en español (por si respondes en español)

```
Gracias por la revisión detallada. Hemos resuelto los cuatro puntos en esta build.

1) Guideline 2.1 — App Tracking Transparency
El prompt de ATT dependía de una cadena de consentimiento con llamada de red que, en
iOS/iPadOS 26, podía descartarse si la app aún no estaba activa en primer plano. Ahora
solicitamos ATT EN PRIMER LUGAR, en el hilo principal, en cuanto la app pasa a activa tras
una instalación limpia — antes de recolectar cualquier dato de seguimiento y desacoplado de
los diálogos de notificaciones y de consentimiento de anuncios. Adjuntamos un screen
recording desde un dispositivo físico. Nota: por favor verificad que en Ajustes ▸ Privacidad
y seguridad ▸ Seguimiento esté activado "Permitir que las apps soliciten seguimiento"; si no,
iOS suprime todos los prompts de ATT a nivel de sistema.

2) Guideline 5.2.1 — Propiedad intelectual
Hemos eliminado todo el arte con copyright de terceros de las capturas y los metadatos. Las
capturas usan ahora títulos ficticios con portadas originales generadas por la app. Dentro
de la app, los metadatos e imágenes de los títulos se obtienen en tiempo de ejecución de The
Movie Database (TMDB) mediante su API pública oficial (proveedor de metadatos estándar del
sector). Es un uso nominativo/editorial para que el usuario identifique y organice los
títulos que sigue; la app no aloja ni reproduce contenido, no reclama afiliación con ningún
estudio y muestra la atribución a TMDB exigida por sus términos. Haremos cualquier cambio
concreto que nos indiquéis.

3) Guideline 3.1.2(c) — Suscripciones
El flujo de compra de Chat Premium muestra ahora claramente el título de la suscripción, el
periodo de facturación y el importe cobrado como elemento de precio más destacado, además de
la cláusula de auto-renovación y enlaces funcionales a la Política de privacidad y a los
Términos de uso (EULA estándar de Apple). Los enlaces a EULA y privacidad también están en la
descripción/metadatos de la App Store. Adjuntamos un screen recording del paywall.

Cuenta de prueba (Home/Grupos/Perfil y el paywall): <email> / <password>
```

---

## PARTE D — Checklist antes de reenviar

- [ ] **ATT:** "Allow Apps to Request to Track" ON en el dispositivo de prueba.
- [ ] **ATT:** prompt aparece tras instalación limpia → vídeo grabado y subido a Notes.
- [ ] **ATT:** App Privacy declara tracking correctamente.
- [ ] **Suscripción:** paywall muestra precio `X,XX €/mes` destacado + auto-renovación + enlaces a privacidad y EULA que abren → vídeo grabado.
- [ ] **Suscripción:** EULA estándar enlazada en la descripción (ambos idiomas) y Privacy Policy URL relleno en App Store Connect.
- [ ] **Suscripción:** producto Chat Premium = auto-renovable, con precio/duración, "Ready to Submit" en esta versión.
- [ ] **IP:** target `iosAppUITests` añadido en Xcode; `fastlane screenshots` genera capturas con datos ficticios.
- [ ] **IP:** todas las capturas/preview/icono revisados — sin material de terceros.
- [ ] **IP:** capturas nuevas subidas reemplazando las antiguas (iPhone 6.9"/6.5", iPad 13", en-GB + es-ES).
- [ ] **Build:** `CFBundleVersion` incrementado, archivado y subido.
- [ ] **Notes + reply:** texto de la Parte C pegado, con cuenta de demo y los 2 vídeos adjuntos.

---

## Riesgo residual y plan B (Guideline 5.2.1)

Apple ya rechazó dos veces el argumento "solo usamos TMDB". El cambio decisivo de esta ronda es **eliminar el material con copyright de las capturas/metadatos** (lo más visible para el revisor). El contenido en vivo de TMDB dentro de la app es uso editorial estándar y muy común en la App Store. Si aun así rechazan por 5.2.1:
- Responde solicitando que indiquen **exactamente** qué elemento concreto infringe (Apple debe señalarlo).
- Solicita una **App Review Appointment** (Meet with Apple, martes/jueves) o escala al **App Review Board**.
- Último recurso (frágil y no recomendado salvo exigencia explícita): filtrar/ocultar pósters de estudios concretos en el feed por defecto.
