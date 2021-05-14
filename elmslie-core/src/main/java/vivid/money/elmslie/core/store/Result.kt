package vivid.money.elmslie.core.store

/**
 * Represents result of reduce function
 */
data class Result<State : Any, Effect : Any, Command : Any>(
    val state: State,
    val effects: List<Effect>,
    val commands: List<Command>,
) {

    constructor(
        state: State,
        effect: Effect? = null,
        command: Command? = null,
    ) : this(
        state,
        effect?.let(::listOf) ?: emptyList(),
        command?.let(::listOf) ?: emptyList()
    )

    constructor(
        state: State,
        commands: List<Command>,
    ) : this(
        state,
        emptyList(),
        commands
    )

    constructor(state: State) : this(state, emptyList(), emptyList())
}