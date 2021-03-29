package vivid.money.elmslie.samples.notes.store;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vivid.money.elmslie.core.store.Result;
import vivid.money.elmslie.core.store.StateReducer;
import vivid.money.elmslie.samples.notes.model.Command;
import vivid.money.elmslie.samples.notes.model.Effect;
import vivid.money.elmslie.samples.notes.model.Event;
import vivid.money.elmslie.samples.notes.model.State;

public class NotesReducer implements StateReducer<Event, State, Effect, Command> {

    @NotNull
    @Override
    public Result<State, Effect, Command> reduce(@NotNull Event event, @NotNull State state) {
        if (event instanceof Event.AddNote) {
            List<String> notes = new ArrayList<>(state.notes);
            notes.add(((Event.AddNote) event).note);
            return new Result<>(state.copy(notes));
        } else if (event instanceof Event.Clear) {
            return new Result<>(state.copy(Collections.emptyList()));
        } else {
            throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        }
    }
}

