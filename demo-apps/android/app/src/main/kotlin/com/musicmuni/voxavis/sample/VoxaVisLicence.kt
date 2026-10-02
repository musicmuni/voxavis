package com.musicmuni.voxavis.sample

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicmuni.voxavis.VV
import com.musicmuni.voxavis.exceptions.VoxaVisKilledException
import kotlin.concurrent.thread

/**
 * Starts VoxaVis once per process and says how it went.
 *
 * Only the four feature canvases need this: `SingingPractice`,
 * `InstantPitchMonitor`, `ScrollingPitchMonitor` and `PracticeReview` throw
 * until `VV` is initialized. Every chart, meter, primitive and navigation bar
 * works without it, so the demo keeps those screens open when no key is set.
 */
object VoxaVisLicence {

    sealed interface Status {
        /** No `voxavis.apiKey` in local.properties. */
        data object NotConfigured : Status

        /** Registering the device with the licence server. */
        data object Starting : Status

        data object Ready : Status

        data class Failed(val message: String) : Status
    }

    var status: Status by mutableStateOf(
        if (BuildConfig.VOXAVIS_API_KEY.isBlank()) Status.NotConfigured else Status.Starting,
    )
        private set

    private var started = false

    fun start(context: Context) {
        if (started || status == Status.NotConfigured) return
        started = true
        val appContext = context.applicationContext

        // Off the main thread: a first start registers the device over the
        // network and waits up to ten seconds for the answer.
        thread(name = "voxavis-licence") {
            val result = try {
                // DEMO ONLY - DO NOT USE IN PRODUCTION
                // This uses direct API key initialization, which embeds the key
                // in the app. For production apps, use one of these instead:
                //   - VV.initialize(proxyEndpoint, context) - Recommended for apps
                //     with a backend: your server holds the key and registers
                //     the device on the app's behalf.
                //   - VV.initializeWithAttestation(apiKey, context, callback = ...)
                //     - For apps without a backend (Play Integrity, so the app
                //     must be installed from the Play Store).
                VV.initializeForServer(
                    apiKey = BuildConfig.VOXAVIS_API_KEY,
                    context = appContext,
                    debugLogging = BuildConfig.DEBUG,
                )
                Status.Ready
            } catch (e: VoxaVisKilledException) {
                Status.Failed(e.message ?: "Licence validation failed")
            }
            Handler(Looper.getMainLooper()).post { status = result }
        }
    }
}
