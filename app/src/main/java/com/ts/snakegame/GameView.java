package com.ts.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * View layer for Snake Game - handles only rendering and user input
 * Game logic is delegated to SnakeGameLogic class
 */
public class GameView extends View {
    private static final int GRID_SIZE = 15; // Reduced from 20 for bigger, more visible cells
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private int cellSize;
    private int gridWidth;
    private int gridHeight;

    private SnakeGameLogic gameLogic;
    private boolean isPausedByUser;

    private Paint gameOverPaint;
    private Paint textPaint;

    private OnScoreChangeListener scoreChangeListener;
    private OnGameOverListener gameOverListener;
    private GestureDetector gestureDetector;
    private GraphicsConfig graphicsConfig;

    // Interfaces for MainActivity compatibility
    public interface OnScoreChangeListener {
        void onScoreChange(int score);
    }

    public interface OnGameOverListener {
        void onGameOver(int finalScore);
    }

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        gameOverPaint = new Paint();
        gameOverPaint.setColor(ContextCompat.getColor(context, R.color.game_over_overlay));
        gameOverPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.white));
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (gameLogic == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            gameLogic.setDirection(SnakeGameLogic.Direction.RIGHT);
                        } else {
                            gameLogic.setDirection(SnakeGameLogic.Direction.LEFT);
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            gameLogic.setDirection(SnakeGameLogic.Direction.DOWN);
                        } else {
                            gameLogic.setDirection(SnakeGameLogic.Direction.UP);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (gameLogic != null && gameLogic.isGameOver()) {
                    resetGame();
                }
                return true;
            }
        });

        graphicsConfig = new GraphicsConfig(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        gridWidth = w / GRID_SIZE;
        gridHeight = h / GRID_SIZE;
        cellSize = Math.min(w / gridWidth, h / gridHeight);

        // Initialize game logic with grid dimensions
        gameLogic = new SnakeGameLogic(gridWidth, gridHeight);
        gameLogic.setEventListener(new SnakeGameLogic.GameEventListener() {
            @Override
            public void onScoreChanged(int score) {
                if (scoreChangeListener != null) {
                    scoreChangeListener.onScoreChange(score);
                }
                invalidate();
            }

            @Override
            public void onGameOver(int finalScore) {
                if (gameOverListener != null) {
                    gameOverListener.onGameOver(finalScore);
                }
                invalidate();
            }

            @Override
            public void onFoodEaten() {
                invalidate();
            }
        });

        resetGame();
    }

    public void resetGame() {
        if (gameLogic == null) {
            return;
        }
        gameLogic.reset();
        isPausedByUser = true;
        invalidate();

        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChange(0);
        }
    }

    public void setDirection(SnakeGameLogic.Direction newDirection) {
        if (gameLogic != null) {
            gameLogic.setDirection(newDirection);
        }
    }

    public void update() {
        if (gameLogic != null) {
            gameLogic.update();
            invalidate();
        }
    }

    public boolean isGameOver() {
        return gameLogic != null && gameLogic.isGameOver();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gridWidth == 0 || gridHeight == 0 || gameLogic == null) return;

        // Draw grass background
        drawGrassBackground(canvas);

        // Draw score counter in top-right corner
        Paint scorePaint = new Paint();
        scorePaint.setColor(0xFFFFFFFF);
        scorePaint.setTextSize(48);
        scorePaint.setTextAlign(Paint.Align.RIGHT);
        scorePaint.setAntiAlias(true);
        scorePaint.setShadowLayer(4, 2, 2, 0xFF000000);
        canvas.drawText("Score: " + gameLogic.getScore(), getWidth() - 20, 60, scorePaint);

        // Draw snake
        List<Point> snake = gameLogic.getSnake();
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            drawSnakeSegment(canvas, segment, i, snake.size());
        }

        // Draw food
        Point food = gameLogic.getFood();
        if (food != null) {
            drawFood(canvas, food);
        }

        // Draw game over overlay
        if (gameLogic.isGameOver()) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), gameOverPaint);

            canvas.drawText("Game Over!", getWidth() / 2f, getHeight() / 2f - 50, textPaint);

            Paint smallTextPaint = new Paint(textPaint);
            smallTextPaint.setTextSize(40);
            canvas.drawText("Score: " + gameLogic.getScore(), getWidth() / 2f, getHeight() / 2f + 30, smallTextPaint);
            canvas.drawText("Tap to Restart", getWidth() / 2f, getHeight() / 2f + 90, smallTextPaint);
        }

        // Draw pause overlay
        if (isPausedByUser && !gameLogic.isGameOver()) {
            Paint pauseOverlayPaint = new Paint();
            pauseOverlayPaint.setColor(0x88000000);
            pauseOverlayPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getWidth(), getHeight(), pauseOverlayPaint);

            Paint pauseTextPaint = new Paint(textPaint);
            pauseTextPaint.setTextSize(50);
            canvas.drawText("Press Play to Start", getWidth() / 2f, getWidth() / 2f, pauseTextPaint);
        }
    }

    private void drawGrassBackground(Canvas canvas) {
        Drawable tile = graphicsConfig.getGrassDrawable();
        if (tile == null) {
            return;
        }
        Rect bounds = new Rect();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                bounds.set(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize);
                tile.setBounds(bounds);
                tile.draw(canvas);
            }
        }
    }

    private void drawSnakeSegment(Canvas canvas, Point segment, int index, int snakeSize) {
        GraphicsConfig.SnakeSegmentType type;
        if (index == 0) {
            type = GraphicsConfig.SnakeSegmentType.HEAD;
        } else if (index == snakeSize - 1) {
            type = GraphicsConfig.SnakeSegmentType.TAIL;
        } else {
            type = GraphicsConfig.SnakeSegmentType.BODY;
        }
        Drawable drawable = graphicsConfig.getSnakeDrawable(type);
        if (drawable == null) {
            return;
        }
        int left = segment.x * cellSize;
        int top = segment.y * cellSize;
        drawable.setBounds(left, top, left + cellSize, top + cellSize);
        drawable.draw(canvas);
    }

    private void drawFood(Canvas canvas, Point food) {
        Drawable drawable = graphicsConfig.getFoodDrawable();
        if (drawable == null) {
            return;
        }
        int left = food.x * cellSize;
        int top = food.y * cellSize;
        drawable.setBounds(left, top, left + cellSize, top + cellSize);
        drawable.draw(canvas);
    }

    /**
     * Draw obstacle at position
     */
    private void drawObstacle(Canvas canvas, Point obstacle) {
        // For now, draw simple rectangle for obstacles
        Paint obstaclePaint = new Paint();
        obstaclePaint.setColor(0xFF424242); // Dark gray
        obstaclePaint.setStyle(Paint.Style.FILL);

        int left = obstacle.x * cellSize;
        int top = obstacle.y * cellSize;
        canvas.drawRect(left, top, left + cellSize, top + cellSize, obstaclePaint);

        // Add border
        Paint borderPaint = new Paint();
        borderPaint.setColor(0xFF212121);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3);
        canvas.drawRect(left, top, left + cellSize, top + cellSize, borderPaint);
    }

    public void setScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreChangeListener = listener;
    }

    public void setGameOverListener(OnGameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void setPaused(boolean paused) {
        this.isPausedByUser = paused;
        invalidate();
    }

    public boolean isPaused() {
        return isPausedByUser;
    }

    public int getScore() {
        return gameLogic != null ? gameLogic.getScore() : 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
}

