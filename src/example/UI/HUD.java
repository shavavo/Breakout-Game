package example.UI;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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

    public HUD(double w, double h, Group root) {
        ClassLoader classLoader = getClass().getClassLoader();
        Font.loadFont(classLoader.getResource("prstartk.TTF").toExternalForm(), 10);

        myHUD = new Group();

        myLevelLabel = new Text();
        myLevelLabel.setFill(Color.WHITE);
        myLevelLabel.setY(h - 10);
        myLevelLabel.setWrappingWidth(w/2);
        myLevelLabel.setTextAlignment(TextAlignment.LEFT);
        myLevelLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        myLivesLabel = new Text();
        myLivesLabel.setFill(Color.WHITE);
        myLivesLabel.setY(h - 10);
        myLivesLabel.setX(w/2);
        myLivesLabel.setWrappingWidth(w/2);
        myLivesLabel.setTextAlignment(TextAlignment.RIGHT);
        myLivesLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        myScoreLabel = new Text("0");
        myScoreLabel.setFill(Color.WHITE);
        myScoreLabel.setY(20);
        myScoreLabel.setX(0);
        myScoreLabel.setWrappingWidth(w);
        myScoreLabel.setTextAlignment(TextAlignment.CENTER);
        myScoreLabel.setStyle("-fx-font-family: \"Press Start K\";");

        myPrimaryLabel = new Text();
        myPrimaryLabel.setFill(Color.WHITE);
        myPrimaryLabel.setY(h*.8);
        myPrimaryLabel.setWrappingWidth(w);
        myPrimaryLabel.setTextAlignment(TextAlignment.CENTER);
        myPrimaryLabel.setStyle("-fx-font-size: 24; -fx-font-family: \"Press Start K\";");

        mySecondaryLabel = new Text();
        mySecondaryLabel.setFill(Color.WHITE);
        mySecondaryLabel.setY(h*.8 + myPrimaryLabel.getBoundsInParent().getHeight() + 10);
        mySecondaryLabel.setWrappingWidth(w);
        mySecondaryLabel.setTextAlignment(TextAlignment.CENTER);
        mySecondaryLabel.setStyle("-fx-font-size: 9; -fx-font-family: \"Press Start K\";");

        myNewLabel = new Text();
        myNewLabel.setFill(Color.WHITE);
        myNewLabel.setY(h - 10);
        myNewLabel.setX(0);
        myNewLabel.setWrappingWidth(w);
        myNewLabel.setTextAlignment(TextAlignment.CENTER);
        myNewLabel.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 12;");


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
}
