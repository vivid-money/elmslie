package money.vivid.elmslie.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.switcher.Switcher

abstract class Actor<Command : Any, Event : Any> {

    @PublishedApi
    internal val switchers = mutableMapOf<String, Switcher>()
    private val mutex = Mutex()

    /**
     * Executes a command. This method is performed on the [Dispatchers.Default]
     * [kotlinx.coroutines.Dispatchers.Default] which is set by ElmslieConfig.elmDispatcher()
     */
    abstract fun execute(command: Command): Flow<Event>

    fun <T : Any> Flow<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (error: Throwable) -> Event? = { null },
    ) = mapNotNull { eventMapper(it) }
        .catch { it.logErrorEvent(errorMapper)?.let { event -> emit(event) } ?: throw it }

    protected fun <T : Any, Command : Any> Flow<T>.asSwitchFlow(command: Command, delayMillis: Long = 0): Flow<T> {
        val key = command::class.simpleName.orEmpty()
        return asSwitchFlow(key, delayMillis)
    }

    protected fun <T : Any> Flow<T>.asSwitchFlow(customKey: String, delayMillis: Long = 0): Flow<T> {
        return flow {
            val switcher = mutex.withLock {
                switchers.getOrPut(customKey) {
                    Switcher()
                }
            }
            switcher.switch(delayMillis) { this@asSwitchFlow }.collect {
                emit(it)
            }
        }
    }

    protected inline fun <reified T : Any> cancelSwitchFlow(): Flow<Event> {
        val key = T::class.simpleName.orEmpty()
        return cancelSwitchFlow(key)
    }

    protected fun cancelSwitchFlow(
        vararg keys: String,
    ): Flow<Event> {
        return flow<Any> {
            keys.forEach { key ->
                mutex.withLock {
                    switchers.remove(key)?.cancel()
                }
            }
        }.mapEvents()
    }

    private fun Throwable.logErrorEvent(errorMapper: (Throwable) -> Event?): Event? {
        return errorMapper(this).also {
            ElmslieConfig.logger.nonfatal(error = this)
        }
    }
}
