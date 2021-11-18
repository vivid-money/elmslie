package vivid.money.elmslie.core.testutil.model

sealed class ParentEvent {
    object Plain : ParentEvent()
    data class ChildUpdated(val state: ChildState) : ParentEvent()
}
data class ParentState(val value: Int = 0, val childValue: Int = 0)
object ParentEffect
object ParentCommand
