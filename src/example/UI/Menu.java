package example.UI;

import example.MainGame;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;

public class Menu {
    Group menu;

    Text title;
    Text author;

    Text instructions;

    private boolean isMenuVisible;
    private ArrayList<MenuOption> selectionMenu;
    private int selection=0;

    private MainGame parentContext;

    public Menu(int w, int h, MainGame context) {
        this.parentContext = context;
        menu = new Group();

        title = new Text("BREAKOUT");
        title.setFill(Color.WHITE);
        title.setY(h/4);
        title.setX(0);
        title.setWrappingWidth(w);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 24;");

        author = new Text("MADE BY DAVID CHENG");
        author.setFill(Color.WHITE);
        author.setY(h/4 + title.getBoundsInParent().getHeight() + 10 );
        author.setX(0);
        author.setWrappingWidth(w);
        author.setTextAlignment(TextAlignment.CENTER);
        author.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        selectionMenu = new ArrayList<>();
        selectionMenu.add(new MenuOption("PLAY", true, w/6, h/2,  w));
        selectionMenu.add(new MenuOption("INSTRUCTIONS",false, w/6, h/2 + 30, w));
        selectionMenu.add(new MenuOption("SKINS", false,w/6, h/2 + 60, w));

        instructions = new Text(
                "CONTROLS: \nSPACE: launches ball\nLEFT/RIGHT ARROWS: moves paddle\n\nPADDLE POWERUPS:\nPADDLE SIZE+: Increases paddle size for 5 bounces\nPOWER BOUNCER: Upgrades the next bouncer,  hits for 2 HP and goes through blocks if broken\nLASER: Equips paddle with 3 lasers. SPACE to shoot.\nNOTE: The paddle can only hold one power up at a time.\n\nOTHER POWERUPS:\nEXTRA BALL, SPEED +/-, EXTRA LIFE\n\nPRESS SPACE TO RETURN"
        );

        instructions.setY(h/2.75);
        instructions.setX(w/10);
        instructions.setWrappingWidth(8*w/10);
        instructions.setTextAlignment(TextAlignment.LEFT);
        instructions.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 9; -fx-line-spacing: 0.5em;");
        instructions.setFill(Color.WHITE);
        instructions.setVisible(false);


        for(MenuOption option: selectionMenu)
            menu.getChildren().add(option.getTitle());

        menu.getChildren().addAll(title, author, instructions);

        context.getRoot().getChildren().add(menu);
    }

    public void up() {
        selection--;
        if(selection<0)
            selection=selectionMenu.size()-1;
        selection %= selectionMenu.size();
        update();
    }

    public void down() {
        selection++;
        selection %= selectionMenu.size();
        update();
    }

    private void update() {
        for(int i=0; i<selectionMenu.size(); i++) {
            if(selection == i)
                selectionMenu.get(i).setSelected(true);
            else
                selectionMenu.get(i).setSelected(false);
        }
    }

    public void setInstructionsVisibile(boolean x) {
        instructions.setVisible(x);
    }

    public void setMenuVisible(boolean x) {
        isMenuVisible = x;
        for(MenuOption option: selectionMenu)
            option.setVisible(x);
    }

    public int getSelection() {
        return selection;
    }

    public void clear() {
        parentContext.getRoot().getChildren().remove(menu);
    }

    public void show() {
        parentContext.getRoot().getChildren().add(menu);
    }

    public boolean getIsMenuVisibile() {
        return this.isMenuVisible;
    }


}
