package money.vivid.elmslie.android.processdeath

import android.app.Activity
import android.app.Application
import android.os.Bundle

object ProcessDeathDetector {

  private var isFirstStart = true

  /** Will be true in one onCreate..onResume cycle */
  var isRestoringAfterProcessDeath = false
    private set

  internal fun init(app: Application) {
    app.registerActivityLifecycleCallbacks(
      object : EmptyActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
          if (!isRestoringAfterProcessDeath && isFirstStart && savedInstanceState != null) {
            isRestoringAfterProcessDeath = true
          }
          isFirstStart = false
        }

        override fun onActivityResumed(activity: Activity) {
          isRestoringAfterProcessDeath = false
        }
      }
    )
  }
}
