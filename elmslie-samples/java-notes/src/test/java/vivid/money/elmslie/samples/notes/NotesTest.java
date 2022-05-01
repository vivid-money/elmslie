package vivid.money.elmslie.samples.notes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;

import kotlin.jvm.JvmField;
import vivid.money.elmslie.test.background.executor.MockBackgroundExecutorExtension;

public class NotesTest {

    @JvmField
    @RegisterExtension
    public Extension extension = new MockBackgroundExecutorExtension();

    @Test
    public void notesAreEmptyInitially() {
        Notes notes = new Notes();
        assertEquals(Collections.emptyList(), notes.getAll());
    }

    @Test
    public void addingNoteWorks() {
        Notes notes = new Notes();
        notes.add("note");
        assertEquals(Collections.singletonList("note"), notes.getAll());
    }

    @Test
    public void clearingNotesWorks() {
        Notes notes = new Notes();
        notes.add("note");
        notes.clear();
        assertEquals(Collections.emptyList(), notes.getAll());
    }
}
