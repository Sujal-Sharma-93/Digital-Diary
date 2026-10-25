package model;

import java.io.Serializable;
import java.sql.Date;

public abstract class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private int entryId;
    private int userId;
    private Date entryDate;
    private String title;
    private String content;
    private String mood;

    // Creates an empty entry object.
    protected Entry() {
    }

    // Creates an entry object with all its details.
    public Entry(int entryId, int userId, Date entryDate, String title, String content, String mood) {
        this.entryId = entryId;
        this.userId = userId;
        this.entryDate = entryDate;
        this.title = title;
        this.content = content;
        this.mood = mood;
    }

    // Returns the type of this entry.
    public abstract String getEntryType();

    // Returns the entry id.
    public int getEntryId() {
        return entryId;
    }

    // Sets the entry id.
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    // Returns the id of the user who owns this entry.
    public int getUserId() {
        return userId;
    }

    // Sets the id of the user who owns this entry.
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Returns the date of this entry.
    public Date getEntryDate() {
        return entryDate;
    }

    // Sets the date of this entry.
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    // Returns the title of this entry.
    public String getTitle() {
        return title;
    }

    // Sets the title of this entry.
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the content of this entry.
    public String getContent() {
        return content;
    }

    // Sets the content of this entry.
    public void setContent(String content) {
        this.content = content;
    }

    // Returns the mood of this entry.
    public String getMood() {
        return mood;
    }

    // Sets the mood of this entry.
    public void setMood(String mood) {
        this.mood = mood;
    }
}
