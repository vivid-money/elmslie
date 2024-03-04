package money.vivid.elmslie.core.store

/** Represents result of reduce function */
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
        state = state,
        effects = listOfNotNull(effect),
        commands = listOfNotNull(command),
    )

    constructor(
        state: State,
        commands: List<Command>,
    ) : this(
        state = state,
        effects = emptyList(),
        commands = commands,
    )

    constructor(
        state: State
    ) : this(
        state = state,
        effects = emptyList(),
        commands = emptyList(),
    )
}
