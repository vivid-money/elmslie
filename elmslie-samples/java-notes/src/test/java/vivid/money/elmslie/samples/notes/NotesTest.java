package vivid.money.elmslie.samples.notes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;

import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.test.TestBuildersJvmKt;
import kotlinx.coroutines.test.TestBuildersKt;
import vivid.money.elmslie.test.background.executor.MockBackgroundExecutorExtension;
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension;

public class NotesTest {

    @JvmField
    @RegisterExtension
    public Extension extension = new MockBackgroundExecutorExtension();

    @JvmField
    @RegisterExtension
    public TestDispatcherExtension testDispatcherExtension = new TestDispatcherExtension();

    @Test
    public void notesAreEmptyInitially() {
        Notes notes = new Notes();
        assertEquals(Collections.emptyList(), notes.getAll());
    }

//    @Test Ignore
//    public void addingNoteWorks() {
//        Notes notes = new Notes();
//        notes.add("note");
//        assertEquals(Collections.singletonList("note"), notes.getAll());
//    }

    @Test
    public void clearingNotesWorks() {
        Notes notes = new Notes();
        notes.add("note");
        notes.clear();
        assertEquals(Collections.emptyList(), notes.getAll());
    }
}
