package example.GameComponents;

import example.MainGame;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bouncer implements MainGame.UpdateableObject {
    public enum State {
        NORMAL,
        LAUNCH;
    }

    private ImageView myImage;

    private State state;
    private int bouncer_speed;
    private double myBouncerSize;
    private double myXDirection;
    private double myYDirection;

    private boolean powerBouncer = false;
    private boolean recentlyHitBouncer = false;
    private boolean recentlyHitTop = false;
    private boolean free = true;


    private MainGame parentContext;


    public Bouncer(Image image, int x, int y, int xDir, int yDir,  double scale, State type, int speed, MainGame parentContext) {
        this.myImage = new ImageView(image);
        this.myImage.setX(x);
        this.myImage.setY(y);
        this.myImage.setScaleX(scale);
        this.myImage.setScaleY(scale);

        this.bouncer_speed = speed;
        this.parentContext = parentContext;
        this.state = type;
        this.myXDirection = xDir;
        this.myYDirection = yDir;

        this.myBouncerSize = myImage.boundsInParentProperty().get().getWidth();

        parentContext.getRoot().getChildren().add(myImage);
    }

    public void reverseX() {
        myXDirection *= -1;
    }

    public void reverseY() {
        myYDirection *= -1;
    }

    public boolean update(double elapsedTime) {
        if(state==State.NORMAL) {
            myImage.setX(myImage.getX() + bouncer_speed * myXDirection * elapsedTime);
            myImage.setY(myImage.getY() + bouncer_speed * myYDirection * elapsedTime);
        }
        // Follow paddle until launched
        else if(state==State.LAUNCH) {
            myImage.setX(parentContext.getMyPaddle().getX() + parentContext.getMyPaddle().getWidth()/2 - myBouncerSize/2);
            myImage.setY(parentContext.getMyPaddle().getY() - parentContext.getMyPaddle().getHeight() );
        }

        if(powerBouncer) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(-.8);
            myImage.setEffect(colorAdjust);
        }

        // Walls
        if(myImage.getX()  >= parentContext.getMyScene().getWidth() - myBouncerSize/2 ||  myImage.getX() <= 0 ) {
            myXDirection *= -1;
            recentlyHitBouncer = false;
        }
        else if( recentlyHitTop==false && myImage.getY() <= 0 ) {
            myYDirection *= -1;
            recentlyHitBouncer = false;
            recentlyHitTop = true;
        }
        // Bouncer hits mover
        else if(recentlyHitBouncer==false && parentContext.getMyPaddle().getBoundsInParent().intersects(myImage.getBoundsInParent())) {
            // Hit on top
            if(parentContext.getMyPaddle().getY() > myImage.getY())
                reverseY();
            // Hit on side
            else if(myImage.getX() < parentContext.getMyPaddle().getX()  || parentContext.getMyPaddle().getX() + parentContext.getMyPaddle().getWidth() < myImage.getX())
                reverseX();

            parentContext.onBouncerHitPaddle(this);

            recentlyHitBouncer = true;
            recentlyHitTop = false;
        // bouncer goes off bottom
        } else if(myImage.getY()>parentContext.getMyPaddle().getY() + parentContext.getMyPaddle().getHeight()) {
            remove();
            return true;
        }


        // Check for hit on block
        // free variable is to avoid double hit: bouncer cannot hit another block until it is not touching anything
        for (Block block : parentContext.getMyBlocks()) {
            if (free && block.isActive() && block.getStack().getBoundsInParent().intersects(myImage.getBoundsInParent())) {
                block.collide(this, parentContext.getRoot());
                recentlyHitBouncer = false;
                recentlyHitTop = false;
                free = false;
                break;
            }

            free = true;
        }

        return false;
    }

    public void remove() {
        parentContext.getRoot().getChildren().remove(myImage);
    }

    public void setMyState(State state) {
        this.state = state;
    }

    public void setPowerBouncher(boolean powerBouncer) {
        this.powerBouncer = powerBouncer;

        if(powerBouncer==false) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            myImage.setEffect(colorAdjust);
        }
    }


    public void changeSpeedBy(int x) {
        bouncer_speed += x;
        if(bouncer_speed <= 0)
            bouncer_speed = 50;
    }

    public State getMyState() { return state; }

    public boolean getPowerBouncer() { return this.powerBouncer; }

    public ImageView getMyImage() { return myImage; }
}
