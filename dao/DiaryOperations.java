package dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import exception.EmptyEntryException;
import model.Entry;

public interface DiaryOperations {

    // Adds a new diary entry to the database.
    void addEntry(Entry entry) throws SQLException, EmptyEntryException;

    // Updates an existing diary entry in the database.
    void updateEntry(Entry entry) throws SQLException, EmptyEntryException;

    // Deletes a diary entry using its entry id.
    void deleteEntry(int entryId) throws SQLException;

    // Returns all entries that belong to a user.
    ArrayList<Entry> getEntriesByUser(int userId) throws SQLException;

    // Returns a user's entries that contain the given keyword.
    ArrayList<Entry> searchByKeyword(int userId, String keyword) throws SQLException;

    // Returns a user's entries that match the given date.
    ArrayList<Entry> searchByDate(int userId, Date date) throws SQLException;
}
