package example;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class Bouncer {
    public enum State {
        NORMAL,
        LAUNCH;
    }

    State myState;

    public int BOUNCER_SPEED = 200;

    private ImageView myImage;

    private double myBouncerSize;
    private double myXDirection;
    private double myYDirection;

    private boolean powerBouncher;
    private boolean recentlyHitBouncer;

    private ExampleBounce parentContext;


    public Bouncer(Image image, int x, int y, int xDir, int yDir,  double scale, State type, ExampleBounce parentContext) {

        this.myImage = new ImageView(image);
        this.myImage.setX(x);
        this.myImage.setY(y);
        this.myImage.setScaleX(scale);
        this.myImage.setScaleY(scale);

        this.parentContext = parentContext;
        this.powerBouncher = false;
        this.recentlyHitBouncer = false;
        this.myState = type;


        this.myXDirection = xDir;
        this.myYDirection = yDir;

        this.myBouncerSize = myImage.boundsInParentProperty().get().getWidth();

        parentContext.getRoot().getChildren().add(myImage);

    }

    public ImageView getMyImage() {
        return myImage;
    }


    public void reverseX() {
        myXDirection *= -1;
    }

    public void reverseY() {
        myYDirection *= -1;
    }


    public void update(double elapsedTime) {
        if(myState==State.NORMAL) {
            myImage.setX(myImage.getX() + BOUNCER_SPEED * myXDirection * elapsedTime);
            myImage.setY(myImage.getY() + BOUNCER_SPEED * myYDirection * elapsedTime);
        } else if(myState==State.LAUNCH) {
            myImage.setX(parentContext.getMyMover().getX() + parentContext.getMyMover().getWidth()/2 );
            myImage.setY(parentContext.getMyMover().getY() - parentContext.getMyMover().getHeight() );
        }


        if(powerBouncher) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(-.8);
            myImage.setEffect(colorAdjust);
        }


        // Walls
        if(myImage.getX()  >= parentContext.getMyScene().getWidth() - myBouncerSize/2 ||  myImage.getX() <= 0 ) {
            myXDirection *= -1;
            recentlyHitBouncer = false;
        }
        else if( myImage.getY() <= 0 ) {
            myYDirection *= -1;
            recentlyHitBouncer = false;
        }
        // Bouncer hits mover
        else if(recentlyHitBouncer==false && parentContext.getMyMover().getBoundsInParent().intersects(myImage.getBoundsInParent())) {

            // Hit on top
            if(parentContext.getMyMover().getY() > myImage.getY())
                myYDirection *= -1;
            // Hit on side
            else if(myImage.getX() < parentContext.getMyMover().getX()  || parentContext.getMyMover().getX() + parentContext.getMyMover().getWidth() < myImage.getX())
                myXDirection *= -1;

            parentContext.onBouncerHitMover(this);


            recentlyHitBouncer = true;
        } else if(myImage.getY()>parentContext.getMyMover().getY() + parentContext.getMyMover().getHeight()) {
            parentContext.getRoot().getChildren().remove(myImage);
            parentContext.addToRemove(this);
        }

        // Check for hit on block
        for(Block block: parentContext.getMyBlocks()) {
            if(block.isActive() &&  block.getStack().getBoundsInParent().intersects(myImage.getBoundsInParent())) {
                block.collide(myImage.getX(), myImage.getY(), this, parentContext.getRoot());
                recentlyHitBouncer = false;
                break;
            }

        }




    }

    public void setMyState(State myState) {
        this.myState = myState;
    }

    public void setPowerBouncher(boolean powerBouncher) {
        this.powerBouncher = powerBouncher;

        if(powerBouncher==false) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            myImage.setEffect(colorAdjust);
        }
    }

    public boolean getPowerBouncer() {
        return this.powerBouncher;
    }


    public void changeSpeedBy(int x) {
        BOUNCER_SPEED += x;
    }
}
