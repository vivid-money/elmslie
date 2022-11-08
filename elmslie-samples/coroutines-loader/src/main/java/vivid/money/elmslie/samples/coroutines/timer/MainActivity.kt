package vivid.money.elmslie.samples.coroutines.timer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import com.google.android.material.snackbar.Snackbar
import vivid.money.elmslie.android.elmStore
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.samples.coroutines.timer.elm.Effect
import vivid.money.elmslie.samples.coroutines.timer.elm.Event
import vivid.money.elmslie.samples.coroutines.timer.elm.State
import vivid.money.elmslie.samples.coroutines.timer.elm.storeFactory

internal class MainActivity : AppCompatActivity(R.layout.activity_main),
    ElmRendererDelegate<Effect, State> {

    override val store by elmStore(
        storeFactory = ::createStore,
    )

    @Suppress("LeakingThis")
    private val renderer =
        ElmRenderer(
            delegate = this,
            screenLifecycle = lifecycle,
        )

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val starter =
        ViewBasedStoreStarter(
            storeProvider = { store },
            screenLifecycle = lifecycle,
            initEventProvider = { Event.Init },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.start).setOnClickListener { store.accept(Event.Start) }
        findViewById<Button>(R.id.stop).setOnClickListener { store.accept(Event.Stop) }
    }

    @SuppressLint("SetTextI18n")
    override fun render(state: State) {
        findViewById<TextView>(R.id.start).visibility = if (state.isStarted) GONE else VISIBLE
        findViewById<TextView>(R.id.stop).visibility = if (state.isStarted) VISIBLE else GONE
        findViewById<TextView>(R.id.currentValue).text = "Seconds passed: ${state.secondsPassed}"
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        is Effect.Error -> Snackbar.make(
            findViewById(R.id.content),
            "Error!",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    @Suppress("UnusedPrivateMember")
    private fun createStore(savedStateHandle: SavedStateHandle) = storeFactory()
}
