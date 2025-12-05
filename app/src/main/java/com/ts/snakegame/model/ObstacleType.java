package com.ts.snakegame.model;

/**
 * Enum defining obstacle types that can appear on the game board
 * Obstacles block movement and cause game over on collision
 */
public enum ObstacleType {
    /**
     * Stone obstacle - Immovable rock
     */
    STONE("Stone", 0.3f),

    /**
     * Wood/Branch obstacle - Fallen branch
     */
    WOOD("Wood Branch", 0.3f),

    /**
     * Wall segment - Additional wall pieces
     */
    WALL("Wall", 0.2f);

    private final String displayName;
    private final float spawnProbability;

    ObstacleType(String displayName, float spawnProbability) {
        this.displayName = displayName;
        this.spawnProbability = spawnProbability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getSpawnProbability() {
        return spawnProbability;
    }
}

