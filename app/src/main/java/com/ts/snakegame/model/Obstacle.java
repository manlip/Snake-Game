package com.ts.snakegame.model;
import android.graphics.Point;
public class Obstacle {
    private final Point position;
    private final ObstacleType type;
    private final long spawnTime;
    public Obstacle(Point position, ObstacleType type) {
        this.position = position;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }
    public Point getPosition() {
        return position;
    }
    public ObstacleType getType() {
        return type;
    }
    public long getSpawnTime() {
        return spawnTime;
    }
}
