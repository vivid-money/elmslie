package money.vivid.elmslie.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import money.vivid.elmslie.core.switcher.Switcher
import kotlin.reflect.KClass

abstract class Actor<Command : Any, Event : Any> {

    private val switchers = mutableMapOf<KClass<out Any>, Switcher>()
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
        .catch { errorMapper(it)?.let { event -> emit(event) } ?: throw it }

    protected fun <T : Any, Command : Any> Flow<T>.asSwitchFlow(command: Command, delayMillis: Long = 0): Flow<T> {
        return flow {
            val switcher = mutex.withLock {
                switchers.getOrPut(command::class) {
                    Switcher()
                }
            }
            switcher.switch(delayMillis) { this@asSwitchFlow }.collect {
                emit(it)
            }
        }
    }

    protected fun <T : Any> cancelSwitchFlow(command: KClass<out Any>): Flow<T> {
        return switchers[command]?.cancel() ?: emptyFlow()
    }
}
