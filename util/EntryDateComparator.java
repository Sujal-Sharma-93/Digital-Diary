package util;

import java.util.Comparator;

import model.Entry;

public class EntryDateComparator implements Comparator<Entry> {

    // Compares two entries so the newest date appears first.
    @Override
    public int compare(Entry first, Entry second) {
        int dateResult = second.getEntryDate().compareTo(first.getEntryDate());

        if (dateResult != 0) {
            return dateResult;
        }

        return Integer.compare(second.getEntryId(), first.getEntryId());
    }
}
