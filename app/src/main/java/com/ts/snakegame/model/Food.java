package com.ts.snakegame.model;
import android.graphics.Point;
public class Food {
    private final Point position;
    private final FoodType type;
    private final long spawnTime;
    public Food(Point position, FoodType type) {
        this.position = position;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }
    public Point getPosition() {
        return position;
    }
    public FoodType getType() {
        return type;
    }
    public long getSpawnTime() {
        return spawnTime;
    }
}
