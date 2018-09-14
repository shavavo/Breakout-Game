package example;

import example.GameComponents.*;
import example.UI.HUD;
import example.UI.Menu;
import example.UI.Theme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Worked on by: David Cheng
 *
 * MainGame controls the game, stores any game state variables, updates components, etc.
 */

public class MainGame extends Application {
    public interface UpdateableObject {
        boolean update(double elapsedTime);
    }

    public static final String TITLE = "BREAKOUT";
    public static final int SIZE = 500;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final String BOUNCER_IMAGE = "ball.gif";
    public static final int MOVER_SIZE = 75;
    public static final int MOVER_SPEED = 6;

    public enum GameState {
        WELCOME,
        PLAYING,
        INTERMISSION,
        GAME_OVER,
        WON
    }

    public int score;
    public int lives;
    public GameState gameState;

    private Scene myScene;
    private Group root;
    private int windowWidth;
    private int windowHeight;

    private HUD myHUD;
    private Menu myMenu;
    private Rectangle myPaddle;
    private List<Bouncer> myBouncers;
    private List<Block> myBlocks;
    private List<Drop> myDrops;
    private List<Laser> myLasers;
    private Drop.Type myActiveBouncerBuff;
    private Theme myTheme;
    private ArrayList<Level> myLevels;
    public int currentLevelNumber;
    public Level currentLevel;

    private Image bouncerImage;

    private HashMap<String, Boolean> currentlyActiveKeys = new HashMap<>();

    private int widthBuffLength = -1;
    private int lasersLeft = 0;


    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        ClassLoader classLoader = getClass().getClassLoader();
        Font.loadFont(classLoader.getResource("font.TTF").toExternalForm(), 10);
        myTheme = Menu.themes[0];

        // attach scene to the stage and display it
        myScene = setupGame( (int)(.8*SIZE) , SIZE, myTheme.getBgColor());
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();

        myLevels = new ArrayList<>();
        myLevels.add(new Level(myTheme, 1, 3, "level1.txt", 200, this));
        myLevels.add(new Level(myTheme, 2, 3, "level2.txt", 225, this));
        myLevels.add(new Level(myTheme, 3, 3, "level3.txt", 250, this));

        this.currentLevelNumber = 0;
        this.score = 0;

        myBlocks = new ArrayList<>();
        myLasers = new ArrayList<>();

        gameState = GameState.WELCOME;

        // attach "game loop" to timeline to play it
        var frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        var animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    /**
     * Create the game's "scene": what shapes will be in the game and their starting properties
     *
     * @param width
     * @param height
     * @param background
     * @return Scene
     */
    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        root = new Group();
        // create a place to see the shapes
        var scene = new Scene(root, width, height, background);
        // make some shapes and set their properties
        bouncerImage = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));

        this.windowWidth = width;
        this.windowHeight = height;

        myMenu = new Menu(width, height, myTheme.getTextColor(), this);

        myDrops = new ArrayList<Drop>();
        myBouncers = new ArrayList<>();

        myPaddle = new Rectangle(width/2 - MOVER_SIZE/2, .9 * height , MOVER_SIZE, MOVER_SIZE/5);
        myPaddle.setFill(myTheme.getPaddleColor());
        root.getChildren().add(myPaddle);

        myHUD = new HUD(width, height, myTheme.getTextColor(), root);

        // respond to input
        scene.setOnKeyPressed(event -> {
            String codeString = event.getCode().toString();
            if (!currentlyActiveKeys.containsKey(codeString)) {
                currentlyActiveKeys.put(codeString, true);
            }
        });

        scene.setOnKeyReleased(event -> currentlyActiveKeys.remove(event.getCode().toString()));

        return scene;
    }

    /**
     * Clears components from previous level and loads blocks for levelNumber
     *
     * @param levelNumber
     */
    public void loadLevel(int levelNumber) {
        gameState = GameState.PLAYING;

        // Clear previous levels elements
        for(Block block: myBlocks)
            root.getChildren().remove(block.getStack());
        myBlocks.clear();

        for(Bouncer bouncer: myBouncers)
            bouncer.remove();
        myBouncers.clear();

        for(Drop drop: myDrops)
            drop.remove();
        myDrops.clear();

        myActiveBouncerBuff = null;

        this.currentLevel = myLevels.get(levelNumber);
        this.lives = myLevels.get(levelNumber).getLivesStart();

        // Add initial bouncer
        myBouncers.add(new Bouncer(bouncerImage, windowWidth/20, windowHeight/2, 1, 1, 1, Bouncer.State.LAUNCH, currentLevel.getDefaultBouncerSpeed(),this));

        // Populate blocks
        myBlocks = currentLevel.populateBlocks(windowWidth, windowHeight);
        for(Block block: myBlocks)
            root.getChildren().add(block.getStack());

        // Update HUD
        myHUD.updateLives(lives);
        myHUD.updateLevel(currentLevel.getLevelNumber());

        myHUD.updatePrimaryLabel("");
        myHUD.updateSecondaryLabel("");
    }

    /**
     * Generalized function to call .update(elapsedTime) in a list of UpdateableObjects
     * Removes these objects if needed (if update() returns True)
     *
     * @param objectsToUpdate
     * @param elapsedTime
     */
    private void updateObjectList(List<? extends UpdateableObject> objectsToUpdate, double elapsedTime) {
        List<UpdateableObject> toRemove = new ArrayList<>();
        for(UpdateableObject object : objectsToUpdate) {
            boolean shouldRemove = object.update(elapsedTime);
            if(shouldRemove) toRemove.add(object);
        }

        objectsToUpdate.removeAll(toRemove);
    }

    /**
     * Change properties of shapes to animate them
     * Reads game state and reacts accordingly
     *
     * @param elapsedTime
     */
    private void step (double elapsedTime) {
        if(gameState==GameState.WELCOME) {
            myMenu.handleKey();
        }
        else if(gameState==GameState.PLAYING) {
            // update attributes
            updateObjectList(myBouncers, elapsedTime);
            updateObjectList(myDrops, elapsedTime);
            updateObjectList(myLasers, elapsedTime);

            handleKeys();

            // No bouncers left, lives - 1
            if(myBouncers.size()==0) {
                lives  -= 1;
                myHUD.updateLives(lives);

                if(lives==0) {
                    gameState = GameState.GAME_OVER;
                    myHUD.updatePrimaryLabel("GAME OVER");
                    myHUD.updateSecondaryLabel("PRESS SPACE TO RESTART LEVEL");
                } else {
                    myBouncers.add(new Bouncer(bouncerImage, windowWidth/20, windowHeight/2, 1, 1, 1, Bouncer.State.LAUNCH, currentLevel.getDefaultBouncerSpeed(),this));
                }
            }

            // No blocks left, level won
            if(myBlocks.size()==0) {
                if(currentLevelNumber == myLevels.size() - 1) {
                    gameState = GameState.WON;
                    myHUD.updatePrimaryLabel("YOU WON!");
                    myHUD.updateSecondaryLabel("SCORE: " + Integer.toString(score));
                } else {
                    gameState = GameState.INTERMISSION;
                    myHUD.updatePrimaryLabel("LEVEL COMPLETED");
                    myHUD.updateSecondaryLabel("PRESS SPACE TO START NEXT LEVEL");
                }
            }

        } else if(gameState==GameState.GAME_OVER) {
            if(isKeyActive(KeyCode.SPACE)) {
                loadLevel(currentLevelNumber);

                currentlyActiveKeys.remove(KeyCode.SPACE.toString());
            }
        } else if(gameState==GameState.INTERMISSION) {
            if(isKeyActive(KeyCode.SPACE)) {
                currentLevelNumber++;
                loadLevel(currentLevelNumber);

                currentlyActiveKeys.remove(KeyCode.SPACE.toString());
            }
        } else if(gameState==GameState.WON) {
            if(isKeyActive(KeyCode.SPACE)) {
                currentLevelNumber=0;
                myMenu.show();
                myHUD.clear();

                gameState = GameState.WELCOME;

                for(Bouncer bouncer: myBouncers)
                    bouncer.remove();
                myBouncers.clear();

                for(Drop drop: myDrops)
                    drop.remove();
                myDrops.clear();

                myActiveBouncerBuff = null;

                currentlyActiveKeys.remove(KeyCode.SPACE.toString());
            }
        }
    }

    /**
     * Handles any key presses like cheat keys or movement
     */
    public void handleKeys() {
        // Handle key presses
        if (isKeyActive(KeyCode.LEFT) && isKeyActive(KeyCode.RIGHT)) {
            // Do nothing
        }
        else if (isKeyActive(KeyCode.LEFT) && myPaddle.getX() > 0) {
            myPaddle.setX(myPaddle.getX() - MOVER_SPEED);
        }
        else if (isKeyActive(KeyCode.RIGHT) && myPaddle.getX() < myScene.getWidth() - myPaddle.getWidth()) {
            myPaddle.setX(myPaddle.getX() + MOVER_SPEED);
        }
        // Launch ball or shoot laser
        else if (isKeyActive(KeyCode.SPACE)) {
            if(myBouncers.get(0).getMyState() == Bouncer.State.LAUNCH)
                myBouncers.get(0).setMyState(Bouncer.State.NORMAL);
            else if(myActiveBouncerBuff == Drop.Type.LASER) {
                shootLaser();
            }
            currentlyActiveKeys.remove(KeyCode.SPACE.toString());
        }
        // Skip level
        else if (isKeyActive(KeyCode.PERIOD)) {
            for(Block block: myBlocks)
                root.getChildren().remove(block.getStack());
            myBlocks.clear();

            currentlyActiveKeys.remove(KeyCode.PERIOD.toString());
        }
        // Previous level
        else if (isKeyActive(KeyCode.COMMA)) {
            currentLevelNumber--;
            loadLevel(currentLevelNumber);

            currentlyActiveKeys.remove(KeyCode.COMMA.toString());
        }
        // Ball speed+
        else if (isKeyActive(KeyCode.EQUALS)) {
            myHUD.fadeNewLabel("BALL SPEED+");
            for(Bouncer bouncer: myBouncers)
                bouncer.changeSpeedBy(10);
        }
        // Ball speed-
        else if (isKeyActive(KeyCode.MINUS)) {
            myHUD.fadeNewLabel("BALL SPEED-");
            for(Bouncer bouncer: myBouncers)
                bouncer.changeSpeedBy(-10);
        } else if(isKeyActive(KeyCode.BACK_QUOTE)) {
            myHUD.fadeNewLabel("LIVES +1");
            lives += 1;
            myHUD.updateLives(lives);

            currentlyActiveKeys.remove(KeyCode.BACK_QUOTE.toString());
        }
    }

    /**
     * Checks to see if key is in HashMap and set to True
     *
     * @param code
     * @return True/False if key is active
     */
    private boolean isKeyActive(KeyCode code) {
        String codeString = code.toString();
        if(currentlyActiveKeys.containsKey(codeString) && currentlyActiveKeys.get(codeString))
            return true;
        return false;
    }


    /**
     * Creates power-up effects
     *
     * @param type
     */
    public void powerUp(Drop.Type type) {
        addToScore(500);

        switch(type) {
            case MOVER_SIZE_UP:
                myActiveBouncerBuff = type;
                myPaddle.setWidth(MOVER_SIZE * 1.5);
                widthBuffLength = 5;
                myHUD.fadeNewLabel("PADDLE SIZE+");
                break;
            case EXTRA_BALL:
                var image = new Image(this.getClass().getClassLoader().getResourceAsStream("ball.gif"));
                myHUD.fadeNewLabel("EXTRA BALL");
                myBouncers.add(
                        new Bouncer(image, (int)(myPaddle.getX() + myPaddle.getWidth()/2),
                                (int)(myPaddle.getY() - myPaddle.getHeight()), 1, -1, 1,
                                Bouncer.State.NORMAL, currentLevel.getDefaultBouncerSpeed(), this
                        )
                );
                break;
            case POWER_BOUNCHER:
                myHUD.fadeNewLabel("POWER BALL");
                myActiveBouncerBuff = type;
                myPaddle.setWidth(MOVER_SIZE);
                break;
            case LASER:
                myHUD.fadeNewLabel("LASERS");
                myActiveBouncerBuff = type;
                myPaddle.setWidth(MOVER_SIZE);
                lasersLeft = 3;
                break;
            case EXTRA_LIFE:
                myHUD.fadeNewLabel("LIVES +1");
                lives += 1;
                myHUD.updateLives(lives);
                break;
        }

        updatePaddleColor();
    }

    /**
     * Listener for when bouncer hits paddle
     * Required for paddle buff because it lasts 5 hits
     *
     * @param bouncer
     */
    public void onBouncerHitPaddle(Bouncer bouncer) {
        if(myActiveBouncerBuff == Drop.Type.POWER_BOUNCHER) {
            bouncer.setPowerBouncher(true);
            myActiveBouncerBuff = null;
            updatePaddleColor();
        } else {
            bouncer.setPowerBouncher(false);
        }

        if(widthBuffLength!=-1) {
            widthBuffLength--;

            if(widthBuffLength==0) {
                myPaddle.setWidth(MOVER_SIZE);
                myActiveBouncerBuff = null;
                updatePaddleColor();
                widthBuffLength = -1;
            }
        }
    }

    /**
     * Changes paddle color depending on active buff
     */
    public void updatePaddleColor() {
        // Power bouncer takes priority
        if(myActiveBouncerBuff == Drop.Type.POWER_BOUNCHER)
            myPaddle.setFill(Color.YELLOW);
        else if(myActiveBouncerBuff == Drop.Type.MOVER_SIZE_UP)
            myPaddle.setFill(Color.RED);
        else if(myActiveBouncerBuff == Drop.Type.LASER)
            myPaddle.setFill(Color.DARKBLUE);
        else
            myPaddle.setFill(myTheme.getPaddleColor());
    }

    /**
     *  Creates new instance of laser
     */
    private void shootLaser() {
        myLasers.add(
                new Laser(myPaddle.getX() + myPaddle.getWidth()/2, myPaddle.getY() - windowHeight*.05,
                        windowWidth*.01, windowHeight*.05, this)
        );

        lasersLeft--;

        if(lasersLeft==0) {
            myActiveBouncerBuff = null;
            updatePaddleColor();
        }

    }

    /**
     * Loads theme by changing colors
     *
     * @param theme
     */
    public void loadTheme(Theme theme) {
        myTheme = theme;
        
        myPaddle.setFill(theme.getPaddleColor());
        myHUD.updateColor(theme.getTextColor());
        myMenu.updateColor(theme.getTextColor());

        myScene.setFill(theme.getBgColor());

    }

    public void addToScore(int value) {
        score += value;
        myHUD.updateScore(score);
    }

    // GETTERS

    public Scene getMyScene() { return myScene; }

    public List<Block> getMyBlocks() { return myBlocks; }

    public Rectangle getMyPaddle() { return myPaddle; }

    public List<Drop> getMyDrops() { return myDrops; }

    public Group getRoot() { return root; }

    public HUD getMyHUD() { return myHUD; }

    public HashMap<String, Boolean> getCurrentlyActiveKeys() {
        return currentlyActiveKeys;
    }

    public Theme getMyTheme() {
        return myTheme;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        myHUD.updateScore(score);
    }

    // END GETTERS

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
