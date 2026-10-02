---
sidebar_position: 1
title: Installation
---

# Installation

VoxaVis supports Android (minSdk 24) and iOS (15 and later, on devices and
Apple Silicon simulators).

## Android and Kotlin Multiplatform

```kotlin
dependencies {
    implementation("com.musicmuni:voxavis:2.0.0")
}
```

VoxaVis brings Compose runtime, foundation, UI and Material 3 with it, and
needs your app to compile against SDK 37 (`compileSdk = 37`). Your app needs
the `INTERNET` permission, for licensing:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

In a Kotlin Multiplatform project, add the dependency to `commonMain`; the iOS
targets get the same API.

## iOS: Swift Package Manager

In Xcode, **File > Add Package Dependencies**, and enter
`https://github.com/musicmuni/voxavis`. Or in `Package.swift`:

```swift
.package(url: "https://github.com/musicmuni/voxavis", from: "2.0.0")
```

Then `import voxavis`.

The XCFramework covers devices and Apple Silicon simulators. If your app builds
for every simulator architecture (a generic simulator destination, CI), add
`EXCLUDED_ARCHS[sdk=iphonesimulator*] = x86_64` to its build settings.

## iOS: CocoaPods

```ruby
pod 'VoxaVis', :podspec => 'https://raw.githubusercontent.com/musicmuni/voxavis/2.0.0/VoxaVis.podspec'
```

## Next

Licensing comes before the first feature component:
[Licensing](../guides/licensing.md).
