package example;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Block {
    public enum Type {
        NORMAL,
        RANDOM_DROP,
        BALL_SPEED_UP,
        BALL_SPEED_DOWN;
    }

    private Rectangle myRectangle;
    private int myHealth;
    private Boolean active;
    private StackPane stack;

    private Type blockType;

    private MainGame parentContext;


    public Block(int x, int y, int w, int h, int health, Type type, MainGame context) {
        this.myRectangle = new Rectangle(x, y, w, h);

        this.parentContext = context;
        this.myHealth = health;
        this.updateColor();

        this.myRectangle.setStyle("-fx-stroke: black;");

        this.active = true;
        this.blockType = type;

        Text text = new Text("");

        if(type==Type.RANDOM_DROP) {
            text.setText("?");
        } else if(type==Type.BALL_SPEED_DOWN) {
            text.setText("-");
        } else if(type==Type.BALL_SPEED_UP) {
            text.setText("+");
        }


        stack = new StackPane();
        stack.setLayoutX(x);
        stack.setLayoutY(y);

        stack.getChildren().addAll(myRectangle, text);


    }

    public Rectangle getMyRectangle() {
        return myRectangle;
    }

    public void updateColor() {
        if(myHealth==3) this.myRectangle.setFill(Color.DARKRED);
        else if(myHealth==2) this.myRectangle.setFill(Color.YELLOW);
        else if(myHealth==1) this.myRectangle.setFill(Color.GREEN);
    }

    public void collide(double bouncerX, double bouncerY, Bouncer bouncer, Group root) {
        parentContext.addToScore(50);

        if(bouncer.getPowerBouncer())
            myHealth = 0;
        else
            myHealth--;

        // Block is broken
        if(myHealth==0) {
            parentContext.addToScore(100);

            root.getChildren().remove(stack);
            parentContext.getMyBlocks().remove(this);

            active = false;

            if(blockType==Type.RANDOM_DROP)
                parentContext.addDrop((int)(stack.getLayoutX() + stack.getWidth()/2), (int)stack.getLayoutY());
            else if(blockType==Type.BALL_SPEED_DOWN)
                bouncer.changeSpeedBy(-50);
            else if(blockType==Type.BALL_SPEED_UP)
                bouncer.changeSpeedBy(50);



        } else {
            updateColor();
        }

        if(!bouncer.getPowerBouncer()) {
            //Hit was from below the brick
            if (bouncerY <= stack.getLayoutY() - (stack.getHeight() / 2))
                bouncer.reverseY();

                //Hit was from above the brick
            else if (bouncerY >= stack.getLayoutY() + (stack.getHeight() / 2))
                bouncer.reverseY();

                //Hit was on left
            else if (bouncerX < stack.getLayoutX())
                bouncer.reverseX();

                //Hit was on right
            else if (bouncerX > stack.getLayoutX())
                bouncer.reverseX();
        }

    }

    public Boolean isActive() {
        return active;
    }

    public StackPane getStack() {
        return stack;
    }


}
