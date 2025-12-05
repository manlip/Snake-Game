package com.ts.snakegame;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView tvScore;
    private Button btnPause;
    private ImageButton btnUp, btnDown, btnLeft, btnRight;

    private Handler gameHandler;
    private Runnable gameRunnable;
    private boolean isPaused = false;
    private static final int GAME_SPEED = 150; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        gameView = findViewById(R.id.gameView);
        tvScore = findViewById(R.id.tvScore);
        btnPause = findViewById(R.id.btnPause);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        // Set up game handler
        gameHandler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused && !gameView.isGameOver()) {
                    gameView.update();
                    gameHandler.postDelayed(this, GAME_SPEED);
                }
            }
        };

        // Set score change listener
        gameView.setScoreChangeListener(new GameView.OnScoreChangeListener() {
            @Override
            public void onScoreChange(int score) {
                tvScore.setText(getString(R.string.score, score));
            }
        });

        // Set game over listener
        gameView.setGameOverListener(new GameView.OnGameOverListener() {
            @Override
            public void onGameOver(int finalScore) {
                // Game loop will stop automatically
            }
        });

        // Set up control buttons
        btnUp.setOnClickListener(v -> gameView.setDirection(GameView.Direction.UP));
        btnDown.setOnClickListener(v -> gameView.setDirection(GameView.Direction.DOWN));
        btnLeft.setOnClickListener(v -> gameView.setDirection(GameView.Direction.LEFT));
        btnRight.setOnClickListener(v -> gameView.setDirection(GameView.Direction.RIGHT));

        // Set up pause button
        btnPause.setOnClickListener(v -> togglePause());

        // Set up game view click for restart
        gameView.setOnClickListener(v -> {
            if (gameView.isGameOver()) {
                restartGame();
            }
        });

        // Initialize score display
        tvScore.setText(getString(R.string.score, 0));

        // Initialize game in paused state
        initGame();
    }

    private void initGame() {
        isPaused = true;
        gameView.setPaused(true); // Sync with GameView
        btnPause.setText("▶"); // Show play button initially
        // Don't start game loop yet - wait for user to press play
    }

    private void startGame() {
        isPaused = false;
        btnPause.setText("||");
        gameHandler.postDelayed(gameRunnable, GAME_SPEED);
    }

    private void togglePause() {
        if (gameView.isGameOver()) return;

        isPaused = !isPaused;
        if (isPaused) {
            btnPause.setText("▶");
            gameHandler.removeCallbacks(gameRunnable);
        } else {
            btnPause.setText("||");
            gameHandler.postDelayed(gameRunnable, GAME_SPEED);
        }
    }

    private void restartGame() {
        gameView.resetGame();
        isPaused = true;
        gameView.setPaused(true); // Sync with GameView
        btnPause.setText("▶"); // Show play button after restart
        gameHandler.removeCallbacks(gameRunnable);
        // Wait for user to press play
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameHandler.removeCallbacks(gameRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPaused && !gameView.isGameOver()) {
            gameHandler.postDelayed(gameRunnable, GAME_SPEED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacks(gameRunnable);
    }
}

