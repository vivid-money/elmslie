package vivid.money.elmslie.samples.notes;

import java.util.List;

import vivid.money.elmslie.core.store.Store;
import vivid.money.elmslie.samples.notes.model.Effect;
import vivid.money.elmslie.samples.notes.model.Event;
import vivid.money.elmslie.samples.notes.model.State;
import vivid.money.elmslie.samples.notes.store.NotesStoreFactory;

public class Notes {

    private final Store<Event, Effect, State> store = NotesStoreFactory.create();

    public void add(String note) {
        store.accept(new Event.AddNote(note));
    }

    public void clear() {
        store.accept(new Event.Clear());
    }

    public List<String> getAll() {
        return store.getCurrentState().notes;
    }
}
