package vivid.money.elmslie.android.storestarter

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector
import vivid.money.elmslie.android.processdeath.StopElmOnProcessDeath
import vivid.money.elmslie.android.storeholder.StoreHolder

class ViewBasedStoreStarter<Event : Any, Effect : Any, State : Any>(
    private val storeHolder: StoreHolder<Event, Effect, State>,
    screenLifecycle: Lifecycle,
    private val initEventProvider: () -> Event,
    private val activityProvider: () -> Activity,
) {

    private var isAfterProcessDeath = false

    init {
        screenLifecycle.coroutineScope.launchWhenCreated {
            saveProcessDeathState()
            triggerInitEventIfNecessary()
        }
    }

    private fun saveProcessDeathState() {
        isAfterProcessDeath = ProcessDeathDetector.isRestoringAfterProcessDeath
    }

    private fun triggerInitEventIfNecessary() {
        if (!storeHolder.isStarted && isAllowedToRun()) {
            storeHolder.store.accept(initEventProvider.invoke())
        }
    }

    private fun isAllowedToRun() =
        !isAfterProcessDeath || activityProvider() !is StopElmOnProcessDeath
}
