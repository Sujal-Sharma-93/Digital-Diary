package model;

import java.sql.Date;

public class PersonalEntry extends Entry {

    private static final long serialVersionUID = 1L;

    // Creates an empty personal entry object.
    public PersonalEntry() {
        super();
    }

    // Creates a personal entry object with all its details.
    public PersonalEntry(int entryId, int userId, Date entryDate, String title, String content, String mood) {
        super(entryId, userId, entryDate, title, content, mood);
    }

    // Returns the type name for a personal entry.
    @Override
    public String getEntryType() {
        return "Personal";
    }
}
