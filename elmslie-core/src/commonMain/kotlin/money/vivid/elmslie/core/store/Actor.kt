package money.vivid.elmslie.core.store

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.switcher.Switcher

abstract class Actor<Command : Any, Event : Any> {

  private val switchers = mutableMapOf<Any, Switcher>()
  private val mutex = Mutex()

  /** Executes a command. This method is performed on the [ElmslieConfig.elmDispatcher]. */
  abstract fun execute(command: Command): Flow<Event>

  protected fun <T : Any> Flow<T>.mapEvents(
    eventMapper: (T) -> Event? = { null },
    errorMapper: (error: Throwable) -> Event? = { null },
  ) =
    mapNotNull { eventMapper(it) }
      .catch { it.logErrorEvent(errorMapper)?.let { event -> emit(event) } ?: throw it }

  /**
   * Extension function to switch the flow by a given key and optional delay. This function ensures
   * that only one flow with the same key is active at a time.
   *
   * @param key The key to identify the flow.
   * @param delay The delay in milliseconds before launching the initial flow. Defaults to 0.
   * @return A new flow that emits the values from the original flow.
   */
  protected fun <T : Any> Flow<T>.switch(key: Any, delay: Duration = 0.milliseconds): Flow<T> {
    return flow {
      val switcher = mutex.withLock { switchers.getOrPut(key) { Switcher() } }
      switcher.switch(delay) { this@switch }.collect { emit(it) }
    }
  }

  /**
   * Cancels the switch flow(s) by a given key(s).
   *
   * @param keys The keys to identify the flows.
   * @return A new flow that emits [Unit] when switch flows are cancelled.
   */
  protected fun cancelSwitchFlows(vararg keys: Any): Flow<Unit> {
    return flow {
      keys.forEach { key -> mutex.withLock { switchers.remove(key)?.cancel() } }
      emit(Unit)
    }
  }

  private fun Throwable.logErrorEvent(errorMapper: (Throwable) -> Event?): Event? {
    return errorMapper(this).also { ElmslieConfig.logger.nonfatal(error = this) }
  }
}
