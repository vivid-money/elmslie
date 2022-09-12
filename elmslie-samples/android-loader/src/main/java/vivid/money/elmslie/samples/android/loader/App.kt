package vivid.money.elmslie.samples.android.loader

import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.logger.strategy.IgnoreLog

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ElmslieConfig.apply { if (BuildConfig.DEBUG) logger { always(IgnoreLog) } else logger { always(IgnoreLog) } }
    }
}
