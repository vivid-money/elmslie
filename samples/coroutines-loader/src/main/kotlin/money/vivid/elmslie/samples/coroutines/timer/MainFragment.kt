package money.vivid.elmslie.samples.coroutines.timer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import money.vivid.elmslie.android.RetainedElmStore.Companion.StateBundleKey
import money.vivid.elmslie.android.renderer.ElmRendererDelegate
import money.vivid.elmslie.android.renderer.androidElmStore
import money.vivid.elmslie.samples.coroutines.timer.elm.Effect
import money.vivid.elmslie.samples.coroutines.timer.elm.Event
import money.vivid.elmslie.samples.coroutines.timer.elm.State
import money.vivid.elmslie.samples.coroutines.timer.elm.storeFactory

internal class MainFragment : Fragment(R.layout.fragment_main), ElmRendererDelegate<Effect, State> {

  companion object {
    private const val ARG = "ARG"
    private const val GENERATED_ID = "GENERATED_ID"

    fun newInstance(id: String): Fragment = MainFragment().apply { arguments = bundleOf(ARG to id) }
  }

  private val store by
    androidElmStore(saveState = { state -> putString(GENERATED_ID, state.generatedId) }) {
      storeFactory(
        id = get(ARG)!!,
        generatedId = get<Bundle>(StateBundleKey)?.getString(GENERATED_ID),
      )
    }

  private lateinit var startButton: Button
  private lateinit var stopButton: Button
  private lateinit var currentValueText: TextView
  private lateinit var screenIdText: TextView
  private lateinit var generatedIdText: TextView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    startButton = view.findViewById(R.id.start)
    stopButton = view.findViewById(R.id.stop)
    currentValueText = view.findViewById(R.id.currentValue)
    screenIdText = view.findViewById(R.id.screenId)
    generatedIdText = view.findViewById(R.id.generatedID)

    startButton.setOnClickListener { store.accept(Event.Start) }
    stopButton.setOnClickListener { store.accept(Event.Stop) }
  }

  @SuppressLint("SetTextI18n")
  override fun render(state: State) {
    screenIdText.text = state.id
    generatedIdText.text = state.generatedId
    startButton.visibility = if (state.isStarted) GONE else VISIBLE
    stopButton.visibility = if (state.isStarted) VISIBLE else GONE
    currentValueText.text = "Seconds passed: ${state.secondsPassed}"
  }

  override fun handleEffect(effect: Effect) =
    when (effect) {
      is Effect.Error ->
        Snackbar.make(requireView().findViewById(R.id.content), "Error!", Snackbar.LENGTH_SHORT)
          .show()
    }
}
