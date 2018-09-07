package example.UI;

import javafx.scene.paint.Color;

public class Theme {
    private Color[] blockColors;
    private Color paddleColor;
    private Color bgColor;
    private Color textColor;

    public Theme(Color blockColor1, Color blockColor2, Color blockColor3, Color paddleColor, Color bgColor, Color textColor) {
        blockColors = new Color[]{blockColor1, blockColor2, blockColor3};
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.paddleColor = paddleColor;
    }

    public Color[] getBlockColors() {
        return blockColors;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getPaddleColor() {
        return paddleColor;
    }
}
