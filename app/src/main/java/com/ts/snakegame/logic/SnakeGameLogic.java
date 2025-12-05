package com.ts.snakegame.logic;

import android.graphics.Point;

import com.ts.snakegame.config.GameConfig;
import com.ts.snakegame.model.Food;
import com.ts.snakegame.model.FoodType;
import com.ts.snakegame.model.Obstacle;
import com.ts.snakegame.model.ObstacleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core game logic class - handles snake movement, collisions, scoring
 * Separated from view layer for clean architecture
 */
public class SnakeGameLogic {

    /**
     * Snake movement directions
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Interface for game events - used to notify view layer
     */
    public interface GameEventListener {
        void onScoreChanged(int score);
        void onGameOver(int finalScore);
        void onFoodEaten(FoodType foodType);
        void onObstacleHit();
        void onSpeedChanged(int newSpeed);
    }

    private final int gridWidth;
    private final int gridHeight;
    private final Random random;
    private final GameConfig config;

    private List<Point> snake;
    private List<Food> foods;
    private List<Obstacle> obstacles;
    private Direction direction;
    private Direction nextDirection;
    private boolean isGameOver;
    private int score;
    private int currentSpeed;
    private long lastFoodSpawnTime;
    private long lastObstacleSpawnTime;
    private GameEventListener eventListener;

    /**
     * Constructor - initializes game with grid dimensions
     */
    public SnakeGameLogic(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.random = new Random();
        this.config = GameConfig.getInstance();
        this.snake = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        reset();
    }

    /**
     * Reset game to initial state
     */
    public void reset() {
        snake.clear();
        foods.clear();
        obstacles.clear();

        // Initialize snake in center
        int startX = gridWidth / 2;
        int startY = gridHeight / 2;
        for (int i = 0; i < config.initialSnakeLength; i++) {
            snake.add(new Point(startX - i, startY));
        }

        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        isGameOver = false;
        score = 0;
        currentSpeed = config.baseGameSpeed;
        lastFoodSpawnTime = System.currentTimeMillis();
        lastObstacleSpawnTime = System.currentTimeMillis();

        // Spawn initial food
        spawnFood();
    }

    /**
     * Set snake movement direction (with reverse prevention)
     */
    public void setDirection(Direction newDirection) {
        // Prevent 180-degree turns
        if (direction == Direction.UP && newDirection == Direction.DOWN) return;
        if (direction == Direction.DOWN && newDirection == Direction.UP) return;
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT) return;
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT) return;

        nextDirection = newDirection;
    }

    /**
     * Main game update - called each frame
     * Handles movement, collisions, spawning
     */
    public void update() {
        if (isGameOver) return;

        direction = nextDirection;
        Point head = snake.get(0);
        Point newHead = calculateNewHead(head);

        // Apply wrap-around if enabled
        if (config.wrapAroundMode) {
            newHead = wrapPosition(newHead);
        }

        // Check wall collision
        if (config.wallCollisionEnabled && !config.wrapAroundMode) {
            if (isOutOfBounds(newHead)) {
                gameOver();
                return;
            }
        }

        // Check self collision
        if (config.selfCollisionEnabled && isPointOnSnake(newHead)) {
            gameOver();
            return;
        }

        // Check obstacle collision
        if (config.obstacleCollisionEnabled && isPointOnObstacle(newHead)) {
            if (eventListener != null) {
                eventListener.onObstacleHit();
            }
            gameOver();
            return;
        }

        snake.add(0, newHead);

        // Check food collision
        Food eatenFood = getFoodAt(newHead);
        if (eatenFood != null) {
            handleFoodEaten(eatenFood);
        } else {
            snake.remove(snake.size() - 1);
        }

        // Spawn new items based on time
        trySpawnFood();
        trySpawnObstacle();
    }

    /**
     * Calculate new head position based on current direction
     */
    private Point calculateNewHead(Point current) {
        switch (direction) {
            case UP:
                return new Point(current.x, current.y - 1);
            case DOWN:
                return new Point(current.x, current.y + 1);
            case LEFT:
                return new Point(current.x - 1, current.y);
            case RIGHT:
                return new Point(current.x + 1, current.y);
            default:
                return current;
        }
    }

    /**
     * Wrap position for wrap-around mode
     */
    private Point wrapPosition(Point pos) {
        int x = pos.x;
        int y = pos.y;

        if (x < 0) x = gridWidth - 1;
        if (x >= gridWidth) x = 0;
        if (y < 0) y = gridHeight - 1;
        if (y >= gridHeight) y = 0;

        return new Point(x, y);
    }

    /**
     * Handle food consumption
     */
    private void handleFoodEaten(Food food) {
        FoodType type = food.getType();

        // Apply score
        int scoreGain = (int)(type.getScoreValue() * config.scoreMultiplier);
        score += scoreGain;

        // Apply length change
        int lengthChange = type.getLengthChange();
        if (lengthChange > 0) {
            // Keep last segment (don't remove)
            for (int i = 1; i < lengthChange; i++) {
                snake.add(snake.get(snake.size() - 1));
            }
        } else if (lengthChange < 0) {
            // Remove segments
            for (int i = 0; i < Math.abs(lengthChange) && snake.size() > config.minSnakeLength; i++) {
                snake.remove(snake.size() - 1);
            }
            // Game over if too short
            if (snake.size() < config.minSnakeLength) {
                gameOver();
                return;
            }
        }

        // Remove eaten food
        foods.remove(food);

        // Update speed based on score
        int newSpeed = config.getCurrentSpeed(score);
        if (newSpeed != currentSpeed) {
            currentSpeed = newSpeed;
            if (eventListener != null) {
                eventListener.onSpeedChanged(currentSpeed);
            }
        }

        // Notify listeners
        if (eventListener != null) {
            eventListener.onScoreChanged(score);
            eventListener.onFoodEaten(type);
        }
    }

    /**
     * Spawn new food item
     */
    private void spawnFood() {
        if (foods.size() >= config.maxFoodItems) return;

        FoodType selectedType = selectRandomFoodType();
        Point position = findEmptyPosition();

        if (position != null) {
            foods.add(new Food(position, selectedType));
        }
    }

    /**
     * Try to spawn food based on timer
     */
    private void trySpawnFood() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFoodSpawnTime >= config.foodSpawnInterval) {
            spawnFood();
            lastFoodSpawnTime = currentTime;
        }
    }

    /**
     * Spawn new obstacle
     */
    private void spawnObstacle() {
        if (!config.obstaclesEnabled) return;
        if (obstacles.size() >= config.maxObstacles) return;

        ObstacleType selectedType = selectRandomObstacleType();
        Point position = findEmptyPosition();

        if (position != null) {
            obstacles.add(new Obstacle(position, selectedType));
        }
    }

    /**
     * Try to spawn obstacle based on timer
     */
    private void trySpawnObstacle() {
        if (!config.obstaclesEnabled) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastObstacleSpawnTime >= config.obstacleSpawnInterval) {
            spawnObstacle();
            lastObstacleSpawnTime = currentTime;
        }
    }

    /**
     * Select random food type based on probabilities
     */
    private FoodType selectRandomFoodType() {
        List<FoodType> availableTypes = new ArrayList<>();
        for (FoodType type : FoodType.values()) {
            if (config.isFoodTypeEnabled(type)) {
                availableTypes.add(type);
            }
        }

        if (availableTypes.isEmpty()) {
            return FoodType.NORMAL;
        }

        // Weighted random selection
        float totalProbability = 0;
        for (FoodType type : availableTypes) {
            totalProbability += type.getSpawnProbability();
        }

        float randomValue = random.nextFloat() * totalProbability;
        float cumulative = 0;

        for (FoodType type : availableTypes) {
            cumulative += type.getSpawnProbability();
            if (randomValue <= cumulative) {
                return type;
            }
        }

        return availableTypes.get(0);
    }

    /**
     * Select random obstacle type
     */
    private ObstacleType selectRandomObstacleType() {
        List<ObstacleType> availableTypes = new ArrayList<>();
        for (ObstacleType type : ObstacleType.values()) {
            if (config.isObstacleTypeEnabled(type)) {
                availableTypes.add(type);
            }
        }

        if (availableTypes.isEmpty()) {
            return ObstacleType.STONE;
        }

        return availableTypes.get(random.nextInt(availableTypes.size()));
    }

    /**
     * Find empty position not occupied by snake, food, or obstacles
     */
    private Point findEmptyPosition() {
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            Point pos = new Point(random.nextInt(gridWidth), random.nextInt(gridHeight));
            if (!isPointOnSnake(pos) && getFoodAt(pos) == null && !isPointOnObstacle(pos)) {
                return pos;
            }
        }
        return null; // No empty position found
    }

    /**
     * Check if point is on snake
     */
    private boolean isPointOnSnake(Point point) {
        for (Point segment : snake) {
            if (segment.equals(point.x, point.y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if point is out of bounds
     */
    private boolean isOutOfBounds(Point point) {
        return point.x < 0 || point.x >= gridWidth || point.y < 0 || point.y >= gridHeight;
    }

    /**
     * Get food at position
     */
    private Food getFoodAt(Point point) {
        for (Food food : foods) {
            if (food.getPosition().equals(point.x, point.y)) {
                return food;
            }
        }
        return null;
    }

    /**
     * Check if point has obstacle
     */
    private boolean isPointOnObstacle(Point point) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getPosition().equals(point.x, point.y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trigger game over
     */
    private void gameOver() {
        isGameOver = true;
        if (eventListener != null) {
            eventListener.onGameOver(score);
        }
    }

    // ===== Getters =====

    public List<Point> getSnake() {
        return new ArrayList<>(snake);
    }

    public List<Food> getFoods() {
        return new ArrayList<>(foods);
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<>(obstacles);
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setEventListener(GameEventListener listener) {
        this.eventListener = listener;
    }
}

