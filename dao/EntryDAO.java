package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import exception.EmptyEntryException;
import model.Entry;
import model.PersonalEntry;
import model.TravelEntry;
import model.WorkEntry;
import util.DBConnection;

public class EntryDAO implements DiaryOperations {

    // Adds a new diary entry and stores its generated id.
    @Override
    public void addEntry(Entry entry) throws SQLException, EmptyEntryException {
        validateEntry(entry);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "INSERT INTO entries "
                    + "(user_id, entry_type, entry_date, title, content, mood) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entry.getUserId());
            statement.setString(2, entry.getEntryType());
            statement.setDate(3, entry.getEntryDate());
            statement.setString(4, entry.getTitle());
            statement.setString(5, entry.getContent());
            statement.setString(6, entry.getMood());
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                entry.setEntryId(resultSet.getInt(1));
            }
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    // Updates the title, type, date, content and mood of an existing entry.
    @Override
    public void updateEntry(Entry entry) throws SQLException, EmptyEntryException {
        validateEntry(entry);

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "UPDATE entries SET entry_type = ?, entry_date = ?, "
                    + "title = ?, content = ?, mood = ? "
                    + "WHERE entry_id = ? AND user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, entry.getEntryType());
            statement.setDate(2, entry.getEntryDate());
            statement.setString(3, entry.getTitle());
            statement.setString(4, entry.getContent());
            statement.setString(5, entry.getMood());
            statement.setInt(6, entry.getEntryId());
            statement.setInt(7, entry.getUserId());
            statement.executeUpdate();
        } finally {
            closeResources(statement, connection);
        }
    }

    // Deletes one diary entry using its id.
    @Override
    public void deleteEntry(int entryId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "DELETE FROM entries WHERE entry_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, entryId);
            statement.executeUpdate();
        } finally {
            closeResources(statement, connection);
        }
    }

    // Returns all entries that belong to one user, newest dates first.
    @Override
    public ArrayList<Entry> getEntriesByUser(int userId) throws SQLException {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT * FROM entries WHERE user_id = ? "
                    + "ORDER BY entry_date DESC, entry_id DESC";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                entries.add(createEntry(resultSet));
            }
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return entries;
    }

    // Searches a user's entry titles and contents for a keyword.
    @Override
    public ArrayList<Entry> searchByKeyword(int userId, String keyword) throws SQLException {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT * FROM entries WHERE user_id = ? "
                    + "AND (title LIKE ? OR content LIKE ?) "
                    + "ORDER BY entry_date DESC, entry_id DESC";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                entries.add(createEntry(resultSet));
            }
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return entries;
    }

    // Searches for all of a user's entries written on one date.
    @Override
    public ArrayList<Entry> searchByDate(int userId, Date date) throws SQLException {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT * FROM entries WHERE user_id = ? AND entry_date = ? "
                    + "ORDER BY entry_id DESC";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setDate(2, date);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                entries.add(createEntry(resultSet));
            }
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return entries;
    }

    // Creates the correct Entry subclass from one database row.
    private Entry createEntry(ResultSet resultSet) throws SQLException {
        int entryId = resultSet.getInt("entry_id");
        int userId = resultSet.getInt("user_id");
        Date entryDate = resultSet.getDate("entry_date");
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        String mood = resultSet.getString("mood");
        String entryType = resultSet.getString("entry_type");

        Entry entry;

        if (entryType.equalsIgnoreCase("Work")) {
            entry = new WorkEntry(entryId, userId, entryDate, title, content, mood);
        } else if (entryType.equalsIgnoreCase("Travel")) {
            entry = new TravelEntry(entryId, userId, entryDate, title, content, mood);
        } else {
            entry = new PersonalEntry(entryId, userId, entryDate, title, content, mood);
        }

        return entry;
    }

    // Checks that the required entry details are present.
    private void validateEntry(Entry entry) throws EmptyEntryException {
        if (entry == null) {
            throw new EmptyEntryException("Please create an entry first.");
        }

        if (entry.getEntryDate() == null) {
            throw new EmptyEntryException("Please enter a date.");
        }

        if (entry.getTitle() == null || entry.getTitle().trim().isEmpty()) {
            throw new EmptyEntryException("Please enter a title.");
        }

        if (entry.getContent() == null || entry.getContent().trim().isEmpty()) {
            throw new EmptyEntryException("Please enter some content.");
        }

        if (entry.getMood() == null || entry.getMood().trim().isEmpty()) {
            throw new EmptyEntryException("Please select a mood.");
        }
    }

    // Closes the statement and connection safely.
    private void closeResources(PreparedStatement statement, Connection connection) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Could not close the prepared statement.");
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Could not close the database connection.");
            }
        }
    }

    // Closes the result set, statement and connection safely.
    private void closeResources(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Could not close the result set.");
            }
        }

        closeResources(statement, connection);
    }
}
