package vivid.money.elmslie.samples.android.loader

import android.app.Application
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.logger.strategy.IgnoreLog

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ElmslieConfig.apply { if (BuildConfig.DEBUG) logger { always(IgnoreLog) } else logger { always(IgnoreLog) } }
    }
}