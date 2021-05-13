package vivid.money.elmslie.samples.notes.model;

import java.util.Collections;
import java.util.List;

public class State {

    public final List<String> notes;

    public State() {
        this(Collections.emptyList());
    }

    public State(List<String> notes) {
        this.notes = Collections.unmodifiableList(notes);
    }

    public State copy(List<String> notes) {
        return new State(notes);
    }
}
