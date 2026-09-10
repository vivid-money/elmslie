package money.vivid.elmslie.core.config

import kotlin.concurrent.Volatile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import money.vivid.elmslie.core.logger.ElmslieLogConfiguration
import money.vivid.elmslie.core.logger.ElmslieLogger
import money.vivid.elmslie.core.logger.strategy.IgnoreLog
import money.vivid.elmslie.core.store.StoreListener
import money.vivid.elmslie.core.utils.ElmDispatcher

public object ElmslieConfig {

  @Volatile
  public var logger: ElmslieLogger = ElmslieLogConfiguration().apply { always(IgnoreLog) }.build()
    private set

  @Volatile
  public var elmDispatcher: CoroutineDispatcher = ElmDispatcher
    private set

  @Volatile
  public var shouldStopOnProcessDeath: Boolean = true
    private set

  @Volatile
  public var globalStoreListeners: Set<StoreListener<Any, Any, Any, Any>> = emptySet()
    private set

  /**
   * Configures logging and error handling
   *
   * Example:
   * ```
   * ElmslieConfig.logger {
   *   fatal(Crash)
   *   nonfatal(AndroidLog)
   *   debug(Ignore)
   * }
   * ```
   */
  public fun logger(config: (ElmslieLogConfiguration.() -> Unit)) {
    ElmslieLogConfiguration().apply(config).build().also { logger = it }
  }

  /**
   * Configures CoroutineDispatcher for performing operations in background. Default is
   * [Dispatchers.Default]
   */
  public fun elmDispatcher(builder: () -> CoroutineDispatcher) {
    elmDispatcher = builder()
  }

  public fun shouldStopOnProcessDeath(builder: () -> Boolean) {
    shouldStopOnProcessDeath = builder()
  }

  public fun globalStoreListeners(builder: () -> Set<StoreListener<Any, Any, Any, Any>>) {
    globalStoreListeners = builder()
  }
}
