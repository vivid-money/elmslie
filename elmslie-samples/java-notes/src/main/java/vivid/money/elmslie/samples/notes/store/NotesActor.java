package vivid.money.elmslie.samples.notes.store;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import vivid.money.elmslie.core.disposable.Disposable;
import vivid.money.elmslie.core.store.DefaultActor;
import vivid.money.elmslie.samples.notes.model.Command;
import vivid.money.elmslie.samples.notes.model.Event;

public abstract class NotesActor implements DefaultActor<Command, Event> {
    @NotNull
    @Override
    public Disposable execute(
            @NotNull Command command,
            @NotNull Function1<? super Event, Unit> onEvent,
            @NotNull Function1<? super Throwable, Unit> onError
    ) {
        return () -> {
        };
    }
}
