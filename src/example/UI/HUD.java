package example.UI;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class HUD {
    Group myHUD;

    Text myLevelLabel;
    Text myLivesLabel;
    Text myScoreLabel;
    Text myPrimaryLabel;
    Text mySecondaryLabel;
    Text myNewLabel;

    public HUD(double w, double h, Color textColor, Group root) {


        myHUD = new Group();

        myLevelLabel = new Text();
        myLevelLabel.setY(h - 10);
        myLevelLabel.setWrappingWidth(w/2);
        myLevelLabel.setTextAlignment(TextAlignment.LEFT);
        myLevelLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        myLivesLabel = new Text();
        myLivesLabel.setY(h - 10);
        myLivesLabel.setX(w/2);
        myLivesLabel.setWrappingWidth(w/2);
        myLivesLabel.setTextAlignment(TextAlignment.RIGHT);
        myLivesLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        myScoreLabel = new Text("0");
        myScoreLabel.setY(20);
        myScoreLabel.setX(0);
        myScoreLabel.setWrappingWidth(w);
        myScoreLabel.setTextAlignment(TextAlignment.CENTER);
        myScoreLabel.setStyle("-fx-font-family: \"Press Start K\";");

        myPrimaryLabel = new Text();
        myPrimaryLabel.setY(h*.8);
        myPrimaryLabel.setWrappingWidth(w);
        myPrimaryLabel.setTextAlignment(TextAlignment.CENTER);
        myPrimaryLabel.setStyle("-fx-font-size: 24; -fx-font-family: \"Press Start K\";");

        mySecondaryLabel = new Text();
        mySecondaryLabel.setY(h*.8 + myPrimaryLabel.getBoundsInParent().getHeight() + 10);
        mySecondaryLabel.setWrappingWidth(w);
        mySecondaryLabel.setTextAlignment(TextAlignment.CENTER);
        mySecondaryLabel.setStyle("-fx-font-size: 9; -fx-font-family: \"Press Start K\";");

        myNewLabel = new Text();
        myNewLabel.setY(h - 10);
        myNewLabel.setX(0);
        myNewLabel.setWrappingWidth(w);
        myNewLabel.setTextAlignment(TextAlignment.CENTER);
        myNewLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 12;");

        updateColor(textColor);


        myHUD.getChildren().addAll(myLevelLabel, myLivesLabel, myScoreLabel, myPrimaryLabel, mySecondaryLabel, myNewLabel);

        root.getChildren().add(myHUD);
    }

    public void updateLives(int lives) {
        myLivesLabel.setText("LIVES: " + Integer.toString(lives) + " ");

    }

    public void updateLevel(int level) {
        myLevelLabel.setText(" LEVEL " + Integer.toString(level));

    }

    public void updateScore(int score) {
        myScoreLabel.setText(Integer.toString(score));
    }

    public void updatePrimaryLabel(String message) {
        myPrimaryLabel.setText(message);
    }

    public void updateSecondaryLabel(String message) {
        mySecondaryLabel.setText(message);

    }

    public void fadeNewLabel(String message) {
        myNewLabel.setText(message);

        FadeTransition ft = new FadeTransition(Duration.millis(2000), myNewLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.play();
    }

    public void updateColor(Color textColor) {
        myLevelLabel.setFill(textColor);
        myLivesLabel.setFill(textColor);
        myScoreLabel.setFill(textColor);
        myPrimaryLabel.setFill(textColor);
        mySecondaryLabel.setFill(textColor);
        myNewLabel.setFill(textColor);
    }

    public void clear() {
        myLevelLabel.setText("");
        myLivesLabel.setText("");
        myPrimaryLabel.setText("");
        mySecondaryLabel.setText("");
    }
}
