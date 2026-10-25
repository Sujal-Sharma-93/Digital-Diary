package model;

import java.sql.Date;

public class WorkEntry extends Entry {

    private static final long serialVersionUID = 1L;

    // Creates an empty work entry object.
    public WorkEntry() {
        super();
    }

    // Creates a work entry object with all its details.
    public WorkEntry(int entryId, int userId, Date entryDate, String title, String content, String mood) {
        super(entryId, userId, entryDate, title, content, mood);
    }

    // Returns the type name for a work entry.
    @Override
    public String getEntryType() {
        return "Work";
    }
}
