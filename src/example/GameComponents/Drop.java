package example.GameComponents;

import example.MainGame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

public class Drop implements MainGame.UpdateableObject {
    public static final int DROP_GRAVITY_SPEED = 100;

    private ImageView myImage;

    private boolean isActive;
    private Type myType;
    private MainGame parentContext;

    public enum Type {
        EXTRA_BALL,
        MOVER_SIZE_UP,
        POWER_BOUNCHER,
        LASER,
        EXTRA_LIFE;

        private static final List<Type> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Type randomType()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    public Drop(Type type, int x, int y, MainGame context) {
        Image image;

        switch(type) {
            case EXTRA_BALL:
                image = new Image("extraballpower.gif");
                break;
            case MOVER_SIZE_UP:
                image = new Image("sizepower.gif");
                break;
            case POWER_BOUNCHER:
                image = new Image("powerbouncerpower.gif");
                break;
            case LASER:
                image = new Image("laserpower.gif");
                break;
            case EXTRA_LIFE:
                image = new Image("heart.png");
                break;
            default:
                image = new Image("extraballpower.gif");

        }

        this.myImage = new ImageView(image);

        if(type==type.EXTRA_LIFE) {
            myImage.setFitWidth(20);
            myImage.setFitHeight(20);
        }


        myImage.setX(x);
        myImage.setY(y);

        this.isActive = true;
        this.myType = type;
        this.parentContext = context;

        parentContext.getRoot().getChildren().add(myImage);
    }

    public boolean update(double elapsedTime) {
        myImage.setY(myImage.getY() + DROP_GRAVITY_SPEED * elapsedTime);

        if(isActive && parentContext.getMyPaddle().getBoundsInParent().intersects(myImage.getBoundsInParent())) {
            parentContext.powerUp(myType);

            parentContext.getRoot().getChildren().remove(myImage);
            isActive = false;

            return true;
        }

        return false;

    }

    public void remove() {
        parentContext.getRoot().getChildren().remove(myImage);
    }
}

