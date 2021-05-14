package vivid.money.elmslie.samples.notes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;

import io.reactivex.rxjava3.schedulers.TestScheduler;
import vivid.money.elmslie.test.TestSchedulerExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotesTest {

    private static final TestScheduler scheduler = new TestScheduler();

    @RegisterExtension
    static final TestSchedulerExtension extension = new TestSchedulerExtension(scheduler);

    @Test
    public void notesAreEmptyInitially() {
        Notes notes = new Notes();
        assertEquals(Collections.emptyList(), notes.getAll());
    }

    @Test
    public void addingNoteWorks() {
        Notes notes = new Notes();
        notes.add("note");
        scheduler.triggerActions();
        assertEquals(Collections.singletonList("note"), notes.getAll());
    }

    @Test
    public void clearingNotesWorks() {
        Notes notes = new Notes();
        notes.add("note");
        notes.clear();
        scheduler.triggerActions();
        assertEquals(Collections.emptyList(), notes.getAll());
    }
}
