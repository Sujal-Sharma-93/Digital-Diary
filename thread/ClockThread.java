package thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ClockThread extends Thread {

    private JLabel clockLabel;
    private volatile boolean running;

    // Stores the label and prepares the clock thread.
    public ClockThread(JLabel clockLabel) {
        this.clockLabel = clockLabel;
        this.running = true;
    }

    // Updates the date and time label once every second.
    @Override
    public void run() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy   hh:mm:ss a");

        while (running) {
            final String currentTime = formatter.format(new Date());

            SwingUtilities.invokeLater(new Runnable() {
                // Writes the new time on the label using the Swing thread.
                @Override
                public void run() {
                    clockLabel.setText(currentTime);
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    // Stops the clock thread safely.
    public void stopClock() {
        running = false;
        interrupt();
    }
}
