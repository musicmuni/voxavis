---
sidebar_position: 1
title: Licensing
---

# Licensing

VoxaVis needs a licence. Initialize it once at app start, before the first
feature component (`SingingPractice`, `InstantPitchMonitor`,
`ScrollingPitchMonitor`, `PracticeReview`) is shown; those throw
`VoxaVisNotInitializedException` without it. Charts, meters, navigation and
primitives do not need it.

VoxaVis licenses through the same server and the same proxy contract as
VoxaTrace, so an app using both can serve them from one endpoint.

## Three ways to initialize

| | Proxy (recommended) | App attestation | Server key |
| --- | --- | --- | --- |
| Call | `VV.initialize(proxyEndpoint)` | `VV.initializeWithAttestation(apiKey)` | `VV.initializeForServer(apiKey)` |
| Where the API key lives | Your server | Inside the app | Inside the app or tool |
| Use for | Shipping apps with a backend | Shipping apps without a backend | Trying VoxaVis out, demos, tools |

An API key inside a shipped app can be read by anyone who has the app. Prefer
the proxy.

## Proxy

Your backend exposes one endpoint. It receives the device's registration
request, adds your API key, forwards it to the licence server and returns the
device token. What the endpoint receives and returns is described in
VoxaTrace's [authentication guide](https://voxatrace.ai/guides/authentication).

Kotlin:

```kotlin
VV.initialize(
    proxyEndpoint = "https://your-server.com/voxatrace/register",
    context = applicationContext, // Android; ignored on iOS
    proxyAuth = { "Bearer ${auth.freshIdToken()}" },
)
```

Swift:

```swift
try VV.initialize(
    proxyEndpoint: "https://your-server.com/voxatrace/register",
    proxyAuthProvider: { "Bearer \(Session.token)" }
)
```

The Authorization hook is optional and comes in two forms: `proxyAuthProvider`
returns a header you already hold, and `proxyAuth` (Kotlin only) is a suspend
function for a token fetched asynchronously, such as a Firebase ID token. Pass
one or neither.

### What your endpoint's answers mean

- **403** is a licence refusal. The SDK switches off and remembers it until a
  later registration succeeds.
- **401**, or any other failure, is treated like the network being down. If
  the device already holds a token it keeps working; if it holds none,
  `initialize` throws and the next call registers normally. A 401 before
  anyone has signed in therefore does no lasting harm.

### Start-up never waits on a held token

A device that registered before holds a token. `initialize` returns at once
and re-checks it in the background, renewing it in its last week. Only a first
registration, with no token yet, waits on the network.

## App attestation

```kotlin
VV.initializeWithAttestation(
    apiKey = "sk_live_...",
    context = this,
    callback = object : VV.InitCallback {
        override fun onComplete(success: Boolean, error: String?) {
            if (!success) Log.w("VoxaVis", "licence: $error")
        }
    },
)
```

```swift
VV.initializeWithAttestation(apiKey: "sk_live_...") { success, error in
    if !success { print("licence: \(error ?? "")") }
}
```

It uses Play Integrity on Android and App Attest on iOS to show the app is
genuine before registering.

## Server key

```kotlin
VV.initializeForServer(apiKey = "sk_test_...")
```

```swift
try VV.initializeForServer(apiKey: "sk_test_...")
```

The demo apps use this so they run without a backend. Do not ship it.

## Checking

`VV.isInitialized` tells you whether the SDK is ready, so a screen can show
something else instead of a feature component that would throw.
