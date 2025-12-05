package com.ts.snakegame;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.ts.snakegame.model.FoodType;
import com.ts.snakegame.model.ObstacleType;

import java.util.EnumMap;

/**
 * Graphics configuration class - manages drawable resources for game elements
 * Provides centralized access to all game graphics
 */
public class GraphicsConfig {
    public enum SnakeSegmentType {
        HEAD,
        BODY,
        TAIL
    }

    private final Context context;
    private final EnumMap<SnakeSegmentType, Drawable> snakeDrawables = new EnumMap<>(SnakeSegmentType.class);
    private final EnumMap<FoodType, Drawable> foodDrawables = new EnumMap<>(FoodType.class);
    private final EnumMap<ObstacleType, Drawable> obstacleDrawables = new EnumMap<>(ObstacleType.class);
    private final Drawable grassDrawable;

    public GraphicsConfig(Context context) {
        this.context = context;

        // Initialize snake drawables
        snakeDrawables.put(SnakeSegmentType.HEAD, ContextCompat.getDrawable(context, R.drawable.snake_head));
        snakeDrawables.put(SnakeSegmentType.BODY, ContextCompat.getDrawable(context, R.drawable.snake_body));
        snakeDrawables.put(SnakeSegmentType.TAIL, ContextCompat.getDrawable(context, R.drawable.snake_tail));

        // Initialize food drawables from FoodType enum
        for (FoodType foodType : FoodType.values()) {
            foodDrawables.put(foodType, ContextCompat.getDrawable(context, foodType.getDrawableResId()));
        }

        // Initialize obstacle drawables from ObstacleType enum
        for (ObstacleType obstacleType : ObstacleType.values()) {
            obstacleDrawables.put(obstacleType, ContextCompat.getDrawable(context, obstacleType.getDrawableResId()));
        }

        // Initialize grass background
        grassDrawable = ContextCompat.getDrawable(context, R.drawable.grass_tile);
    }

    public Drawable getSnakeDrawable(SnakeSegmentType type) {
        return snakeDrawables.get(type);
    }

    /**
     * Get drawable for specific food type
     * @param foodType The type of food
     * @return Drawable for the food type
     */
    public Drawable getFoodDrawable(FoodType foodType) {
        return foodDrawables.get(foodType);
    }

    /**
     * Get drawable for specific obstacle type
     * @param obstacleType The type of obstacle
     * @return Drawable for the obstacle type
     */
    public Drawable getObstacleDrawable(ObstacleType obstacleType) {
        return obstacleDrawables.get(obstacleType);
    }

    public Drawable getGrassDrawable() {
        return grassDrawable;
    }
}

