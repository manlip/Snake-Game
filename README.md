# Snake Game - Android

A feature-rich Snake Game for Android with configurable gameplay mechanics, multiple food types, obstacles, and customizable rules.

## 📱 Features

### Core Gameplay
- Classic snake movement with swipe and button controls
- Bigger 12x12 grid for better visibility (4x larger than before)
- Dynamic speed adjustment based on score
- Pause/Resume functionality
- Orange/red snake on green grass for high contrast

### Food System
Multiple food types with unique effects:

| Food Type | Score | Length Change | Effect | Spawn Probability |
|-----------|-------|---------------|--------|-------------------|
| **Normal Apple** | +10 | +1 | Classic growth | 100% (always) |
| **Poison** | -20 | -2 | Shortens snake | 15% |
| **Bonus Cherry** | +50 | +2 | Extra points | 10% |
| **Freeze Berry** | +15 | +1 | Slows speed for 3s | 20% |
| **Speed Strawberry** | +20 | +1 | Speeds up for 3s | 15% |
| **Teleport Fruit** | +25 | 0 | Random teleport | 10% |

### Obstacle System
- **Stone**: Immovable rocks
- **Wood Branch**: Fallen branches
- Configurable spawn rates and quantities

### Collision Rules
Configurable collision detection:
- **Wall Collision**: Game over when hitting edges (can be disabled)
- **Self Collision**: Game over when snake hits itself (can be disabled)
- **Obstacle Collision**: Game over when hitting obstacles (can be disabled)
- **Wrap-Around Mode**: Snake appears on opposite side (optional)

## 🎮 Controls

### Button Controls
- **Up Arrow**: Move snake up
- **Down Arrow**: Move snake down
- **Left Arrow**: Move snake left
- **Right Arrow**: Move snake right
- **Play/Pause Button**: Toggle game state

### Swipe Gestures
- Swipe in any direction to control snake
- More intuitive for mobile gameplay

## ⚙️ Configuration

All game behavior can be customized in `GameConfig.java`:

### Grid Configuration
```java
public static final int GRID_SIZE = 12; // Number of cells (lower = bigger cells)
```

### Collision Rules
```java
public boolean wallCollisionEnabled = true;
public boolean selfCollisionEnabled = true;
public boolean obstacleCollisionEnabled = true;
public boolean wrapAroundMode = false; // Allow wrap-around
```

### Speed Configuration
```java
public int baseGameSpeed = 200; // Base speed in milliseconds
public int minGameSpeed = 50; // Fastest speed cap
public int maxGameSpeed = 500; // Slowest speed cap
public int speedDecreasePerThreshold = 10; // Speed increase amount
public int scoreThresholdForSpeedIncrease = 50; // Score needed to speed up
```

### Food Configuration
```java
public int maxFoodItems = 3; // Maximum food on screen
public int foodSpawnInterval = 5000; // Spawn interval in ms

// Enable/disable food types
public Set<FoodType> enabledFoodTypes = new HashSet<>(Arrays.asList(
    FoodType.NORMAL,
    FoodType.BONUS,
    FoodType.POISON,
    FoodType.FREEZE
));
```

### Obstacle Configuration
```java
public boolean obstaclesEnabled = true;
public int maxObstacles = 5; // Maximum obstacles on screen
public int obstacleSpawnInterval = 10000; // Spawn interval in ms

public Set<ObstacleType> enabledObstacleTypes = new HashSet<>(Arrays.asList(
    ObstacleType.STONE,
    ObstacleType.WOOD
));
```

### Snake Configuration
```java
public int initialSnakeLength = 3; // Starting length
public int minSnakeLength = 2; // Minimum before game over
```

## 🏗️ Architecture

### Package Structure
```
com.ts.snakegame/
├── config/
│   └── GameConfig.java          # Central configuration
├── logic/
│   └── SnakeGameLogic.java      # Core game logic
├── model/
│   ├── Food.java                # Food entity
│   ├── FoodType.java            # Food type enum
│   ├── Obstacle.java            # Obstacle entity
│   └── ObstacleType.java        # Obstacle type enum
├── GameView.java                # View layer (rendering)
├── MainActivity.java            # UI controller
└── GraphicsConfig.java          # Graphics/drawable management
```

### Separation of Concerns
- **Logic Layer** (`SnakeGameLogic`): Handles all game rules, collisions, scoring
- **View Layer** (`GameView`): Handles rendering and user input
- **Configuration** (`GameConfig`): Centralized settings management
- **Models**: Data classes for game entities

## 🎨 Customizing Graphics

### Snake Graphics
Snake graphics are defined in `/res/drawable/`:
- `snake_head.xml` - Orange snake head with eyes
- `snake_body.xml` - Orange snake body segment
- `snake_tail.xml` - Orange snake tail segment

To change snake color, edit the `fillColor` attributes:
```xml
<!-- Current: Orange (#FF6F00) -->
<path android:fillColor="#FF6F00" ... />

<!-- Change to blue for example: -->
<path android:fillColor="#2196F3" ... />
```

### Food Graphics
Food drawable: `food_apple.xml`
```xml
<!-- Red apple -->
<path android:fillColor="#E53935" ... />
```

### Grass Background
Grass tile: `grass_tile.xml`
Multiple shades of green create grass effect

### Changing Graphics
1. Navigate to `/app/src/main/res/drawable/`
2. Edit XML files or replace with new vector drawables
3. Maintain same file names for automatic updates
4. Or modify `GraphicsConfig.java` to use different drawables

## 🔧 Modifying Game Logic

### Adding New Food Type
1. Open `model/FoodType.java`
2. Add new enum value:
```java
NEW_FOOD(scoreValue, lengthChange, effectDuration, speedChange, "Display Name", spawnProbability);
```

3. Enable in `GameConfig.java`:
```java
enabledFoodTypes.add(FoodType.NEW_FOOD);
```

### Adding New Obstacle Type
1. Open `model/ObstacleType.java`
2. Add new enum value:
```java
NEW_OBSTACLE("Display Name", spawnProbability);
```

3. Enable in `GameConfig.java`:
```java
enabledObstacleTypes.add(ObstacleType.NEW_OBSTACLE);
```

### Changing Scoring System
Edit `GameConfig.java`:
```java
public float scoreMultiplier = 2.0f; // Double all scores
```

Or modify individual food scores in `FoodType.java`.

### Changing Speed Progression
```java
// Faster speed increases
config.scoreThresholdForSpeedIncrease = 30; // Speed up every 30 points

// More dramatic speed changes
config.speedDecreasePerThreshold = 20; // Decrease 20ms per threshold
```

## 🎯 Game Modes

### Classic Mode (Default)
- Wall collision: ON
- Self collision: ON
- Obstacles: ON
- Wrap-around: OFF

### Easy Mode
```java
config.wallCollisionEnabled = false;
config.selfCollisionEnabled = false;
config.wrapAroundMode = true;
```

### Hard Mode
```java
config.maxObstacles = 10;
config.obstacleSpawnInterval = 5000;
config.baseGameSpeed = 100;
```

### No Obstacles Mode
```java
config.obstaclesEnabled = false;
```

## 📊 Scoring Details

### Base Scoring
- Normal food: +10 points
- Each food type has its own score value
- Score affects speed: higher score = faster game

### Score Multiplier
Apply global multiplier:
```java
config.scoreMultiplier = 1.5f; // 50% more points
```

### Speed Calculation
```java
// Automatic speed calculation based on score
int speed = config.getCurrentSpeed(score);
// Formula: baseSpeed - ((score / threshold) * decrease)
// Clamped between minSpeed and maxSpeed
```

## 🐛 Debug Mode

To enable debug features, modify `GameConfig.java`:
```java
// Disable collisions for testing
config.wallCollisionEnabled = false;
config.selfCollisionEnabled = false;

// Faster food spawning
config.foodSpawnInterval = 1000;

// Only spawn specific food
config.enabledFoodTypes.clear();
config.enabledFoodTypes.add(FoodType.BONUS);
```

## 📝 Code Comments

All major classes and methods are documented with Javadoc comments:
- Class purpose and responsibility
- Method functionality
- Parameter descriptions
- Return value explanations

## 🚀 Building & Running

### Requirements
- Android Studio
- Android SDK API 21+
- Gradle 8.13

### Build
```bash
./gradlew assembleDebug
```

### Install
```bash
./gradlew installDebug
```

### Run
Open project in Android Studio and press Run, or install APK on device.

## 📱 Compatibility

- **Minimum SDK**: Android 5.0 (API 21)
- **Target SDK**: Android 11+ (API 30+)
- **Tested on**: Pixel 5 Emulator

## 🎨 Visual Improvements

### Version 2.0 Changes
- ✅ 4x bigger cells (12x12 grid instead of 20x20)
- ✅ Orange/red snake for high contrast against green grass
- ✅ Removed "Press Play to Start" text (shows as dark overlay)
- ✅ Multiple food items on screen
- ✅ Obstacle system
- ✅ Configurable collision rules
- ✅ Dynamic speed based on score

## 📄 License

[Your License Here]

## 👤 Author

[Your Name]

## 🤝 Contributing

Contributions welcome! Areas to enhance:
- Additional food types
- New obstacle types
- Power-ups system
- Sound effects
- Particle effects
- Leaderboard
- Achievements

---

**Enjoy the game! 🐍🎮**

