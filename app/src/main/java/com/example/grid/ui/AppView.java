package com.example.grid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.grid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.grid.logic.GameBoard;
import com.example.grid.logic.GameMode;
import com.example.grid.logic.Player;
import com.example.grid.logic.TickListener;
import com.example.grid.logic.TickHandler;

public class AppView extends AppCompatImageView implements TickListener {
    private GameBoard gameBoard;
    private boolean gamePaused = false;
    private Paint paint;
    private List<GridButton> buttons;
    private int gridSize = 5;
    private float cellSize;
    private Bitmap redImage;
    private Bitmap blueImage;
    private boolean isXturn;
    private ArrayList<GuiToken> tokens;
    private TickHandler tickHandler;
    private ArrayList<GuiToken> neighbors;
    private GameMode gameMode = GameMode.ONE_PLAYER;
    private MediaPlayer victorySound;
    private int currentBgIndex = 0;
    private final int[] bgResources = {
            R.drawable.playbakground1,
            R.drawable.playbackground2,
            R.drawable.playbackground3,
    };
    /**
     * Constructor for AppView
     * @param context
     * @param attrs
     */
    // Constructor to initialize the View and the Paint object
    public AppView(Context context, AttributeSet attrs, boolean player) {
        super(context, attrs);
        inst();
        gameBoard = new GameBoard();
        tickHandler = new TickHandler();
        tickHandler.addListener(this);
        tickHandler.start();
        if (player) {
            gameMode = GameMode.ONE_PLAYER;
        } else {
            gameMode = GameMode.TWO_PLAYERS;
        }
        setBg();
        setScaleType(ImageView.ScaleType.FIT_XY);
        this.victorySound = MediaPlayer.create(context, R.raw.victory);

    }
    // Set the background image
    private void setBg() {
        setImageResource(bgResources[currentBgIndex]);
    }

    // Cycle to the next background image
    private void cycleBg() {
        currentBgIndex = (currentBgIndex + 1) % bgResources.length; // Cycle through the resources
        setBg();
    }


    private void inst() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        buttons = new ArrayList<>();
        isXturn = true;

        redImage = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        blueImage = BitmapFactory.decodeResource(getResources(), R.drawable.blue);

        tokens = new ArrayList<>();
    }

    // Initialize buttons
    private void initializeButtons(float left, float top, float cellSize) {

        for (int i = 0; i < gridSize; i++) {
            char label = (char) ('1' + i);
            float x = left + (i * cellSize);
            float y = top - cellSize;
            GridButton button = new GridButton(getResources(), label, x, y, cellSize, cellSize, getContext());
            buttons.add(button);

        }

        for (int i = 0; i < gridSize; i++) {
            char label = (char) ('A' + i);
            float x = left - cellSize;
            float y = top + i * cellSize;
            GridButton button = new GridButton(getResources(), label, x, y, cellSize, cellSize, getContext());
            buttons.add(button);
        }
    }

    private GridButton selectRandomAvailableButton() {
        List<GridButton> availableButtons = new ArrayList<>();
        for (GridButton button : buttons) {
            if (!button.isPressed()) { // Assuming isPressed() checks if the button is already used
                availableButtons.add(button);
            }
        }

        if (availableButtons.isEmpty()) {
            return null; // No available buttons, AI cannot make a move
        }

        // Randomly select one of the available buttons
        int index = new Random().nextInt(availableButtons.size());
        return availableButtons.get(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ArrayList<GuiToken> tokensToRemove = new ArrayList<>();

        // Get the width and height of the view
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        // Calculate grid dimensions
        float gridHeight = viewHeight * 0.42f; // 42% of the view's height for the grid
        float gridWidth = gridHeight;         // Grid is square
        float lineThickness = gridHeight * 0.01f; // Line thickness as 1% of grid size
        paint.setStrokeWidth(lineThickness);

        // Calculate the start points to center the grid
        float left = (viewWidth - gridWidth) / 2;
        float top = (viewHeight - gridHeight) / 2;

        // Use class-level cellSize instead of redeclaring it locally
        cellSize = gridHeight / 5;

        // Draw horizontal lines
        for (int i = 0; i <= 5; i++) {
            float y = top + i * cellSize;
            canvas.drawLine(left, y, left + gridWidth, y, paint); // Horizontal lines
        }

        // Draw vertical lines
        for (int i = 0; i <= 5; i++) {
            float x = left + i * cellSize;
            canvas.drawLine(x, top, x, top + gridHeight, paint); // Vertical lines
        }
        // Initialize buttons
        if (buttons.isEmpty()) {
            initializeButtons( left, top, cellSize);
        }
        // Draw buttons
        buttons.forEach(button -> button.draw(canvas));


        // Draw each token and check if it is off-screen
        for (GuiToken token : tokens) {
            token.move();
            token.draw(canvas);

            // Check if the token is invisible
            if (token.isInvisible(viewHeight)) {
                tokensToRemove.add(token);  // Mark for removal
            }
        }

        // Remove invisible tokens and unregister them as listeners
        for (GuiToken token : tokensToRemove) {
            tokens.remove(token);
            tickHandler.removeListener(token);  // Unregister from TickHandler
        }


    }

    /**
     * Handle touch events
     * @param event param for touch event
     * @return true if event is handled, false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (GuiToken.areAnyMoving() || gamePaused) {
            return false;
        }

        if (!GuiToken.areAnyMoving()) {
            Player winner = gameBoard.checkForWin();
            if (winner != Player.BLANK) {
                showWinDialog(winner);
            } else {
                if (gameMode == GameMode.ONE_PLAYER && !isXturn) {
                    new Thread(() -> {
                        GridButton randomButton = selectRandomAvailableButton();
                        if (randomButton != null) {
                            randomButton.press();
                            gameBoard.submitMove(randomButton.getLabel(), gameBoard.getCurrentPlayer());
                            isXturn = !isXturn;
                            postInvalidate(); // Redraw the view from worker thread

                        }
                    }).start();
                }
            }
        }


        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean buttonPressed = false;
                // Inside the ACTION_DOWN case of onTouchEvent
                for (GridButton button : buttons) {
                    if (button.isInside(x, y)) {
                        button.press();
                        buttonPressed = true;
                        char move = button.getLabel();
                        gameBoard.submitMove(move, gameBoard.getCurrentPlayer());

                        char row = 'A' - 1;
                        char col = button.getLabel();
                        boolean isColumnButton = (col >= '1' && col <= '5');
                        neighbors = new ArrayList<>();


                        if (isColumnButton) { // Downward movement
                            RectF tokenBounds = new RectF(button.getBounds().left, button.getBounds().top,
                                    button.getBounds().right, button.getBounds().bottom);
                            RectF tBounds = new RectF(button.getBounds().left, button.getBounds().top + cellSize,
                                    button.getBounds().right, button.getBounds().bottom + cellSize);
                            Bitmap tokenImage = isXturn ? blueImage : redImage;
                            GuiToken.GridPosition tokenPosition = new GuiToken.GridPosition(row, col);
                            // Add the new token to neighbors
                            GuiToken newToken = new GuiToken(tokenBounds, tokenImage, isXturn, new PointF(0, 5), new GuiToken.GridPosition(row, col), getContext());
                            newToken.setDestination(tBounds);
                            newToken.setVelocity(new PointF(0, 10));
                            tokens.add(newToken);
                            neighbors.add(newToken);
                            tickHandler.addListener(newToken);

                            // Slide other tokens in the same column
                            for (char gridRow = 'A'; gridRow <= 'E'; gridRow++) {
                                GuiToken tokenAtPos = findTokenAtPosition(gridRow, col);
                                if (tokenAtPos != null) {
                                    neighbors.add(tokenAtPos);
                                } else {
                                    break; // Stop if an empty cell is found
                                }
                            }

                            for (GuiToken token : neighbors) {
                                RectF currentBounds = token.getBounds();
                                RectF newDestination = new RectF(currentBounds.left, currentBounds.top + cellSize,
                                        currentBounds.right, currentBounds.bottom + cellSize);
                                token.setDestination(newDestination);
                                token.setVelocity(new PointF(0, 5)); // Set downward velocity
                                token.getPosition().incrementRow(); // Increment row position
                            }

                        } else { // Rightward movement
                            RectF tokenBounds = new RectF(button.getBounds().right, button.getBounds().top,
                                    button.getBounds().right + cellSize, button.getBounds().bottom);
                            Bitmap tokenImage = isXturn ? blueImage : redImage;
                            GuiToken.GridPosition tokenPosition = new GuiToken.GridPosition(row, col);
                            GuiToken newToken = new GuiToken(tokenBounds, tokenImage, isXturn, new PointF(5, 0), new GuiToken.GridPosition(row, col), getContext());
                            neighbors.add(newToken);
                            tickHandler.addListener(newToken);
                            GuiToken.incrementMovers();

                            // Slide other tokens in the same row
                            for (char colChar = '1'; colChar <= '5'; colChar++) {
                                GuiToken tokenAtPos = findTokenAtPosition(button.getLabel(), colChar);
                                if (tokenAtPos != null) {
                                    neighbors.add(tokenAtPos);
                                } else {
                                    break;
                                }
                            }

                            for (GuiToken token : neighbors) {
                                RectF currentBounds = token.getBounds();
                                RectF newDestination = new RectF(currentBounds.left + cellSize, currentBounds.top,
                                        currentBounds.right + cellSize, currentBounds.bottom);
                                token.setDestination(newDestination);
                                token.setVelocity(new PointF(5, 0));
                                token.getPosition().incrementCol();
                            }
                        }
                        isXturn = !isXturn;
                        invalidate();
                        break;
                    }
                }

                if (!buttonPressed) {
                    // Show a toast if the touch was outside any button
                    Toast.makeText(getContext(), "Please touch one of the buttons", Toast.LENGTH_SHORT).show();
                }
                return true;

            case MotionEvent.ACTION_UP:
                Log.d("AppView", "onTouchEvent: ACTION_UP detected");
                // Release all buttons
                for (GridButton button : buttons) {
                    button.release();
                }
                invalidate();  // Redraw the view to show all buttons unpressed
                return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Finds the token at the specified grid position (row, col).
     */
    private GuiToken findTokenAtPosition(char row, char col) {
        for (GuiToken token : tokens) {
            if (token.getPosition().row == row && token.getPosition().col == col) {
                return token;
            }
        }
        return null;
    }

    /**
     * Called by the TickHandler to update the view.
     */
    @Override
    public void onTick() {

        invalidate();
    }

    private void showWinDialog(Player winner) {
        if (victorySound != null) {
            victorySound.start();
        }
        String message;
        if (winner == Player.X) {
            message = "X";
        } else if (winner == Player.O) {
            message = "O";
        } else {
            message = "It's a tie!";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Game Over");
        builder.setMessage(message + " wins! Play again?");
        // Lambda expression for "Yes" button to restart the game
        builder.setPositiveButton("Yes", (dialog, which) -> restartGame());

        // Anonymous class for "No" button to exit the program
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) getContext()).finish();  // Exit the activity
            }
        });

        builder.show();
    }


    private void restartGame() {
        tokens.clear();                  // Clear all tokens
        for (GuiToken token : tokens) {
            tickHandler.removeListener(token);  // Unregister tokens as listeners
        }
        gameBoard = new GameBoard();     // Reset the game board
        isXturn = true;                  // Reset turn to initial player
        cycleBg();               // Cycle to the next background image
        invalidate();                    // Redraw the view
    }



}