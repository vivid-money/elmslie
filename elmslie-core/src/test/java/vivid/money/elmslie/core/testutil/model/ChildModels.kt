package vivid.money.elmslie.core.testutil.model

enum class ChildEvent { First, Second, Third }
object ChildEffect
object ChildCommand
data class ChildState(val value: Int = 0)
