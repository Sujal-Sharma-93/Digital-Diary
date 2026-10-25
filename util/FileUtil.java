package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.ArrayList;

import model.Entry;
import model.PersonalEntry;
import model.TravelEntry;
import model.WorkEntry;

public class FileUtil {

    // Writes one entry to a readable text file.
    public static void exportEntryToText(Entry entry, String filePath) throws IOException {
        FileWriter writer = null;

        try {
            writer = new FileWriter(filePath);
            StringBuffer buffer = new StringBuffer();
            buffer.append("Entry ID: ").append(entry.getEntryId()).append("\n");
            buffer.append("User ID: ").append(entry.getUserId()).append("\n");
            buffer.append("Entry Type: ").append(entry.getEntryType()).append("\n");
            buffer.append("Date: ").append(entry.getEntryDate()).append("\n");
            buffer.append("Title: ").append(entry.getTitle()).append("\n");
            buffer.append("Mood: ").append(entry.getMood()).append("\n");
            buffer.append("Content:\n");
            buffer.append(entry.getContent());
            writer.write(buffer.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    // Reads one entry back from a text file created by exportEntryToText.
    public static Entry importFromText(String filePath) throws IOException {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        int entryId = 0;
        int userId = 0;
        String entryType = "";
        String dateText = "";
        String title = "";
        String mood = "";
        StringBuffer content = new StringBuffer();
        boolean contentStarted = false;

        try {
            reader = new FileReader(filePath);
            bufferedReader = new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (contentStarted) {
                    content.append(line).append("\n");
                } else if (line.startsWith("Entry ID: ")) {
                    entryId = parseId(line.substring("Entry ID: ".length()).trim());
                } else if (line.startsWith("User ID: ")) {
                    userId = parseId(line.substring("User ID: ".length()).trim());
                } else if (line.startsWith("Entry Type: ")) {
                    entryType = line.substring("Entry Type: ".length()).trim();
                } else if (line.startsWith("Date: ")) {
                    dateText = line.substring("Date: ".length()).trim();
                } else if (line.startsWith("Title: ")) {
                    title = line.substring("Title: ".length()).trim();
                } else if (line.startsWith("Mood: ")) {
                    mood = line.substring("Mood: ".length()).trim();
                } else if (line.equals("Content:")) {
                    contentStarted = true;
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            } else if (reader != null) {
                reader.close();
            }
        }

        if (dateText.isEmpty() || title.isEmpty() || mood.isEmpty() || !contentStarted) {
            throw new IOException("The selected text file does not contain a valid entry.");
        }

        if (content.length() > 0) {
            content.setLength(content.length() - 1);
        }

        Date date;

        try {
            date = Date.valueOf(dateText);
        } catch (IllegalArgumentException e) {
            throw new IOException("The entry date in the text file is invalid.");
        }

        if (entryType.equalsIgnoreCase("Work")) {
            return new WorkEntry(entryId, userId, date, title, content.toString(), mood);
        } else if (entryType.equalsIgnoreCase("Travel")) {
            return new TravelEntry(entryId, userId, date, title, content.toString(), mood);
        } else {
            return new PersonalEntry(entryId, userId, date, title, content.toString(), mood);
        }
    }

    // Saves an ArrayList of entries to a binary backup file.
    public static void backupEntries(ArrayList<Entry> entries, String filePath) throws IOException {
        FileOutputStream fileStream = null;
        ObjectOutputStream objectStream = null;

        try {
            fileStream = new FileOutputStream(filePath);
            objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(entries);
        } finally {
            if (objectStream != null) {
                objectStream.close();
            } else if (fileStream != null) {
                fileStream.close();
            }
        }
    }

    // Converts id text from a file into a number.
    private static int parseId(String text) throws IOException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IOException("An id in the selected text file is invalid.");
        }
    }

    // Loads an ArrayList of entries from a binary backup file.
    @SuppressWarnings("unchecked")
    public static ArrayList<Entry> restoreEntries(String filePath) throws IOException {
        FileInputStream fileStream = null;
        ObjectInputStream objectStream = null;

        try {
            fileStream = new FileInputStream(filePath);
            objectStream = new ObjectInputStream(fileStream);
            Object object = objectStream.readObject();

            if (!(object instanceof ArrayList)) {
                throw new IOException("The selected file is not a valid diary backup.");
            }

            ArrayList<Entry> entries = (ArrayList<Entry>) object;
            return entries;
        } catch (ClassNotFoundException e) {
            throw new IOException("The backup file contains unknown data.");
        } finally {
            if (objectStream != null) {
                objectStream.close();
            } else if (fileStream != null) {
                fileStream.close();
            }
        }
    }
}
