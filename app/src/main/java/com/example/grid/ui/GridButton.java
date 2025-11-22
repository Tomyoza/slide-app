package com.example.grid.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;

import com.example.grid.R;

public class GridButton {
    private Bitmap unpressedImage; // Moved to class level
    private Bitmap pressedImage;
    private Bitmap img;
    protected RectF bounds; // Position and size of the button
    private char label;
    private boolean pressed;
    private MediaPlayer buttonPressSound;

    /**
     *
     * @param res   The resources object used to load the image
     * @param label The label of the button
     * @param x     The x-coordinate of the top-left corner of the button
     * @param y     The y-coordinate of the top-left corner of the button
     * @param width The width of the button
     * @param height The height of the button
     */

    // Constructor for GridButton that accepts the label, position, and size
    public GridButton(Resources res, char label, float x, float y, float width, float height, Context context) {
        this.label = label;
        this.bounds = new RectF(x, y, x + width, y + height);
        Bitmap unpressedImage = BitmapFactory.decodeResource(res, R.drawable.unpressed_button2);
        Bitmap pressedImage = BitmapFactory.decodeResource(res, R.drawable.pressed_button2);
        this.unpressedImage = Bitmap.createScaledBitmap(unpressedImage, (int) width, (int) height, false);
        this.pressedImage = Bitmap.createScaledBitmap(pressedImage, (int) width, (int) height, false);
        // Start with the unpressed state
        this.pressed = false;
        this.img = unpressedImage;
        this.buttonPressSound = MediaPlayer.create(context, R.raw.pressed_sound);

    }


    /**
     * Draws the button on the canvas
     * @param canvas The canvas to draw on
     */

    public void draw(Canvas canvas) {
        // Switch between pressed and unpressed images based on the button's state
        if (pressed) {
            img = pressedImage;
        } else {
            img = unpressedImage;
        }
        canvas.drawBitmap(img, null, bounds, null);
    }

    /**
     * Check if the given (x, y) coordinates are within the button bounds
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the point is inside the button, false otherwise
     */
    public boolean isInside(float x, float y) {
        return bounds.contains(x, y);
    }

    /**
     * Set the button to the pressed state
     */
    public void press() {
        this.pressed = true;
        if (buttonPressSound != null) {
            buttonPressSound.start(); // Play button press sound
        }
    }

    /**
     * Set the button to the unpressed state
     */
    public void release() {
        this.pressed = false;
    }

    /**
     * Check if the button is pressed
     * @return true if the button is pressed, false otherwise
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     *
     * @return The label of the button
     */
    public char getLabel() {
        return label;
    }

    /**
     * @return The bounds of the button
     */
    public RectF getBounds(){
        return bounds;
    }

}