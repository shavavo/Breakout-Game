package example.GameComponents;

import example.GameComponents.Block;
import example.MainGame;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Laser implements MainGame.UpdateableObject {
    private static final int laserSpeed = -400;

    private Rectangle laser;
    private MainGame parentContext;

    public Laser(double x, double y, double w, double h, MainGame parentContext) {
        laser = new Rectangle(x, y, w, h);
        laser.setFill(Color.DARKBLUE);
        this.parentContext = parentContext;
        parentContext.getRoot().getChildren().add(laser);
    }

    public boolean update(double elapsedTime) {
        laser.setY(laser.getY() + laserSpeed  * elapsedTime);

        if(laser.getY() + laser.getHeight() < 0)
            return true;

        for (Block block : parentContext.getMyBlocks()) {
            if (block.getStack().getBoundsInParent().intersects(laser.getBoundsInParent())) {
                block.breakBlock(null);
                parentContext.getRoot().getChildren().remove(laser);
                return true;
            }
        }

        return false;
    }
}
