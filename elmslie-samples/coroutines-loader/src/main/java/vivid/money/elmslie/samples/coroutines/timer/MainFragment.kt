package vivid.money.elmslie.samples.coroutines.timer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import vivid.money.elmslie.android.elmStore
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.samples.coroutines.timer.elm.*
import vivid.money.elmslie.samples.coroutines.timer.elm.storeFactory

internal class MainFragment :
    Fragment(R.layout.fragment_main),
    ElmRendererDelegate<Effect, State> {

    companion object {
        private const val ARG = "ARG"

        fun newInstance(id: String): Fragment = MainFragment().apply {
            arguments = Bundle().apply {
                putString(ARG, id)
            }
        }
    }

    override val store by elmStore(
        storeFactory = { savedStateHandle ->
            storeFactory(
                id = savedStateHandle[ARG]!!,
            )
        }
    )

    @Suppress("LeakingThis")
    private val renderer =
        ElmRenderer(
            delegate = this,
            screenLifecycle = lifecycle,
        )

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var currentValueText: TextView
    private lateinit var screenIdText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton = view.findViewById(R.id.start)
        stopButton = view.findViewById(R.id.stop)
        currentValueText = view.findViewById(R.id.currentValue)
        screenIdText = view.findViewById(R.id.screenId)

        startButton.setOnClickListener { store.accept(Event.Start) }
        stopButton.setOnClickListener { store.accept(Event.Stop) }
    }

    @SuppressLint("SetTextI18n")
    override fun render(state: State) {
        screenIdText.text = state.id
        startButton.visibility = if (state.isStarted) GONE else VISIBLE
        stopButton.visibility = if (state.isStarted) VISIBLE else GONE
        currentValueText.text = "Seconds passed: ${state.secondsPassed}"
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        is Effect.Error -> Snackbar.make(
            requireView().findViewById(R.id.content),
            "Error!",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
