package vivid.money.elmslie.samples.android.loader

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import com.google.android.material.snackbar.Snackbar
import vivid.money.elmslie.android.elmStore
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.samples.android.loader.elm.Effect
import vivid.money.elmslie.samples.android.loader.elm.Event
import vivid.money.elmslie.samples.android.loader.elm.State
import vivid.money.elmslie.samples.android.loader.elm.storeFactory

class MainActivity : AppCompatActivity(R.layout.activity_main), ElmRendererDelegate<Effect, State> {

    override val store by elmStore(
        storeFactory = ::createStore,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.reload).setOnClickListener { store.accept(Event.Ui.ClickReload) }
    }

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
            initEventProvider = { Event.Ui.Init },
        )

    override fun render(state: State) {
        findViewById<TextView>(R.id.currentValue).text = when {
            state.isLoading -> "Loading..."
            state.value == null -> "Value = Unknown"
            else -> "Value = ${state.value}"
        }
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        Effect.ShowError -> Snackbar.make(
            findViewById(R.id.content),
            "Error!",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    @Suppress("UnusedPrivateMember")
    private fun createStore(savedStateHandle: SavedStateHandle): Store<Event, Effect, State> =
        storeFactory()
}
