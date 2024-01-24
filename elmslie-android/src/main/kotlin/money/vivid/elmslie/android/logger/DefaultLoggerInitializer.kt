package money.vivid.elmslie.android.logger

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer
import money.vivid.elmslie.core.config.ElmslieConfig

class DefaultLoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val isDebug = 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebug) ElmslieConfig.defaultDebugLogger() else ElmslieConfig.defaultReleaseLogger()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
