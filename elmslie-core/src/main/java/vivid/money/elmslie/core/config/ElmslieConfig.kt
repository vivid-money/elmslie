package vivid.money.elmslie.core.config

import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.NoOpLogger

object ElmslieConfig {

    @Volatile
    var logger: ElmslieLogger = NoOpLogger
}
