package vivid.money.elmslie.android.logger

import android.app.Application
import android.content.pm.ApplicationInfo
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector
import vivid.money.elmslie.core.config.ElmslieConfig

internal class DefaultInitContentProvider : EmptyContentProvider() {

    override fun init() {
        initDefaultLogger()
        initProcessDeathDetector()
    }

    private fun initProcessDeathDetector() = ProcessDeathDetector.init(context!!.applicationContext as Application)

    private fun initDefaultLogger() {
        val isDebug = 0 != context!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebug) ElmslieConfig.defaultDebugLogger() else ElmslieConfig.defaultReleaseLogger()
    }
}