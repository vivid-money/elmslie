package vivid.money.elmslie.core.switcher

import io.reactivex.Observable

@Deprecated("Please use instance methods", ReplaceWith("switcher.observable(delayMillis, action)"))
fun <Event : Any> switchOn(
    switcher: Switcher,
    delayMillis: Long = 0,
    action: () -> Observable<Event>
) = switcher.observable(delayMillis, action)
