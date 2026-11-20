package com.kapav.ridemode

import android.content.Intent
import android.os.SystemClock
import com.kapav.ridemode.feature.ride.RideNotificationReceiver
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.kapav.ridemode.feature.ride.RideSession
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kapav.ridemode.feature.ride.RideActivity
import com.kapav.ridemode.feature.ride.RideNavigationViewModel
import com.kapav.ridemode.feature.widgets.GoogleMapsController
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** No route, search request, location simulation or permission grant is performed by this test. */
@RunWith(AndroidJUnit4::class)
class RideNavigationRecreationTest {
    @Test fun notificationEndDoesNotRequireAForegroundActivity() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        try {
            instrumentation.runOnMainSync {
                RideSession.start(instrumentation.targetContext)
                assertTrue(RideSession.active.value)
            }
            instrumentation.targetContext.sendBroadcast(Intent(instrumentation.targetContext,
                RideNotificationReceiver::class.java).setAction(RideNotificationReceiver.ACTION_END_RIDE))
            val deadline = SystemClock.elapsedRealtime() + 3_000
            while (RideSession.active.value && SystemClock.elapsedRealtime() < deadline) SystemClock.sleep(50)
            instrumentation.runOnMainSync { assertFalse(RideSession.active.value) }
        } finally {
            instrumentation.runOnMainSync { RideSession.end(instrumentation.targetContext) }
        }
    }

    @Test fun recreationRetainsControllerStateAndNewActivityCanUseIt() {
        lateinit var originalModel: RideNavigationViewModel
        lateinit var originalController: GoogleMapsController
        try { ActivityScenario.launch(RideActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                originalModel = ViewModelProvider(activity)[RideNavigationViewModel::class.java]
                originalController = originalModel.controller("recreation-regression")
                originalController.expand()
            }
            scenario.recreate()
            scenario.onActivity { activity ->
                val model = ViewModelProvider(activity)[RideNavigationViewModel::class.java]
                assertSame(originalModel, model)
                assertSame(originalController, model.controller("recreation-regression"))
                assertTrue(originalController.state.value.expanded)
                originalController.collapse()
                assertFalse(originalController.state.value.expanded)
            }
        } } finally {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.runOnMainSync { RideSession.end(instrumentation.targetContext) }
        }
    }
}
