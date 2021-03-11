package vivid.money.elmslie.samples.android.loader

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.samples.android.loader.elm.Effect
import vivid.money.elmslie.samples.android.loader.elm.Event
import vivid.money.elmslie.samples.android.loader.elm.State
import vivid.money.elmslie.samples.android.loader.elm.storeFactory

class MainActivity : ElmActivity<Event, Effect, State>() {

    override val initEvent: Event = Event.Ui.Init

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.reload).setOnClickListener { store.accept(Event.Ui.ClickReload) }
    }

    override fun createStore() = storeFactory()

    override fun render(state: State) {
        findViewById<TextView>(R.id.currentValue).text = when {
            state.isLoading -> "Loading..."
            state.value == null -> "Value = Unknown"
            else -> "Value = ${state.value}"
        }
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        Effect.ShowError -> Snackbar.make(findViewById(R.id.content), "Error!", Snackbar.LENGTH_SHORT).show()
    }
}