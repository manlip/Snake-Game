package com.ts.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.EnumMap;

public class GraphicsConfig {
    public enum SnakeSegmentType {
        HEAD,
        BODY,
        TAIL
    }

    private final EnumMap<SnakeSegmentType, Drawable> snakeDrawables = new EnumMap<>(SnakeSegmentType.class);
    private final Drawable foodDrawable;
    private final Drawable grassDrawable;

    public GraphicsConfig(Context context) {
        snakeDrawables.put(SnakeSegmentType.HEAD, ContextCompat.getDrawable(context, R.drawable.snake_head));
        snakeDrawables.put(SnakeSegmentType.BODY, ContextCompat.getDrawable(context, R.drawable.snake_body));
        snakeDrawables.put(SnakeSegmentType.TAIL, ContextCompat.getDrawable(context, R.drawable.snake_tail));
        foodDrawable = ContextCompat.getDrawable(context, R.drawable.food_apple);
        grassDrawable = ContextCompat.getDrawable(context, R.drawable.grass_tile);
    }

    public Drawable getSnakeDrawable(SnakeSegmentType type) {
        return snakeDrawables.get(type);
    }

    public Drawable getFoodDrawable() {
        return foodDrawable;
    }

    public Drawable getGrassDrawable() {
        return grassDrawable;
    }
}

