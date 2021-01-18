package vivid.money.elmslie.samples.android.loader

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.samples.android.loader.elm.Effect
import vivid.money.elmslie.samples.android.loader.elm.Event
import vivid.money.elmslie.samples.android.loader.elm.State
import vivid.money.elmslie.samples.android.loader.elm.storeFactory

class MainActivity : ElmActivity<Event, Effect, State>() {

    override val initEvent: Event = Event.Init

    override fun createStore() = storeFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.reload).setOnClickListener { store.accept(Event.ClickReload) }
    }

    override fun render(state: State) {
        val currentValue = state.currentValue?.toString() ?: "Unknown"
        val currentValueText = "Current value: $currentValue"
        findViewById<TextView>(R.id.currentValue).text = currentValueText
        findViewById<View>(R.id.overlay).visibility = if (state.isLoading) View.VISIBLE else View.GONE
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        Effect.ShowError -> Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
    }
}