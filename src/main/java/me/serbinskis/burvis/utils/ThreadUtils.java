package me.serbinskis.burvis.utils;

public class ThreadUtils {
    public static Thread startThread(double interval, Runnable runnable) {
        // Create and start a new thread
        Thread thread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double delta = 0;

            // Run the loop for the timer
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();
                delta += (now - lastTime) / 1e9; // Convert nanoseconds to seconds
                lastTime = now;

                // When the delta exceeds the interval, run the task and reset delta
                if (delta >= interval) {
                    runnable.run();
                    delta -= interval; // Reset delta after the interval
                }

                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
        });

        thread.start();
        return thread;
    }

    public static Runnable startTimer(double interval, Runnable runnable) {
        final long[] lastTime = { System.nanoTime() };
        final double[] delta = {0};

        // Return the runnable that implements the timer logic
        return () -> {
            long now = System.nanoTime();
            delta[0] += (now - lastTime[0]) / 1e9; // Convert nanoseconds to seconds
            lastTime[0] = now;

            // When the delta exceeds the interval, run the task and reset delta
            if (delta[0] >= interval) {
                runnable.run();
                delta[0] -= interval; // Reset delta after the interval
            }
        };
    }
}
