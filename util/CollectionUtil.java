package util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import model.Entry;

public class CollectionUtil {

    // Sorts an ArrayList of entries with the newest date first.
    public static void sortEntriesNewestFirst(ArrayList<Entry> entries) {
        Collections.sort(entries, new EntryDateComparator());
    }

    // Counts how many entries use each mood.
    public static HashMap<String, Integer> countMoods(ArrayList<Entry> entries) {
        HashMap<String, Integer> moodCounts = new HashMap<String, Integer>();

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            String mood = entry.getMood();

            if (mood == null || mood.trim().isEmpty()) {
                mood = "Unknown";
            }

            if (moodCounts.containsKey(mood)) {
                int oldCount = moodCounts.get(mood);
                moodCounts.put(mood, oldCount + 1);
            } else {
                moodCounts.put(mood, 1);
            }
        }

        return moodCounts;
    }

    // Groups entries by date inside a TreeMap so dates stay sorted.
    public static TreeMap<Date, ArrayList<Entry>> groupEntriesByDate(ArrayList<Entry> entries) {
        TreeMap<Date, ArrayList<Entry>> entriesByDate = new TreeMap<Date, ArrayList<Entry>>();

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            Date date = entry.getEntryDate();
            ArrayList<Entry> entriesOnDate = entriesByDate.get(date);

            if (entriesOnDate == null) {
                entriesOnDate = new ArrayList<Entry>();
                entriesByDate.put(date, entriesOnDate);
            }

            entriesOnDate.add(entry);
        }

        return entriesByDate;
    }
}
