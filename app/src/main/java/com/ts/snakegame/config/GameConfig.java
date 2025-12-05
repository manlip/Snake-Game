package com.ts.snakegame.config;

import com.ts.snakegame.model.FoodType;
import com.ts.snakegame.model.ObstacleType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Central configuration class for game rules and behavior
 * All gameplay parameters can be customized here
 */
public class GameConfig {
    // ===== Grid Configuration =====
    /** Number of cells in grid (lower = bigger cells) */
    public static final int GRID_SIZE = 12; // Changed from 15 to 12 for 4x bigger cells

    // ===== Collision Rules =====
    /** Enable wall collision (game over when hitting walls) */
    public boolean wallCollisionEnabled = true;

    /** Enable self collision (game over when hitting own body) */
    public boolean selfCollisionEnabled = true;

    /** Enable obstacle collision */
    public boolean obstacleCollisionEnabled = true;

    /** Enable wrap-around mode (snake appears on opposite side) */
    public boolean wrapAroundMode = false;

    // ===== Speed Configuration =====
    /** Base game speed in milliseconds (lower = faster) */
    public int baseGameSpeed = 200;

    /** Minimum game speed (speed cap) */
    public int minGameSpeed = 50;

    /** Maximum game speed (slowest) */
    public int maxGameSpeed = 500;

    /** Speed decrease per score threshold */
    public int speedDecreasePerThreshold = 10;

    /** Score threshold to increase speed */
    public int scoreThresholdForSpeedIncrease = 50;

    // ===== Scoring Configuration =====
    /** Base score for normal food */
    public int normalFoodScore = 10;

    /** Score multiplier (can be changed dynamically) */
    public float scoreMultiplier = 1.0f;

    // ===== Food Configuration =====
    /** Maximum number of food items on screen */
    public int maxFoodItems = 3;

    /** Food spawn interval in milliseconds */
    public int foodSpawnInterval = 5000;

    /** Enabled food types */
    public Set<FoodType> enabledFoodTypes = new HashSet<>(Arrays.asList(
        FoodType.NORMAL,
        FoodType.BONUS,
        FoodType.POISON,
        FoodType.FREEZE
    ));

    // ===== Obstacle Configuration =====
    /** Enable obstacles feature */
    public boolean obstaclesEnabled = true;

    /** Maximum number of obstacles on screen */
    public int maxObstacles = 5;

    /** Obstacle spawn interval in milliseconds */
    public int obstacleSpawnInterval = 10000;

    /** Enabled obstacle types */
    public Set<ObstacleType> enabledObstacleTypes = new HashSet<>(Arrays.asList(
        ObstacleType.STONE,
        ObstacleType.WOOD
    ));

    // ===== Snake Configuration =====
    /** Initial snake length */
    public int initialSnakeLength = 3;

    /** Minimum snake length (below this = game over) */
    public int minSnakeLength = 2;

    // ===== Singleton Instance =====
    private static GameConfig instance;

    private GameConfig() {}

    /**
     * Get singleton instance of GameConfig
     */
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    /**
     * Calculate current game speed based on score
     */
    public int getCurrentSpeed(int score) {
        int speedDecrease = (score / scoreThresholdForSpeedIncrease) * speedDecreasePerThreshold;
        int currentSpeed = baseGameSpeed - speedDecrease;
        return Math.max(minGameSpeed, Math.min(maxGameSpeed, currentSpeed));
    }

    /**
     * Check if food type is enabled
     */
    public boolean isFoodTypeEnabled(FoodType foodType) {
        return enabledFoodTypes.contains(foodType);
    }

    /**
     * Check if obstacle type is enabled
     */
    public boolean isObstacleTypeEnabled(ObstacleType obstacleType) {
        return obstaclesEnabled && enabledObstacleTypes.contains(obstacleType);
    }

    /**
     * Reset to default configuration
     */
    public void resetToDefaults() {
        wallCollisionEnabled = true;
        selfCollisionEnabled = true;
        obstacleCollisionEnabled = true;
        wrapAroundMode = false;
        baseGameSpeed = 200;
        scoreMultiplier = 1.0f;
        maxFoodItems = 3;
        obstaclesEnabled = true;
        maxObstacles = 5;
    }
}

