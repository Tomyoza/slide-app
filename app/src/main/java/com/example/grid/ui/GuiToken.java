package com.example.grid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.grid.R;
import com.example.grid.logic.TickListener;

/**
 * GuiToken class represents a token in the GUI.
 */
public class GuiToken implements TickListener {
    private Bitmap img;
    protected RectF bounds;
    private PointF velocity;
    private RectF destination;
    private boolean isX;
    private GridPosition position;
    private boolean falling = false;
    private MediaPlayer dropoffSound;

    /**
     * GridPosition class represents a position on the grid.
     */
    public static class GridPosition {
        char row;
        char col;

        GridPosition(char row, char col) {
            this.row = row;
            this.col = col;
        }

        public void incrementRow() {
            row++;
        }

        public void incrementCol() {
            col++;
        }
    }
    /**
     *  Constructor for GuiToken
     * @param bounds The bounds of the token
     * @param img The image of the token
     * @param isX Whether the token is X or O
     * @param velocity The velocity of the token
     */
    public GuiToken(RectF bounds, Bitmap img, boolean isX, PointF velocity, GridPosition position, Context context){
        this.bounds = bounds;
        this.destination= bounds;
        this.img = img;
        this.velocity = velocity;
        this.isX = isX;
        this.position = position;
        this.dropoffSound = MediaPlayer.create(context, R.raw.dropoff);
    }

    /**
     * Draws the token on the canvas
     * @param canvas The canvas to draw on
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(img, null, bounds, null);
    }

    /**
     * Check if the token is invisible (off-screen).
     * @param screenHeight The height of the screen.
     * @return true if the token's top bound is off-screen.
     */
    public boolean isInvisible(float screenHeight) {
        return bounds.top > screenHeight;
    }

    /**
     * Updates the token's position based on its velocity
     */
    @Override
    public void onTick(){
        move();
        if (falling && dropoffSound != null && !dropoffSound.isPlaying()) {
            dropoffSound.start(); // Play dropoff sound if falling
        }
    }


    /**
     * Moves the token based on its velocity
     */
    public void move() {
        if(falling) {
            velocity.y *= 2;
        }

        // Start moving
        bounds.offset(velocity.x, velocity.y);

        // Check if token is close enough to destination to stop
        if (isCloseToDestination()) {
            bounds.set(destination);  // Snap to the destination
            velocity.set(0, 0);
            // Stop the movement
            decrementMovers();
        }

        if(position.row > 'E' || position.col > '5') {
            velocity.x = 0;
            velocity.y = 5;
            falling = true;
        }

    }

    /**
     * \
     * @return true if the token is moving
     */
    public boolean isMoving() {
        return (velocity.x > 0 || velocity.y > 0);
    }

    private static int movers = 0;

    /**
     * Increment movers counter
     */
    public static void incrementMovers() {
        movers++;
    }

    /**
     * Decrement movers counter
     */
    public static void decrementMovers() {
        if (movers > 0) {
            movers--;
        }
    }

    /**
     * Check if any token is moving
     * @return true if any token is moving
     */
    public static boolean areAnyMoving() {
        return movers > 0;
    }


    // Method to check if the token is close to its destination (you can fine-tune the tolerance)
    private boolean isCloseToDestination() {
        float tolerance = 5.0f;  // Adjust this as needed
        return Math.abs(bounds.left - destination.left) < tolerance &&
                Math.abs(bounds.top - destination.top) < tolerance;
    }

    /**
     *
     * @param velocity The new velocity of the token
     */
    public void setVelocity(PointF velocity) {
        this.velocity = velocity;
    }

    /**
     *
     * @param destination The new destination of the token
     */
    public void setDestination(RectF destination) {
        this.destination = destination;
    }

    /**
     *
     * @return The bounds of the token
     */
    public RectF getBounds(){
        return bounds;
    }

    /**
     * Get the position of the token
     * @return The position of the token
     */
    public GridPosition getPosition() {
        return position;
    }

}