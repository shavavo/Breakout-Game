package example;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * A basic example JavaFX program for the first lab.
 * 
 * @author Robert C. Duvall
 */
public class MainGame extends Application {
    public static final String TITLE = "Example JavaFX";
    public static final int SIZE = 500;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.BLACK;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String BOUNCER_IMAGE = "ball.gif";

    public static final Paint MOVER_COLOR = Color.WHITESMOKE;
    public static final int MOVER_SIZE = 75;
    public static final int MOVER_SPEED = 8;

    public int score;



    // some things we need to remember during our game
    private Scene myScene;
    private HUD myHUD;
    private Rectangle myMover;
    private List<Bouncer> myBouncers;
    private List<Block> myBlocks;
    private List<Drop> myDrops;
    private Image bouncerImage;

    private int windowWidth;
    private int windowHeight;

    private HashMap<String, Boolean> currentlyActiveKeys = new HashMap<>();
    private Group root;


    
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

        this.score = 0;


        myDrops = new ArrayList<Drop>();
        myBouncers = new ArrayList<Bouncer>();
        myBouncers.add(new Bouncer(bouncerImage, width/20, height/2, 1, 1, 1, Bouncer.State.LAUNCH,this));
        myMover = new Rectangle(width/2 - MOVER_SIZE/2, .9 * height , MOVER_SIZE, MOVER_SIZE/5);
        myMover.setFill(MOVER_COLOR);


        populateBlocks("level1.txt");

        for(Block block: myBlocks)
            root.getChildren().add(block.getStack());

        root.getChildren().add(myMover);


        myHUD = new HUD(width, height, root);

        // respond to input
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));

        scene.setOnKeyPressed(event -> {
            String codeString = event.getCode().toString();
            if (!currentlyActiveKeys.containsKey(codeString)) {
                currentlyActiveKeys.put(codeString, true);
            }
        });

        scene.setOnKeyReleased(event -> currentlyActiveKeys.remove(event.getCode().toString()));

        return scene;
    }


    List<Bouncer> toRemove;

    // Change properties of shapes to animate them 
    // Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start.
    private void step (double elapsedTime) {
        // update attributes
        toRemove = new ArrayList<Bouncer>();
        for(Bouncer bouncer: myBouncers)
            bouncer.update(elapsedTime);
        myBouncers.removeAll(toRemove);


        for(Drop drop: myDrops)
            drop.update(elapsedTime);


        if( isKeyActive(KeyCode.LEFT) && isKeyActive(KeyCode.RIGHT) ) {
            // Do nothing
        } else if( isKeyActive(KeyCode.LEFT) && myMover.getX() > 0 ) {
            myMover.setX(myMover.getX() - MOVER_SPEED);
        } else if( isKeyActive(KeyCode.RIGHT) && myMover.getX() < myScene.getWidth() - myMover.getWidth() ) {
            myMover.setX(myMover.getX() + MOVER_SPEED);
        } else if( isKeyActive(KeyCode.SPACE) ) {
            myBouncers.get(0).setMyState(Bouncer.State.NORMAL);
        }

        if(myBouncers.size()==0) {
            myBouncers.add(new Bouncer(bouncerImage, windowWidth/20, windowHeight/2, 1, 1, 1, Bouncer.State.LAUNCH,this));
        }
    }

    public void addToRemove(Bouncer bouncer) {
        toRemove.add(bouncer);
    }


    private boolean isKeyActive(KeyCode code) {
        String codeString = code.toString();
        if(currentlyActiveKeys.containsKey(codeString) && currentlyActiveKeys.get(codeString))
            return true;
        return false;
    }




    // What to do each time a key is pressed
    private void handleMouseInput (double x, double y) {

    }

    private void populateBlocks(String fileName) {
        myBlocks = new ArrayList<Block>();

        ArrayList<String> level = readFile(fileName);

        int height = level.size();
        int width = level.get(0).length();



        for(int i=0; i<level.size(); i++) {
            String row = level.get(i);

            int actualJ = 0;
            for(int j=0; j<row.length(); j++) {
                char block = row.charAt(j);

                int health;
                Block.Type type;

                if(block=='?' || block=='+' || block=='-') {
                    j++;
                    health = Character.getNumericValue(row.charAt(j));
                }
                else {
                    health = Character.getNumericValue(block);
                }

                if(block!='o') {
                    if(block=='?')
                        type = Block.Type.RANDOM_DROP;
                    else if(block=='+')
                        type = Block.Type.BALL_SPEED_UP;
                    else if(block=='-')
                        type = Block.Type.BALL_SPEED_DOWN;
                    else
                        type = Block.Type.NORMAL;


                    myBlocks.add(new Block(actualJ*windowWidth/width, i*windowHeight/height, windowWidth/width, windowHeight/height, health, type, this));

                }

                actualJ++;

            }
        }



    }

    private ArrayList<String> readFile(String fileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        ArrayList<String> output = new ArrayList<>();


        try {
            Scanner in = new Scanner(file);

            while(in.hasNext()) {
                output.add(in.next());
            }
            in.close();

        }
        catch (FileNotFoundException e){
            System.out.print("File Not Found");
        }

        return output;
    }

    public void addDrop(int x, int y) {
        myDrops.add(new Drop(Drop.Type.randomType(), x, y, this));

    }

    public List<Bouncer> getMyBouncers() {
        return myBouncers;
    }

    public Rectangle getMyMover() {
        return myMover;
    }

    public Group getRoot() {
        return root;
    }

    private int widthBuffLength = -1;

    private ArrayList<Drop.Type> myActiveBuffs = new ArrayList<>();
    public void powerUp(Drop.Type type) {

        addToScore(500);

        myActiveBuffs.add(type);

        switch(type) {
            case MOVER_SIZE_UP:
                myMover.setWidth(MOVER_SIZE * 1.5);
                myMover.setFill(Color.RED);
                widthBuffLength = 5;
                break;
            case EXTRA_BALL:
                var image = new Image(this.getClass().getClassLoader().getResourceAsStream("ball.gif"));
                myBouncers.add(
                        new Bouncer(
                                image,
                                (int)myMover.getX(),
                                (int)(myMover.getY() - myMover.getHeight()),
                                1,
                                -1,
                                1,
                                Bouncer.State.NORMAL,
                                this
                        )
                );
                break;
            case POWER_BOUNCHER:
                myMover.setFill(Color.YELLOW);

                break;
        }
    }


    public void onBouncerHitMover(Bouncer bouncer) {

        if(myActiveBuffs.contains(Drop.Type.POWER_BOUNCHER)) {
            bouncer.setPowerBouncher(true);
            myActiveBuffs.remove(Drop.Type.POWER_BOUNCHER);
            myMover.setFill(Color.WHITESMOKE);
        } else {
            bouncer.setPowerBouncher(false);

        }

        if(widthBuffLength!=-1) {
            widthBuffLength--;

            if(widthBuffLength==0) {
                myMover.setWidth(MOVER_SIZE);
                myMover.setFill(Color.WHITESMOKE);
                widthBuffLength = -1;
            }
        }

    }

    public void addToScore(int value) {
        score += value;
        myHUD.updateScore(score);
    }

    public Scene getMyScene() {
        return myScene;
    }

    public List<Block> getMyBlocks() {
        return myBlocks;
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}
