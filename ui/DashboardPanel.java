package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.EntryDAO;
import exception.EmptyEntryException;
import model.Entry;
import model.User;
import util.CollectionUtil;
import util.FileUtil;

public class DashboardPanel extends JPanel {

    private MainFrame mainFrame;
    private User user;
    private EntryDAO entryDAO;
    private ArrayList<Entry> entries;
    private ArrayList<Entry> searchResults;
    private JTable entryTable;
    private DefaultTableModel entryTableModel;
    private JTable searchTable;
    private DefaultTableModel searchTableModel;
    private JTable moodTable;
    private DefaultTableModel moodTableModel;
    private JTextField keywordField;
    private JTextField dateField;
    private JLabel searchStatusLabel;
    private JTextArea dateSummaryArea;

    // Builds the dashboard with entries, search and mood statistics tabs.
    public DashboardPanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;
        this.entryDAO = new EntryDAO();
        this.entries = new ArrayList<Entry>();
        this.searchResults = new ArrayList<Entry>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Entries", createEntriesTab());
        tabbedPane.addTab("Search", createSearchTab());
        tabbedPane.addTab("Mood Statistics", createStatisticsTab());

        add(tabbedPane, BorderLayout.CENTER);

        loadEntries();
    }

    // Creates the welcome area and logout button.
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel welcomeLabel = new JLabel("Logged in as: " + user.getUsername());
        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(new ActionListener() {
            // Logs the user out of the dashboard.
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.logout();
            }
        });

        panel.add(welcomeLabel);
        panel.add(logoutButton);
        return panel;
    }

    // Creates the tab that shows all entries and the CRUD buttons.
    private JPanel createEntriesTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton exportButton = new JButton("Export");
        JButton importButton = new JButton("Import");
        JButton backupButton = new JButton("Backup All");
        JButton restoreButton = new JButton("Restore");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(backupButton);
        buttonPanel.add(restoreButton);

        entryTableModel = new DefaultTableModel(
                new String[]{"ID", "Date", "Type", "Title", "Mood"}, 0) {
            // Keeps the table cells read-only.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        entryTable = new JTable(entryTableModel);
        entryTable.setRowHeight(24);

        entryTable.addMouseListener(new MouseAdapter() {
            // Opens the selected entry when its row is double-clicked.
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEntry(entryTable, entries, false);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            // Opens an empty entry form.
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntry();
            }
        });

        editButton.addActionListener(new ActionListener() {
            // Opens the selected entry in the form.
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedEntry(entryTable, entries, false);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            // Deletes the selected entry.
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEntry();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            // Exports the selected entry to a text file.
            @Override
            public void actionPerformed(ActionEvent e) {
                exportSelectedEntry();
            }
        });

        importButton.addActionListener(new ActionListener() {
            // Imports one entry from a text file.
            @Override
            public void actionPerformed(ActionEvent e) {
                importEntry();
            }
        });

        backupButton.addActionListener(new ActionListener() {
            // Saves all entries to a binary backup file.
            @Override
            public void actionPerformed(ActionEvent e) {
                backupAllEntries();
            }
        });

        restoreButton.addActionListener(new ActionListener() {
            // Adds entries from a binary backup file.
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreEntries();
            }
        });

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(entryTable), BorderLayout.CENTER);
        return panel;
    }

    // Creates the tab that searches entries by keyword or date.
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        keywordField = new JTextField(15);
        dateField = new JTextField(10);
        dateField.setText(new Date(System.currentTimeMillis()).toString());

        JButton keywordButton = new JButton("Search Keyword");
        JButton dateButton = new JButton("Search Date");
        searchStatusLabel = new JLabel("Enter a keyword or date to search.");

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        formPanel.add(new JLabel("Keyword:"));
        formPanel.add(keywordField);
        formPanel.add(keywordButton);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateField);
        formPanel.add(dateButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchStatusLabel, BorderLayout.SOUTH);

        searchTableModel = new DefaultTableModel(
                new String[]{"ID", "Date", "Type", "Title", "Mood"}, 0) {
            // Keeps the search table cells read-only.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        searchTable = new JTable(searchTableModel);
        searchTable.setRowHeight(24);

        searchTable.addMouseListener(new MouseAdapter() {
            // Opens a searched entry when its row is double-clicked.
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEntry(searchTable, searchResults, true);
                }
            }
        });

        keywordButton.addActionListener(new ActionListener() {
            // Searches the user's entries by keyword.
            @Override
            public void actionPerformed(ActionEvent e) {
                searchByKeyword();
            }
        });

        dateButton.addActionListener(new ActionListener() {
            // Searches the user's entries by date.
            @Override
            public void actionPerformed(ActionEvent e) {
                searchByDate();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        return panel;
    }

    // Creates the tab that shows mood counts and entries grouped by date.
    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        moodTableModel = new DefaultTableModel(
                new String[]{"Mood", "Number of Entries"}, 0) {
            // Keeps the mood table cells read-only.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        moodTable = new JTable(moodTableModel);
        moodTable.setRowHeight(24);

        JPanel moodPanel = new JPanel(new BorderLayout(5, 5));
        moodPanel.add(new JLabel("Mood counts from a HashMap"), BorderLayout.NORTH);
        moodPanel.add(new JScrollPane(moodTable), BorderLayout.CENTER);

        dateSummaryArea = new JTextArea();
        dateSummaryArea.setEditable(false);
        dateSummaryArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));

        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        datePanel.add(new JLabel("Entries grouped in a TreeMap (sorted by date)"), BorderLayout.NORTH);
        datePanel.add(new JScrollPane(dateSummaryArea), BorderLayout.CENTER);

        panel.add(moodPanel);
        panel.add(datePanel);
        return panel;
    }

    // Loads the user's entries from the database and refreshes the screen.
    private void loadEntries() {
        try {
            entries = entryDAO.getEntriesByUser(user.getUserId());
            CollectionUtil.sortEntriesNewestFirst(entries);
            fillTable(entryTableModel, entries);
            refreshStatistics();
        } catch (SQLException e) {
            showDatabaseError("load entries", e);
        }
    }

    // Fills a table model with a list of entries.
    private void fillTable(DefaultTableModel model, ArrayList<Entry> entryList) {
        model.setRowCount(0);

        for (int i = 0; i < entryList.size(); i++) {
            Entry entry = entryList.get(i);
            model.addRow(new Object[]{
                entry.getEntryId(),
                entry.getEntryDate().toString(),
                entry.getEntryType(),
                entry.getTitle(),
                entry.getMood()
            });
        }
    }

    // Rebuilds the mood counts and the date-sorted TreeMap summary.
    private void refreshStatistics() {
        moodTableModel.setRowCount(0);

        HashMap<String, Integer> moodCounts = CollectionUtil.countMoods(entries);
        ArrayList<String> moodNames = new ArrayList<String>(moodCounts.keySet());
        Collections.sort(moodNames);

        for (int i = 0; i < moodNames.size(); i++) {
            String mood = moodNames.get(i);
            moodTableModel.addRow(new Object[]{mood, moodCounts.get(mood)});
        }

        TreeMap<Date, ArrayList<Entry>> entriesByDate = CollectionUtil.groupEntriesByDate(entries);
        StringBuffer summary = new StringBuffer();

        for (Date date : entriesByDate.keySet()) {
            ArrayList<Entry> entriesOnDate = entriesByDate.get(date);
            summary.append(date).append(" (").append(entriesOnDate.size()).append(" entries)\n");

            for (int i = 0; i < entriesOnDate.size(); i++) {
                Entry entry = entriesOnDate.get(i);
                summary.append("   - ")
                        .append(entry.getEntryType())
                        .append(": ")
                        .append(entry.getTitle())
                        .append(" [")
                        .append(entry.getMood())
                        .append("]\n");
            }

            summary.append("\n");
        }

        dateSummaryArea.setText(summary.toString());
        dateSummaryArea.setCaretPosition(0);
    }

    // Opens an empty EntryDialog and adds the new entry.
    private void addEntry() {
        EntryDialog dialog = new EntryDialog(mainFrame, user.getUserId(), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                entryDAO.addEntry(dialog.getSavedEntry());
                loadEntries();
                JOptionPane.showMessageDialog(this, "Entry added.");
            } catch (EmptyEntryException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Empty Entry", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException e) {
                showDatabaseError("add the entry", e);
            }
        }
    }

    // Opens the selected entry in an EntryDialog and updates it.
    private void editSelectedEntry(JTable table, ArrayList<Entry> entryList, boolean fromSearch) {
        Entry selectedEntry = getSelectedEntry(table, entryList);

        if (selectedEntry == null) {
            return;
        }

        EntryDialog dialog = new EntryDialog(mainFrame, user.getUserId(), selectedEntry);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                entryDAO.updateEntry(dialog.getSavedEntry());
                loadEntries();

                if (fromSearch) {
                    searchResults.clear();
                    searchTableModel.setRowCount(0);
                    searchStatusLabel.setText("Entry updated. Run the search again.");
                }

                JOptionPane.showMessageDialog(this, "Entry updated.");
            } catch (EmptyEntryException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Empty Entry", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException e) {
                showDatabaseError("update the entry", e);
            }
        }
    }

    // Deletes the entry selected in the main table.
    private void deleteSelectedEntry() {
        Entry selectedEntry = getSelectedEntry(entryTable, entries);

        if (selectedEntry == null) {
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete the entry \"" + selectedEntry.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            entryDAO.deleteEntry(selectedEntry.getEntryId());
            loadEntries();
            JOptionPane.showMessageDialog(this, "Entry deleted.");
        } catch (SQLException e) {
            showDatabaseError("delete the entry", e);
        }
    }

    // Exports the selected entry to a text file chosen by the user.
    private void exportSelectedEntry() {
        Entry selectedEntry = getSelectedEntry(entryTable, entries);

        if (selectedEntry == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("entry_" + selectedEntry.getEntryId() + ".txt"));
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = withExtension(chooser.getSelectedFile(), ".txt");

            try {
                FileUtil.exportEntryToText(selectedEntry, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Entry exported to " + file.getName() + ".");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not export the entry.\n" + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Imports one entry from a text file into the current user's diary.
    private void importEntry() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Entry importedEntry = FileUtil.importFromText(
                        chooser.getSelectedFile().getAbsolutePath());
                importedEntry.setEntryId(0);
                importedEntry.setUserId(user.getUserId());
                entryDAO.addEntry(importedEntry);
                loadEntries();
                JOptionPane.showMessageDialog(this, "Entry imported.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not import the entry.\n" + e.getMessage(),
                        "Import Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (EmptyEntryException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Empty Entry", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException e) {
                showDatabaseError("import the entry", e);
            }
        }
    }

    // Saves all loaded entries to a binary backup file.
    private void backupAllEntries() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("diary_backup.dat"));
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = withExtension(chooser.getSelectedFile(), ".dat");

            try {
                FileUtil.backupEntries(entries, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Backup saved to " + file.getName() + ".");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not create the backup.\n" + e.getMessage(),
                        "Backup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Adds every entry from a selected binary backup file.
    private void restoreEntries() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                ArrayList<Entry> restoredEntries = FileUtil.restoreEntries(
                        chooser.getSelectedFile().getAbsolutePath());
                int addedCount = 0;

                for (int i = 0; i < restoredEntries.size(); i++) {
                    Entry entry = restoredEntries.get(i);
                    entry.setEntryId(0);
                    entry.setUserId(user.getUserId());

                    try {
                        entryDAO.addEntry(entry);
                        addedCount++;
                    } catch (EmptyEntryException e) {
                        System.out.println("Skipped an invalid backup entry: " + e.getMessage());
                    }
                }

                loadEntries();
                JOptionPane.showMessageDialog(this, addedCount + " entries restored.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Could not restore the backup.\n" + e.getMessage(),
                        "Restore Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                showDatabaseError("restore the backup", e);
            }
        }
    }

    // Searches entries with the keyword field.
    private void searchByKeyword() {
        String keyword = keywordField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a keyword.",
                    "Missing Keyword",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            searchResults = entryDAO.searchByKeyword(user.getUserId(), keyword);
            fillTable(searchTableModel, searchResults);
            searchStatusLabel.setText(searchResults.size() + " entries found.");
        } catch (SQLException e) {
            showDatabaseError("search by keyword", e);
        }
    }

    // Searches entries with the date field.
    private void searchByDate() {
        String dateText = dateField.getText().trim();
        Date date;

        try {
            date = Date.valueOf(dateText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the date in yyyy-MM-dd format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            searchResults = entryDAO.searchByDate(user.getUserId(), date);
            fillTable(searchTableModel, searchResults);
            searchStatusLabel.setText(searchResults.size() + " entries found.");
        } catch (SQLException e) {
            showDatabaseError("search by date", e);
        }
    }

    // Returns the selected entry, or shows a message when none is selected.
    private Entry getSelectedEntry(JTable table, ArrayList<Entry> entryList) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0 || selectedRow >= entryList.size()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an entry first.",
                    "No Entry Selected",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return entryList.get(selectedRow);
    }

    // Adds the expected file extension when the user did not type one.
    private File withExtension(File file, String extension) {
        if (file.getName().toLowerCase().endsWith(extension)) {
            return file;
        }

        return new File(file.getAbsolutePath() + extension);
    }

    // Shows a readable database error message.
    private void showDatabaseError(String action, SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Could not " + action + ".\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
