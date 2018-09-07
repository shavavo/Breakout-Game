package example.UI;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MenuOption {
    private Text title;

    private String text;
    private boolean selected = false;

    public MenuOption(String text, boolean selected, double x, double y, double w, Color textColor) {
        this.selected = selected;
        this.text = text;

        title = new Text();
        title.setFill(textColor);
        title.setY(y);
        title.setX(x);
        title.setWrappingWidth(w);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 16;");

        update();
    }

    private void update() {
        if(selected)
            title.setText("> " + text);
        else
            title.setText("  " + text);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        update();
    }

    public void setVisible(boolean x) {
        title.setVisible(x);
    }

    public Text getTitle() {
        return title;
    }

    public void updateColor(Color textColor) {
        title.setFill(textColor);
    }

    public String getText() {
        return title.getText();
    }

    public void setText(String text) {
        title.setText(text);
        this.text = text;
    }

}
