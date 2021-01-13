package vivid.money.elmslie.android.processdeath

import android.app.Activity
import android.app.Application
import android.os.Bundle

object ProcessDeathDetector {

    private var isFirstStart = true

    /** Will be true in one onCreate..onResume cycle */
    var isRestoringAfterProcessDeath = false
        private set

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (!isRestoringAfterProcessDeath && isFirstStart && savedInstanceState != null) {
                    isRestoringAfterProcessDeath = true
                }
                isFirstStart = false
            }

            override fun onActivityResumed(activity: Activity) {
                isRestoringAfterProcessDeath = false
            }

            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivityStopped(activity: Activity) = Unit

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}