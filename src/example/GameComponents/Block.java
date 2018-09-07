package example.GameComponents;

import example.MainGame;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
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

        String rgb = "rgb(" + Double.toString(context.getMyTheme().getBgColor().getRed()*255)  + ", "
                + Double.toString(context.getMyTheme().getBgColor().getGreen()*255) + ", "
                + Double.toString(context.getMyTheme().getBgColor().getBlue()*255) + ")";
        this.myRectangle.setStyle("-fx-stroke: " + rgb + ";");
        this.active = true;
        this.blockType = type;

        Text text = new Text("");

        if(type==Type.RANDOM_DROP)
            text.setText("?");
        else if(type==Type.BALL_SPEED_DOWN)
            text.setText("-");
        else if(type==Type.BALL_SPEED_UP)
            text.setText("+");

        stack = new StackPane();
        stack.setLayoutX(x);
        stack.setLayoutY(y);
        stack.getChildren().addAll(myRectangle, text);
    }

    public void updateColor() {
        if(myHealth==3) this.myRectangle.setFill(parentContext.getMyTheme().getBlockColors()[2]);
        else if(myHealth==2) this.myRectangle.setFill(parentContext.getMyTheme().getBlockColors()[1]);
        else if(myHealth==1) this.myRectangle.setFill(parentContext.getMyTheme().getBlockColors()[0]);
    }

    public void collide(Bouncer bouncer, Group root) {
        parentContext.addToScore(50);

        if(bouncer.getPowerBouncer())
            myHealth -= 2;
        else
            myHealth--;

        // Block is broken
        if(myHealth<=0)
            breakBlock(bouncer);
        else
            updateColor();

        if( !bouncer.getPowerBouncer() || (bouncer.getPowerBouncer() && myHealth>0) ) {
            double bouncerY = bouncer.getMyImage().getBoundsInParent().getMinY() + bouncer.getMyImage().getBoundsInParent().getHeight()/2;

            //Hit was from above or below the brick
            if ( bouncerY >= stack.getBoundsInParent().getMaxY() || bouncerY <= stack.getBoundsInParent().getMinY() )
                bouncer.reverseY();
            else
                bouncer.reverseX();
        }
    }

    public void breakBlock(Bouncer bouncer) {
        parentContext.addToScore(100);
        parentContext.getRoot().getChildren().remove(stack);
        parentContext.getMyBlocks().remove(this);

        active = false;

        // Add a random drop
        if (blockType == Type.RANDOM_DROP) {
            int x = (int) (stack.getLayoutX() + stack.getWidth() / 2);
            int y = (int) stack.getLayoutY();
            parentContext.getMyDrops().add(new Drop(Drop.Type.randomType(), x, y, parentContext));
        }
        // Call is from cheat keys, no specific bouncer
        else if(bouncer!=null) {
            if (blockType == Type.BALL_SPEED_DOWN) {
                parentContext.getMyHUD().fadeNewLabel("BALL SPEED-");
                bouncer.changeSpeedBy(-25);
            }
            else if(blockType==Type.BALL_SPEED_UP) {
                parentContext.getMyHUD().fadeNewLabel("BALL SPEED+");
                bouncer.changeSpeedBy(25);
            }
        }
    }

    public Boolean isActive() { return active; }

    public StackPane getStack() { return stack; }
}
