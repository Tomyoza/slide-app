package com.example.grid.logic;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class TickHandler extends Handler {
    private List<TickListener> tickListeners = new ArrayList<>();
    private static final int TICK_MESSAGE = 0;

    /**
     * Add a listener to the list of listeners
     * @param listener The listener to add
     */
    public void addListener(TickListener listener) {
        tickListeners.add(listener);

    }

    /**
     * Remove a listener from the list of listeners
     * @param listener The listener to remove
     */
    public void removeListener(TickListener listener) {
        tickListeners.remove(listener);
    }

    /**
     * Start the tick handler
     */
    public void start() {
        sendEmptyMessage(TICK_MESSAGE);
    }

    /**
     * Stop the tick handler
     * @param msg The message to send
     */
    @Override
    public void handleMessage(Message msg) {
        for (TickListener listener : tickListeners) {
            listener.onTick();
        }
        sendEmptyMessageDelayed(0, 10);// Delay of 1 second (1000 milliseconds)
    }

}
