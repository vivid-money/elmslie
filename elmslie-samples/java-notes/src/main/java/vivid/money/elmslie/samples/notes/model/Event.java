package vivid.money.elmslie.samples.notes.model;

public interface Event {

    class AddNote implements Event {

        public final String note;

        public AddNote(String note) {
            this.note = note;
        }
    }

    class Clear implements Event {
    }
}
