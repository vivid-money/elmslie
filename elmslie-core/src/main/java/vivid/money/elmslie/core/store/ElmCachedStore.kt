package vivid.money.elmslie.core.store

import java.util.concurrent.LinkedBlockingQueue
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vivid.money.elmslie.core.ElmScope

/**
 * Caches effects until there is at least one collector.
 *
 * Note, that effects from the cache are replayed only for the first one.
 *
 * Wrap the store with the instance of [ElmCachedStore] to get the desired behavior like this:
 * ```
 * ```
 */
// TODO Should be moved to android artifact?
class ElmCachedStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    private val elmStore: ElmStore<Event, State, Effect, Command>,
) : Store<Event, Effect, State> by elmStore {

    private val effectsCache = LinkedBlockingQueue<Effect>()
    private val effectsFlow = MutableSharedFlow<Effect>()
    private val storeScope = ElmScope("CachedStoreScope")

    init {
        storeScope.launch {
            elmStore.effects().collect { effect ->
                if (effectsFlow.subscriptionCount.value > 0) {
                    effectsFlow.emit(effect)
                } else {
                    effectsCache.add(effect)
                }
            }
        }
    }

    override fun stop() {
        elmStore.stop()
        storeScope.cancel()
    }

    override fun effects(): Flow<Effect> =
        effectsFlow.onSubscription {
            for (effect in effectsCache) {
                emit(effect)
            }
            effectsCache.clear()
        }
}
