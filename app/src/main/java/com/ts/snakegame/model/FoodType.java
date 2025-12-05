package com.ts.snakegame.model;

/**
 * Enum defining different types of food items with their effects
 * Each food type has unique properties affecting gameplay
 */
public enum FoodType {
    /** Normal food - Classic apple that grows snake by 1 segment */
    NORMAL(10, 1, 0, 0, "Normal Apple", 1.0f),

    /** Poison food - Reduces snake length by 2 segments (game over if too short) */
    POISON(-20, -2, 0, 0, "Poison", 0.15f),

    /** Bonus food - Rare item giving extra points and length */
    BONUS(50, 2, 0, 0, "Bonus Cherry", 0.1f),

    /** Freeze food - Temporarily slows down the snake */
    FREEZE(15, 1, 3000, -50, "Freeze Berry", 0.2f),

    /** Speed food - Temporarily speeds up the snake */
    SPEED(20, 1, 3000, 50, "Speed Strawberry", 0.15f),

    /** Teleport food - Moves snake to random safe location */
    TELEPORT(25, 0, 0, 0, "Teleport Fruit", 0.1f);

    private final int scoreValue;
    private final int lengthChange;
    private final int effectDuration;
    private final int speedChange;
    private final String displayName;
    private final float spawnProbability;
    private final int drawableResId;

    FoodType(int scoreValue, int lengthChange, int effectDuration, int speedChange,
             String displayName, float spawnProbability, int drawableResId) {
        this.scoreValue = scoreValue;
        this.lengthChange = lengthChange;
        this.effectDuration = effectDuration;
        this.speedChange = speedChange;
        this.displayName = displayName;
        this.spawnProbability = spawnProbability;
        this.drawableResId = drawableResId;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getLengthChange() {
        return lengthChange;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public int getSpeedChange() {
        return speedChange;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getSpawnProbability() {
        return spawnProbability;
    }

    public boolean isTemporaryEffect() {
        return effectDuration > 0;
    }

    /**
     * Get the drawable resource ID for this food type
     * @return Android drawable resource ID
     */
    public int getDrawableResId() {
        return drawableResId;
    }
}

