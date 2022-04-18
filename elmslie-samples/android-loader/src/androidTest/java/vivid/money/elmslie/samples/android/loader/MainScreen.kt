package vivid.money.elmslie.samples.android.loader

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KSnackbar
import io.github.kakaocup.kakao.text.KTextView

object MainScreen : KScreen<MainScreen>() {

    override val layoutId: Int = R.layout.activity_main
    override val viewClass: Class<*> = MainActivity::class.java

    val reload = KButton { withId(R.id.reload) }

    val currentValue = KTextView { withId(R.id.currentValue) }

    val snackbar = KSnackbar()
}