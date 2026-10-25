package thread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class AutoSaveThread implements Runnable {

    private JTextArea contentArea;
    private int userId;
    private volatile boolean running;

    // Stores the text area and user id for draft saving.
    public AutoSaveThread(JTextArea contentArea, int userId) {
        this.contentArea = contentArea;
        this.userId = userId;
        this.running = true;
    }

    // Waits thirty seconds at a time and then saves the draft.
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                running = false;
                return;
            }

            if (running) {
                saveDraft();
            }
        }
    }

    // Stops the auto-save thread safely.
    public void stopAutoSave() {
        running = false;
    }

    // Reads the current text safely and saves it to the drafts folder.
    private synchronized void saveDraft() {
        final String[] draftHolder = new String[1];

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                // Reads the text area on the Swing event thread.
                @Override
                public void run() {
                    draftHolder[0] = contentArea.getText().trim();
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (InvocationTargetException e) {
            System.out.println("Could not read the draft text.");
            return;
        }

        String draftText = draftHolder[0];

        if (draftText.isEmpty()) {
            return;
        }

        File draftFolder = new File("drafts");

        if (!draftFolder.exists()) {
            draftFolder.mkdirs();
        }

        File draftFile = new File(draftFolder, "draft_user_" + userId + ".txt");
        FileWriter writer = null;

        try {
            writer = new FileWriter(draftFile);
            writer.write(draftText);
        } catch (IOException e) {
            System.out.println("Could not save the draft: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Could not close the draft file.");
                }
            }
        }
    }
}
