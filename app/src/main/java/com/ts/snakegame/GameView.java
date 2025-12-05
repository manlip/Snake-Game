package com.ts.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    private static final int GRID_SIZE = 20;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private int cellSize;
    private int gridWidth;
    private int gridHeight;

    private List<Point> snake;
    private Point food;
    private Direction direction;
    private Direction nextDirection;
    private boolean isGameOver;
    private boolean isPausedByUser;
    private int score;

    private Paint grassDarkPaint;
    private Paint grassLightPaint;
    private Paint snakeBodyPaint;
    private Paint snakeHeadPaint;
    private Paint foodPaint;
    private Paint gameOverPaint;
    private Paint textPaint;

    private Random random;
    private OnScoreChangeListener scoreChangeListener;
    private OnGameOverListener gameOverListener;
    private GestureDetector gestureDetector;
    private GraphicsConfig graphicsConfig;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

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
        random = new Random();

        // Initialize paints
        grassDarkPaint = new Paint();
        grassDarkPaint.setColor(ContextCompat.getColor(context, R.color.grass_dark));
        grassDarkPaint.setStyle(Paint.Style.FILL);

        grassLightPaint = new Paint();
        grassLightPaint.setColor(ContextCompat.getColor(context, R.color.grass_light));
        grassLightPaint.setStyle(Paint.Style.FILL);

        snakeBodyPaint = new Paint();
        snakeBodyPaint.setColor(ContextCompat.getColor(context, R.color.snake_body));
        snakeBodyPaint.setStyle(Paint.Style.FILL);
        snakeBodyPaint.setAntiAlias(true);

        snakeHeadPaint = new Paint();
        snakeHeadPaint.setColor(ContextCompat.getColor(context, R.color.snake_head));
        snakeHeadPaint.setStyle(Paint.Style.FILL);
        snakeHeadPaint.setAntiAlias(true);

        foodPaint = new Paint();
        foodPaint.setColor(ContextCompat.getColor(context, R.color.food));
        foodPaint.setStyle(Paint.Style.FILL);
        foodPaint.setAntiAlias(true);

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
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Horizontal swipe
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            setDirection(Direction.RIGHT);
                        } else {
                            setDirection(Direction.LEFT);
                        }
                    }
                } else {
                    // Vertical swipe
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            setDirection(Direction.DOWN);
                        } else {
                            setDirection(Direction.UP);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isGameOver) {
                    resetGame();
                } else {
                    // Toggle pause
                    isPausedByUser = !isPausedByUser;
                    invalidate();
                }
                return true;
            }
        });

        graphicsConfig = new GraphicsConfig(context);

        resetGame();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate grid dimensions based on view size
        gridWidth = w / GRID_SIZE;
        gridHeight = h / GRID_SIZE;
        cellSize = Math.min(w / gridWidth, h / gridHeight);

        resetGame();
    }

    public void resetGame() {
        if (gridWidth == 0 || gridHeight == 0) {
            return; // Wait for onSizeChanged
        }

        snake = new ArrayList<>();
        // Start snake in the middle
        int startX = gridWidth / 2;
        int startY = gridHeight / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        isGameOver = false;
        isPausedByUser = true; // Reset to paused state
        score = 0;

        spawnFood();
        invalidate();

        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChange(score);
        }
    }

    private void spawnFood() {
        if (gridWidth == 0 || gridHeight == 0) {
            return; // Wait for proper initialization
        }
        do {
            food = new Point(random.nextInt(gridWidth), random.nextInt(gridHeight));
        } while (isPointOnSnake(food));
    }

    private boolean isPointOnSnake(Point point) {
        for (Point segment : snake) {
            if (segment.equals(point.x, point.y)) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(Direction newDirection) {
        // Prevent reverse direction
        if (direction == Direction.UP && newDirection == Direction.DOWN) return;
        if (direction == Direction.DOWN && newDirection == Direction.UP) return;
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT) return;
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT) return;

        nextDirection = newDirection;
    }

    public void update() {
        if (isGameOver) return;

        direction = nextDirection;
        Point head = snake.get(0);
        Point newHead;

        switch (direction) {
            case UP:
                newHead = new Point(head.x, head.y - 1);
                break;
            case DOWN:
                newHead = new Point(head.x, head.y + 1);
                break;
            case LEFT:
                newHead = new Point(head.x - 1, head.y);
                break;
            case RIGHT:
                newHead = new Point(head.x + 1, head.y);
                break;
            default:
                return;
        }

        // Check wall collision
        if (newHead.x < 0 || newHead.x >= gridWidth || newHead.y < 0 || newHead.y >= gridHeight) {
            gameOver();
            return;
        }

        // Check self collision
        if (isPointOnSnake(newHead)) {
            gameOver();
            return;
        }

        snake.add(0, newHead);

        // Check food collision
        if (newHead.equals(food.x, food.y)) {
            score += 10;
            spawnFood();
            if (scoreChangeListener != null) {
                scoreChangeListener.onScoreChange(score);
            }
        } else {
            snake.remove(snake.size() - 1);
        }

        invalidate();
    }

    private void gameOver() {
        isGameOver = true;
        if (gameOverListener != null) {
            gameOverListener.onGameOver(score);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gridWidth == 0 || gridHeight == 0) return;

        // Draw grass background in checkerboard pattern
        drawGrassBackground(canvas);

        // Draw snake
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            drawSnakeSegment(canvas, segment, i);
        }

        // Draw food
        if (food != null) {
            drawFood(canvas);
        }

        // Draw game over overlay
        if (isGameOver) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), gameOverPaint);

            canvas.drawText("Game Over!", getWidth() / 2f, getHeight() / 2f - 50, textPaint);

            Paint smallTextPaint = new Paint(textPaint);
            smallTextPaint.setTextSize(40);
            canvas.drawText("Score: " + score, getWidth() / 2f, getHeight() / 2f + 30, smallTextPaint);
            canvas.drawText("Tap to Restart", getWidth() / 2f, getHeight() / 2f + 90, smallTextPaint);
        }

        // Draw pause overlay
        if (isPausedByUser && !isGameOver) {
            Paint pauseOverlayPaint = new Paint();
            pauseOverlayPaint.setColor(0x88000000); // Semi-transparent black
            pauseOverlayPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getWidth(), getHeight(), pauseOverlayPaint);

            Paint pauseTextPaint = new Paint(textPaint);
            pauseTextPaint.setTextSize(50);
            canvas.drawText("Press Play to Start", getWidth() / 2f, getHeight() / 2f, pauseTextPaint);
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

    private void drawSnakeSegment(Canvas canvas, Point segment, int index) {
        GraphicsConfig.SnakeSegmentType type;
        if (index == 0) {
            type = GraphicsConfig.SnakeSegmentType.HEAD;
        } else if (index == snake.size() - 1) {
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

    private void drawFood(Canvas canvas) {
        Drawable drawable = graphicsConfig.getFoodDrawable();
        if (drawable == null || food == null) {
            return;
        }
        int left = food.x * cellSize;
        int top = food.y * cellSize;
        drawable.setBounds(left, top, left + cellSize, top + cellSize);
        drawable.draw(canvas);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreChangeListener = listener;
    }

    public void setGameOverListener(OnGameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void setPaused(boolean paused) {
        this.isPausedByUser = paused;
        invalidate(); // Redraw to show/hide pause overlay
    }

    public boolean isPaused() {
        return isPausedByUser;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
}
