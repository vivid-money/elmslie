package vivid.money.elmslie.core.store

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vivid.money.elmslie.core.ElmScope
import java.util.concurrent.LinkedBlockingQueue

/**
 * Caches effects until there is at least one collector.
 *
 * Note, that effects from the cache are replayed only for the first one.
 *
 * Wrap the store with the instance of [EffectCachingElmStore] to get the desired behavior like this:
 * ```
 * ```
 */
// TODO Should be moved to android artifact?
class EffectCachingElmStore<Event : Any, State : Any, Effect : Any>(
    private val elmStore: Store<Event, Effect, State>,
) : Store<Event, Effect, State> by elmStore {

    private val effectsMutex = Mutex()
    private val effectsCache = mutableListOf<Effect>()
    private val effectsFlow = MutableSharedFlow<Effect>()
    private val storeScope = ElmScope("CachedStoreScope")

    init {
        storeScope.launch {
            elmStore.effects.collect { effect ->
                if (effectsFlow.subscriptionCount.value > 0) {
                    effectsFlow.emit(effect)
                } else {
                    effectsMutex.withLock {
                        effectsCache.add(effect)
                    }
                }
            }
        }
    }

    override fun stop() {
        elmStore.stop()
        storeScope.cancel()
    }

    override val effects: Flow<Effect> =
        effectsFlow.onSubscription {
            effectsMutex.withLock {
                for (effect in effectsCache) {
                    emit(effect)
                }
                effectsCache.clear()
            }
        }
}
