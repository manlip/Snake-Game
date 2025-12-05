package com.ts.snakegame;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ts.snakegame.config.GameConfig;
import com.ts.snakegame.logic.SnakeGameLogic;

/**
 * Main Activity - handles game UI and game loop
 * Delegates game logic to SnakeGameLogic class
 */
public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView tvScore;
    private Button btnPause;
    private ImageButton btnUp, btnDown, btnLeft, btnRight;

    private Handler gameHandler;
    private Runnable gameRunnable;
    private boolean isPaused = true; // Start in paused mode
    private int currentGameSpeed;
    private GameConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize configuration
        config = GameConfig.getInstance();
        currentGameSpeed = config.baseGameSpeed;

        // Initialize views
        gameView = findViewById(R.id.gameView);
        tvScore = findViewById(R.id.tvScore);
        btnPause = findViewById(R.id.btnPause);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        // Set up game handler and loop
        gameHandler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused && !gameView.isGameOver()) {
                    gameView.update();
                    // Update speed dynamically based on score
                    currentGameSpeed = config.getCurrentSpeed(gameView.getScore());
                    gameHandler.postDelayed(this, currentGameSpeed);
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
        btnUp.setOnClickListener(v -> gameView.setDirection(SnakeGameLogic.Direction.UP));
        btnDown.setOnClickListener(v -> gameView.setDirection(SnakeGameLogic.Direction.DOWN));
        btnLeft.setOnClickListener(v -> gameView.setDirection(SnakeGameLogic.Direction.LEFT));
        btnRight.setOnClickListener(v -> gameView.setDirection(SnakeGameLogic.Direction.RIGHT));

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

    /**
     * Initialize game in paused state
     */
    private void initGame() {
        isPaused = true;
        gameView.setPaused(true);
        btnPause.setText("▶"); // Show play button initially
        // Don't start game loop yet - wait for user to press play
    }

    /**
     * Start the game loop
     */
    private void startGame() {
        isPaused = false;
        btnPause.setText("||");
        currentGameSpeed = config.baseGameSpeed;
        gameHandler.postDelayed(gameRunnable, currentGameSpeed);
    }

    /**
     * Toggle pause/play state
     */
    private void togglePause() {
        if (gameView.isGameOver()) return;

        isPaused = !isPaused;
        gameView.setPaused(isPaused);

        if (isPaused) {
            btnPause.setText("▶");
            gameHandler.removeCallbacks(gameRunnable);
        } else {
            btnPause.setText("||");
            gameHandler.postDelayed(gameRunnable, currentGameSpeed);
        }
    }

    /**
     * Restart game after game over
     */
    private void restartGame() {
        gameView.resetGame();
        isPaused = true;
        gameView.setPaused(true);
        btnPause.setText("▶");
        gameHandler.removeCallbacks(gameRunnable);
        currentGameSpeed = config.baseGameSpeed;
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
            gameHandler.postDelayed(gameRunnable, currentGameSpeed);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacks(gameRunnable);
    }
}

