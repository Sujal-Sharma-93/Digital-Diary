package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Entry;
import model.PersonalEntry;
import model.TravelEntry;
import model.WorkEntry;
import thread.AutoSaveThread;

public class EntryDialog extends JDialog {

    private int userId;
    private int entryId;
    private JComboBox<String> typeComboBox;
    private JTextField dateField;
    private JTextField titleField;
    private JComboBox<String> moodComboBox;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton cancelButton;
    private Entry savedEntry;
    private boolean saved;
    private AutoSaveThread autoSaveThread;

    // Builds the add or edit entry form.
    public EntryDialog(JFrame owner, int userId, Entry existingEntry) {
        super(owner, existingEntry == null ? "Add Entry" : "Edit Entry", true);
        this.userId = userId;
        this.entryId = existingEntry == null ? 0 : existingEntry.getEntryId();
        this.saved = false;

        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String[] entryTypes = {"Personal", "Work", "Travel"};
        String[] moods = {"Happy", "Sad", "Angry", "Calm", "Excited", "Stressed", "Neutral"};

        typeComboBox = new JComboBox<String>(entryTypes);
        dateField = new JTextField(12);
        titleField = new JTextField(20);
        moodComboBox = new JComboBox<String>(moods);
        contentArea = new JTextArea(10, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        formPanel.add(new JLabel("Entry Type:"));
        formPanel.add(typeComboBox);
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Mood:"));
        formPanel.add(moodComboBox);
        formPanel.add(new JLabel("Content:"));
        formPanel.add(contentScrollPane);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        JPanel saveButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButtonsPanel.add(saveButton);
        saveButtonsPanel.add(cancelButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JLabel("Draft auto-saves every 30 seconds."), BorderLayout.WEST);
        buttonPanel.add(saveButtonsPanel, BorderLayout.EAST);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (existingEntry == null) {
            dateField.setText(new Date(System.currentTimeMillis()).toString());
        } else {
            typeComboBox.setSelectedItem(existingEntry.getEntryType());
            dateField.setText(existingEntry.getEntryDate().toString());
            titleField.setText(existingEntry.getTitle());
            moodComboBox.setSelectedItem(existingEntry.getMood());
            contentArea.setText(existingEntry.getContent());
        }

        saveButton.addActionListener(new ActionListener() {
            // Reads the form and saves the entry.
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEntry();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            // Closes the form without saving.
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        KeyAdapter saveShortcut = new KeyAdapter() {
            // Saves the entry when Ctrl+S is pressed.
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveEntry();
                }
            }
        };

        typeComboBox.addKeyListener(saveShortcut);
        dateField.addKeyListener(saveShortcut);
        titleField.addKeyListener(saveShortcut);
        moodComboBox.addKeyListener(saveShortcut);
        contentArea.addKeyListener(saveShortcut);

        autoSaveThread = new AutoSaveThread(contentArea, userId);
        final Thread autoSaveRunner = new Thread(autoSaveThread);
        autoSaveRunner.setDaemon(true);

        addWindowListener(new WindowAdapter() {
            // Starts auto-saving after the dialog opens.
            @Override
            public void windowOpened(WindowEvent e) {
                autoSaveRunner.start();
            }

            // Stops auto-saving after the dialog closes.
            @Override
            public void windowClosed(WindowEvent e) {
                autoSaveThread.stopAutoSave();
            }
        });

        setSize(600, 480);
        setLocationRelativeTo(owner);
    }

    // Checks the form values and creates the correct Entry subclass.
    private void saveEntry() {
        String type = (String) typeComboBox.getSelectedItem();
        String dateText = dateField.getText().trim();
        String title = titleField.getText().trim();
        String mood = (String) moodComboBox.getSelectedItem();
        String content = contentArea.getText().trim();

        if (dateText.isEmpty() || title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Date, title and content cannot be empty.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (title.length() > 100) {
            JOptionPane.showMessageDialog(this,
                    "The title cannot be longer than 100 characters.",
                    "Title Too Long",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (content.length() > 60000) {
            JOptionPane.showMessageDialog(this,
                    "The content cannot be longer than 60000 characters.",
                    "Content Too Long",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date entryDate;

        try {
            entryDate = Date.valueOf(dateText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the date in yyyy-MM-dd format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (type.equals("Work")) {
            savedEntry = new WorkEntry(entryId, userId, entryDate, title, content, mood);
        } else if (type.equals("Travel")) {
            savedEntry = new TravelEntry(entryId, userId, entryDate, title, content, mood);
        } else {
            savedEntry = new PersonalEntry(entryId, userId, entryDate, title, content, mood);
        }

        saved = true;
        dispose();
    }

    // Returns true when the user saved the entry.
    public boolean isSaved() {
        return saved;
    }

    // Returns the entry that the user saved.
    public Entry getSavedEntry() {
        return savedEntry;
    }
}
