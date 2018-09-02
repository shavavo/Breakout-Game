package example;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class HUD {
    Group myHUD;

    Text myLevelLabel;
    Text myLivesLabel;
    Text myScoreLabel;

    public HUD(double w, double h, Group root) {
        myHUD = new Group();

        myLevelLabel = new Text("  Level 1");
        myLevelLabel.setFill(Color.WHITE);
        myLevelLabel.setY(h - 10);
        myLevelLabel.setWrappingWidth(w/2);
        myLevelLabel.setTextAlignment(TextAlignment.LEFT);

        myLivesLabel = new Text("Lives: 3  ");
        myLivesLabel.setFill(Color.WHITE);
        myLivesLabel.setY(h - 10);
        myLivesLabel.setX(w/2);
        myLivesLabel.setWrappingWidth(w/2);
        myLivesLabel.setTextAlignment(TextAlignment.RIGHT);

        myScoreLabel = new Text("0");
        myScoreLabel.setFill(Color.WHITE);
        myScoreLabel.setY(20);
        myScoreLabel.setX(0);
        myScoreLabel.setWrappingWidth(w);
        myScoreLabel.setTextAlignment(TextAlignment.CENTER);


        myHUD.getChildren().addAll(myLevelLabel, myLivesLabel, myScoreLabel);

        root.getChildren().add(myHUD);
    }

    public void updateLives(int lives) {
        myLivesLabel.setText("Lives: " + Integer.toString(lives) + "  ");

    }

    public void updateLevel(int level) {
        myLevelLabel.setText("  Level " + Integer.toString(level));

    }

    public void updateScore(int score) {
        myScoreLabel.setText(Integer.toString(score));
    }
}
