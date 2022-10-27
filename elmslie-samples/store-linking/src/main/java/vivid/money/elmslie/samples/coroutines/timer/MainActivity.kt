package vivid.money.elmslie.samples.coroutines.timer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vivid.money.elmslie.android.androidstore.store
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.NoOpActor
import vivid.money.elmslie.core.store.linking.linkTo
import vivid.money.elmslie.coroutines.ElmStoreCompat
import vivid.money.elmslie.coroutines.states
import vivid.money.elmslie.samples.coroutines.timer.elm.controller.ControllerEvent
import vivid.money.elmslie.samples.coroutines.timer.elm.controller.ControllerReducer
import vivid.money.elmslie.samples.coroutines.timer.elm.controller.ControllerState
import vivid.money.elmslie.samples.coroutines.timer.elm.timerstore.*
import vivid.money.elmslie.samples.coroutines.timer.elm.timerstore.TimerActor
import vivid.money.elmslie.samples.coroutines.timer.elm.timerstore.TimerEvent
import vivid.money.elmslie.samples.coroutines.timer.elm.timerstore.TimerReducer
import vivid.money.elmslie.samples.coroutines.timer.elm.timerstore.TimerState

@Suppress("MagicNumber")
internal class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val timerStore1 by store(key = "Store1") {
        ElmStoreCompat(
            initialState = TimerState(
                secondsPassed = 1,
            ),
            reducer = TimerReducer,
            actor = TimerActor(3),
            startEvent = TimerEvent.Init,
        )
    }

    private val timerStore2 by store(key = "Store2") {
        ElmStoreCompat(
            initialState = TimerState(
                secondsPassed = 4,
            ),
            reducer = TimerReducer,
            actor = TimerActor(10),
            startEvent = TimerEvent.Init,
        )
    }

    private val controller by store {
        ElmStore(
            initialState = ControllerState(),
            reducer = ControllerReducer,
            actor = NoOpActor(),
            startEvent = ControllerEvent.Init,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        linkStores()
        findViewById<ComposeView>(R.id.content).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Screen()
            }
        }
    }

    private fun linkStores() {
        timerStore1.linkTo(
            producerStore = controller,
            stateMapper = { prevState, currentState ->
                if (prevState.isStarted != currentState.isStarted) return@linkTo null
                if (currentState.isStarted) {
                    return@linkTo TimerEvent.Start
                } else {
                    return@linkTo TimerEvent.Stop
                }
            }
        )

        controller.linkTo(
            producerStore = timerStore1,
            effectMapper = { _, timerEffect ->
                when (timerEffect) {
                    is TimerEffect.Error -> {
                        ControllerEvent.Stop
                    }
                }
            }
        )

        timerStore2.linkTo(
            producerStore = controller,
            stateMapper = { prevState, currentState ->
                if (prevState.isStarted != currentState.isStarted) return@linkTo null
                if (currentState.isStarted) {
                    return@linkTo TimerEvent.Start
                } else {
                    return@linkTo TimerEvent.Stop
                }
            }
        )

        controller.linkTo(
            producerStore = timerStore2,
            effectMapper = { _, timerEffect ->
                when (timerEffect) {
                    is TimerEffect.Error -> {
                        ControllerEvent.Stop
                    }
                }
            }
        )
    }

    @Composable
    private fun Screen() {
        val timerState1 by timerStore1.states.collectAsState(initial = timerStore1.currentState)
        val timerState2 by timerStore2.states.collectAsState(initial = timerStore2.currentState)

        Column {
            Row {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(all = 32.dp),
                    text = "Timer1",
                    fontSize = 32.sp,
                )

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(all = 32.dp),
                    text = timerState1.secondsPassed.toString(),
                    fontSize = 32.sp,
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(all = 32.dp),
                    text = "Timer2",
                    fontSize = 32.sp,
                )

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(all = 32.dp),
                    text = timerState2.secondsPassed.toString(),
                    fontSize = 32.sp,
                )
            }
            Row {
                Button(modifier = Modifier.weight(1f), onClick = { controller.accept(ControllerEvent.Start) }) {
                    Text(text = "Start")
                }
                Button(modifier = Modifier.weight(1f), onClick = { controller.accept(ControllerEvent.Stop) }) {
                    Text(text = "Stop")
                }
            }
        }
    }
}
