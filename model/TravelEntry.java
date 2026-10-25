package model;

import java.sql.Date;

public class TravelEntry extends Entry {

    private static final long serialVersionUID = 1L;

    // Creates an empty travel entry object.
    public TravelEntry() {
        super();
    }

    // Creates a travel entry object with all its details.
    public TravelEntry(int entryId, int userId, Date entryDate, String title, String content, String mood) {
        super(entryId, userId, entryDate, title, content, mood);
    }

    // Returns the type name for a travel entry.
    @Override
    public String getEntryType() {
        return "Travel";
    }
}
