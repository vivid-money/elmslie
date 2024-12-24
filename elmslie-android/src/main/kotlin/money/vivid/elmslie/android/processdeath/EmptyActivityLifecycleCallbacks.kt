package money.vivid.elmslie.android.processdeath

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal interface EmptyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

  override fun onActivityStarted(activity: Activity) = Unit

  override fun onActivityResumed(activity: Activity) = Unit

  override fun onActivityPaused(activity: Activity) = Unit

  override fun onActivityStopped(activity: Activity) = Unit

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

  override fun onActivityDestroyed(activity: Activity) = Unit
}
