# App Store Screenshots

Place manually captured App Store screenshots here before running:

```sh
cd iosApp
bundle exec fastlane ios upload_store_assets
```

Use one folder per App Store locale:

```text
fastlane/screenshots/en-GB/
fastlane/screenshots/es-ES/
```

Suggested filenames:

```text
iPhone_6_5_01_discover.png
iPhone_6_5_02_groups.png
iPhone_6_5_03_recommendation.png
iPhone_6_5_04_chat.png
iPhone_6_5_05_profile.png
iPad_13_01_discover.png
```

Fastlane uploads screenshots it finds under these folders. Keep only images you want App Store Connect to receive.
