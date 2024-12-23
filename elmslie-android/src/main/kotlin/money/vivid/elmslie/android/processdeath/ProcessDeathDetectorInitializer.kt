package money.vivid.elmslie.android.processdeath

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class ProcessDeathDetectorInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    ProcessDeathDetector.init(context.applicationContext as Application)
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
