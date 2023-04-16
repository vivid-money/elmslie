package vivid.money.elmslie.samples.coroutines.timer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlin.random.Random

internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @Suppress("MagicNumber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val screenId = Random.nextInt(100).toString()
                replace(R.id.container, MainFragment.newInstance(screenId))
            }
        }
    }
}
