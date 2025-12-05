package com.ts.snakegame.model;

import com.ts.snakegame.R;

/**
 * Enum defining obstacle types that can appear on the game board
 * Obstacles block movement and cause game over on collision
 * Includes drawable resource mapping for rendering
 */
public enum ObstacleType {
    /**
     * Stone obstacle - Immovable rock
     */
    STONE("Stone", 0.3f, R.drawable.obstacle_stone),

    /**
     * Wood/Branch obstacle - Fallen branch
     */
    WOOD("Wood Branch", 0.3f, R.drawable.obstacle_wood),

    /**
     * Wall segment - Additional wall pieces
     */
    WALL("Wall", 0.2f, R.drawable.obstacle_wall);

    private final String displayName;
    private final float spawnProbability;
    private final int drawableResId;

    ObstacleType(String displayName, float spawnProbability, int drawableResId) {
        this.displayName = displayName;
        this.spawnProbability = spawnProbability;
        this.drawableResId = drawableResId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getSpawnProbability() {
        return spawnProbability;
    }

    /**
     * Get the drawable resource ID for this obstacle type
     * @return Android drawable resource ID
     */
    public int getDrawableResId() {
        return drawableResId;
    }
}

