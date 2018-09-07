package example;

import example.GameComponents.*;
import example.UI.HUD;
import example.UI.Menu;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A basic example JavaFX program for the first lab.
 * 
 * @author Robert C. Duvall
 */
public class MainGame extends Application {
    public static final String TITLE = "BREAKOUT";
    public static final int SIZE = 500;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.BLACK;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String BOUNCER_IMAGE = "ball.gif";

    public static final Paint MOVER_COLOR = Color.WHITESMOKE;
    public static final int MOVER_SIZE = 75;
    public static final int MOVER_SPEED = 6;



    public int score;
    public int lives;
    public GameState gameState;


    // some things we need to remember during our game
    private Scene myScene;
    private HUD myHUD;
    private Menu myMenu;
    private Rectangle myMover;
    private List<Bouncer> myBouncers;
    private List<Block> myBlocks;
    private List<Drop> myDrops;
    private List<Laser> myLasers;
    private Drop.Type myActiveBouncerBuff;

    private Image bouncerImage;

    private int windowWidth;
    private int windowHeight;

    private HashMap<String, Boolean> currentlyActiveKeys = new HashMap<>();
    private Group root;

    private ArrayList<Level> myLevels;

    public int currentLevelNumber;
    public Level currentLevel;

    private int widthBuffLength = -1;
    private int lasersLeft = 0;

    public enum GameState {
        WELCOME,
        PLAYING,
        INTERMISSION,
        GAME_OVER,
        WON
    }

    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        // attach scene to the stage and display it
        myScene = setupGame( (int)(.8*SIZE) , SIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();



        myLevels = new ArrayList<>();
        myLevels.add(new Level(1, 3, "level1.txt", 200, this));
        myLevels.add(new Level(2, 3, "level2.txt", 225, this));
        myLevels.add(new Level(3, 3, "level3.txt", 250, this));

        this.currentLevelNumber = 0;
        this.score = 0;

        myBlocks = new ArrayList<>();
        myLasers = new ArrayList<>();

        gameState = GameState.WELCOME;
//        loadLevel(currentLevelNumber);

        // attach "game loop" to timeline to play it
        var frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        var animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    // Create the game's "scene": what shapes will be in the game and their starting properties
    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        root = new Group();
        // create a place to see the shapes
        var scene = new Scene(root, width, height, background);
        // make some shapes and set their properties
        bouncerImage = new Image(this.getClass().getClassLoader().getResourceAsStream(BOUNCER_IMAGE));

        this.windowWidth = width;
        this.windowHeight = height;

        myMenu = new Menu(width, height, this);

        myDrops = new ArrayList<Drop>();
        myBouncers = new ArrayList<Bouncer>();

        myMover = new Rectangle(width/2 - MOVER_SIZE/2, .9 * height , MOVER_SIZE, MOVER_SIZE/5);
        myMover.setFill(MOVER_COLOR);
        root.getChildren().add(myMover);

        myHUD = new HUD(width, height, root);

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

    private void loadLevel(int levelNumber) {
        gameState = GameState.PLAYING;

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


    // Change properties of shapes to animate them
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start.
    private void step (double elapsedTime) {
        if(gameState==GameState.WELCOME) {
            if(isKeyActive(KeyCode.UP)) {
                myMenu.up();
                currentlyActiveKeys.remove(KeyCode.UP.toString());
            } else if(isKeyActive(KeyCode.DOWN)) {
                myMenu.down();
                currentlyActiveKeys.remove(KeyCode.DOWN.toString());
            } else if(isKeyActive(KeyCode.ENTER) || isKeyActive(KeyCode.SPACE)) {
                switch(myMenu.getSelection()) {
                    case 0:
                        loadLevel(currentLevelNumber);
                        myMenu.clear();
                        break;
                    case 1:
                        if(myMenu.getIsMenuVisibile()) {
                            myMenu.setInstructionsVisibile(true);
                            myMenu.setMenuVisible(false);
                        } else {
                            myMenu.setInstructionsVisibile(false);
                            myMenu.setMenuVisible(true);
                        }
                        break;
                }

                currentlyActiveKeys.remove(KeyCode.ENTER.toString());
                currentlyActiveKeys.remove(KeyCode.SPACE.toString());
            }
        }
        else if(gameState==GameState.PLAYING) {
            // update attributes
            List<Bouncer> toRemove = new ArrayList<>();
            for(Bouncer bouncer : myBouncers) {
                boolean shouldRemove = bouncer.update(elapsedTime);
                if(shouldRemove) toRemove.add(bouncer);
            }
            myBouncers.removeAll(toRemove);

            List<Drop> toRemoveDrops = new ArrayList<>();
            for (Drop drop: myDrops) {
                boolean shouldRemove = drop.update(elapsedTime);
                if(shouldRemove) toRemoveDrops.add(drop);
            }
            myDrops.removeAll(toRemoveDrops);

            List<Laser> toRemoveLasers = new ArrayList<>();
            for (Laser laser: myLasers) {
                boolean shouldRemove = laser.update(elapsedTime);
                if(shouldRemove) toRemoveLasers.add(laser);
            }
            myLasers.removeAll(toRemoveLasers);


            // Handle key presses
            if (isKeyActive(KeyCode.LEFT) && isKeyActive(KeyCode.RIGHT)) {
                // Do nothing
            } else if (isKeyActive(KeyCode.LEFT) && myMover.getX() > 0) {
                myMover.setX(myMover.getX() - MOVER_SPEED);
            } else if (isKeyActive(KeyCode.RIGHT) && myMover.getX() < myScene.getWidth() - myMover.getWidth()) {
                myMover.setX(myMover.getX() + MOVER_SPEED);
            } else if (isKeyActive(KeyCode.SPACE)) {
                if(myBouncers.get(0).getMyState() == Bouncer.State.LAUNCH)
                    myBouncers.get(0).setMyState(Bouncer.State.NORMAL);
                else if(myActiveBouncerBuff == Drop.Type.LASER) {
                    shootLaser();
                }
                currentlyActiveKeys.remove(KeyCode.SPACE.toString());
            } else if (isKeyActive(KeyCode.PERIOD)) {


                for(Block block: myBlocks)
                    root.getChildren().remove(block.getStack());
                myBlocks.clear();

                currentlyActiveKeys.remove(KeyCode.PERIOD.toString());
            } else if (isKeyActive(KeyCode.COMMA)) {
                currentLevelNumber--;
                loadLevel(currentLevelNumber);
                currentlyActiveKeys.remove(KeyCode.COMMA.toString());
            } else if (isKeyActive(KeyCode.EQUALS)) {
                myHUD.fadeNewLabel("BALL SPEED+");
                for(Bouncer bouncer: myBouncers)
                    bouncer.changeSpeedBy(10);
            } else if (isKeyActive(KeyCode.MINUS)) {
                myHUD.fadeNewLabel("BALL SPEED-");
                for(Bouncer bouncer: myBouncers)
                    bouncer.changeSpeedBy(-10);
            }

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
                myHUD.updatePrimaryLabel("");
                myHUD.updateSecondaryLabel("");
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


    private boolean isKeyActive(KeyCode code) {
        String codeString = code.toString();
        if(currentlyActiveKeys.containsKey(codeString) && currentlyActiveKeys.get(codeString))
            return true;
        return false;
    }


    public void powerUp(Drop.Type type) {
        addToScore(500);

        switch(type) {
            case MOVER_SIZE_UP:
                myActiveBouncerBuff = type;
                myMover.setWidth(MOVER_SIZE * 1.5);
                widthBuffLength = 5;
                myHUD.fadeNewLabel("PADDLE SIZE+");
                break;
            case EXTRA_BALL:
                var image = new Image(this.getClass().getClassLoader().getResourceAsStream("ball.gif"));
                myHUD.fadeNewLabel("EXTRA BALL");
                myBouncers.add(
                        new Bouncer(
                                image,
                                (int)(myMover.getX() + myMover.getWidth()/2),
                                (int)(myMover.getY() - myMover.getHeight()),
                                1,
                                -1,
                                1,
                                Bouncer.State.NORMAL,
                                currentLevel.getDefaultBouncerSpeed(),
                                this
                        )
                );
                break;
            case POWER_BOUNCHER:
                myHUD.fadeNewLabel("POWER BALL");
                myActiveBouncerBuff = type;
                myMover.setWidth(MOVER_SIZE);
                break;
            case LASER:
                myHUD.fadeNewLabel("LASERS");
                myActiveBouncerBuff = type;
                myMover.setWidth(MOVER_SIZE);
                lasersLeft = 3;
                break;
        }

        updateMoverColor();
    }

    public void onBouncerHitMover(Bouncer bouncer) {
        if(myActiveBouncerBuff == Drop.Type.POWER_BOUNCHER) {
            bouncer.setPowerBouncher(true);
            myActiveBouncerBuff = null;
            updateMoverColor();
        } else {
            bouncer.setPowerBouncher(false);
        }

        if(widthBuffLength!=-1) {
            widthBuffLength--;

            if(widthBuffLength==0) {
                myMover.setWidth(MOVER_SIZE);
                myActiveBouncerBuff = null;
                updateMoverColor();
                widthBuffLength = -1;
            }
        }
    }

    public void updateMoverColor() {
        // Power bouncer takes priority
        if(myActiveBouncerBuff == Drop.Type.POWER_BOUNCHER)
            myMover.setFill(Color.YELLOW);
        else if(myActiveBouncerBuff == Drop.Type.MOVER_SIZE_UP)
            myMover.setFill(Color.RED);
        else if(myActiveBouncerBuff == Drop.Type.LASER)
            myMover.setFill(Color.DARKBLUE);
        else
            myMover.setFill(Color.WHITESMOKE);

    }

    private void shootLaser() {
        myLasers.add(
                new Laser(myMover.getX() + myMover.getWidth()/2, myMover.getY() - windowHeight*.05, windowWidth*.01, windowHeight*.05, this)
        );

        lasersLeft--;

        if(lasersLeft==0) {
            myActiveBouncerBuff = null;
            updateMoverColor();
        }

    }

    public void addToScore(int value) {
        score += value;
        myHUD.updateScore(score);
    }

    // GETTERS

    public Scene getMyScene() { return myScene; }

    public List<Block> getMyBlocks() { return myBlocks; }

    public Rectangle getMyMover() { return myMover; }

    public List<Drop> getMyDrops() { return myDrops; }

    public Group getRoot() { return root; }

    public HUD getMyHUD() { return myHUD; }

    // END GETTERS

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
