package vivid.money.elmslie.android.storestarter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.Store

class ViewBasedStoreStarter<Event : Any>(
    private val storeProvider: () -> Store<Event, *, *>,
    screenLifecycle: Lifecycle,
    private val initEventProvider: () -> Event,
) {

    private var isAfterProcessDeath = false
    private val store by fastLazy {
        storeProvider.invoke()
    }

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
        if (!store.isStarted && isAllowedToRun()) {
            store.accept(initEventProvider.invoke())
        }
    }

    private fun isAllowedToRun() =
        !isAfterProcessDeath || !ElmslieConfig.shouldStopElmOnProcessDeath
}
