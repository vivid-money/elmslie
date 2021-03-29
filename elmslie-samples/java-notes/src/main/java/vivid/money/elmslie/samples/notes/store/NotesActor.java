package vivid.money.elmslie.samples.notes.store;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Observable;
import vivid.money.elmslie.core.store.Actor;
import vivid.money.elmslie.samples.notes.model.Command;
import vivid.money.elmslie.samples.notes.model.Event;

public abstract class NotesActor implements Actor<Command, Event> {

    @NotNull
    @Override
    public Observable<Event> execute(@NotNull Command command) {
        return null;
    }
}
