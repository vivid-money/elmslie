package vivid.money.elmslie.samples.notes.store;

import vivid.money.elmslie.core.store.ElmStore;
import vivid.money.elmslie.core.store.NoOpActor;
import vivid.money.elmslie.core.store.Store;
import vivid.money.elmslie.samples.notes.model.Effect;
import vivid.money.elmslie.samples.notes.model.Event;
import vivid.money.elmslie.samples.notes.model.State;

public class NotesStoreFactory {

    public static Store<Event, Effect, State> create() {
        return new ElmStore<>(
                new State(),
                new NotesReducer(),
                new NoOpActor<>()
        );
    }
}
