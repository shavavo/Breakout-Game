package example.UI;

import example.MainGame;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Menu {
    public static final Theme[] themes = new Theme[]{
            new Theme(Color.GREEN,
                    Color.YELLOW,
                    Color.DARKRED,
                    Color.WHITESMOKE,
                    Color.BLACK,
                    Color.WHITE
            ),
            new Theme(
                    Color.color(180/255.0,180/255.0,180/255.0),
                    Color.color(123/255.0, 94/255.0, 123/255.0),
                    Color.color(95/255.0, 15/255.0, 64/255.0),
                    Color.BLACK,
                    Color.WHITE,
                    Color.PURPLE
            ),
            new Theme(
                    Color.color(144/255.0,215/255.0,255/255.0),
                    Color.color(107/255.0, 127/255.0, 215/255.0),
                    Color.color(4/255.0, 53/255.0, 101/255.0),
                    Color.BLACK,
                    Color.WHITE,
                    Color.color(144/255.0,215/255.0,255/255.0)
            ),
            new Theme(
                    Color.color(255/255.0,234/255.0,208/255.0),
                    Color.color(72/255.0, 169/255.0, 166/255.0),
                    Color.color(49/255.0, 86/255.0, 89/255.0),
                    Color.BLACK,
                    Color.color(228/255.0, 223/255.0, 218/255.0),
                    Color.BLACK
            ),
            new Theme(
                    Color.color(243/255.0,184/255.0,141/255.0),
                    Color.color(143/255.0, 61/255.0, 75/255.0),
                    Color.color(43/255.0, 29/255.0, 66/255.0),
                    Color.BLACK,
                    Color.color(252/255.0, 236/255.0, 203/255.0),
                    Color.BLACK
            ),
    };


    Group menu;

    Text title;
    Text author;

    Text instructions;

    HashSet<Integer> purchasedSkinsIndex = new HashSet<>();

    private boolean isMenuVisible=true;
    private boolean isSkinsMenuVisible=false;
    private ArrayList<MenuOption> selectionMenu;
    private ArrayList<MenuOption> skinsMenu;
    private int selection=0;
    private int skinsSelection=0;

    private MainGame parentContext;

    public Menu(int w, int h, Color textColor, MainGame context) {
        this.parentContext = context;
        menu = new Group();

        title = new Text("BREAKOUT");
        title.setY(h/4);
        title.setX(0);
        title.setWrappingWidth(w);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 24;");

        author = new Text("MADE BY DAVID CHENG");
        author.setY(h/4 + title.getBoundsInParent().getHeight() + 10 );
        author.setX(0);
        author.setWrappingWidth(w);
        author.setTextAlignment(TextAlignment.CENTER);
        author.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 10;");

        selectionMenu = new ArrayList<>();
        selectionMenu.add(new MenuOption("PLAY", true, w/6, h/2,  w, textColor));
        selectionMenu.add(new MenuOption("INSTRUCTIONS",false, w/6, h/2 + 30, w, textColor));
        selectionMenu.add(new MenuOption("SKINS", false,w/6, h/2 + 60, w, textColor));

        skinsMenu = new ArrayList<>();
        skinsMenu.add(new MenuOption("ARCADE", true, w/6, h/2,  w, textColor));
        skinsMenu.add(new MenuOption("GRAPE (5000)",false, w/6, h/2 + 30, w, textColor));
        skinsMenu.add(new MenuOption("CLOUD (5000)", false,w/6, h/2 + 60, w, textColor));
        skinsMenu.add(new MenuOption("VINTAGE (5000)", false,w/6, h/2 + 90, w, textColor));
        skinsMenu.add(new MenuOption("SUNSET (5000)", false,w/6, h/2 + 120, w, textColor));

        instructions = new Text(
                "CONTROLS: \nSPACE: launches ball\nLEFT/RIGHT ARROWS: moves paddle\n\nPADDLE POWERUPS:\nPADDLE SIZE+: Increases paddle size for 5 bounces\nPOWER BOUNCER: Upgrades the next bouncer,  hits for 2 HP and goes through blocks if broken\nLASER: Equips paddle with 3 lasers. SPACE to shoot.\nNOTE: The paddle can only hold one power up at a time.\n\nOTHER POWERUPS:\nEXTRA BALL, SPEED +/-, EXTRA LIFE\n\nPRESS SPACE TO RETURN"
        );
        instructions.setY(h/2.75);
        instructions.setX(w/10);
        instructions.setWrappingWidth(8*w/10);
        instructions.setTextAlignment(TextAlignment.LEFT);
        instructions.setStyle("-fx-font-family: \"Press Start K\";-fx-font-size: 9; -fx-line-spacing: 0.5em;");
        instructions.setVisible(false);

        updateColor(textColor);

        for(MenuOption option: selectionMenu)
            menu.getChildren().add(option.getTitle());

        for(MenuOption option: skinsMenu)
            menu.getChildren().add(option.getTitle());

        setSkinsMenuVisible(false);

        menu.getChildren().addAll(title, author, instructions);

        context.getRoot().getChildren().add(menu);

        purchasedSkinsIndex.add(0);
    }

    public int up(ArrayList<MenuOption> menu, int selected) {
        selected--;
        if (selected < 0)
            selected = menu.size() - 1;
        selected %= menu.size();
        update(menu, selected);

        return selected;
    }

    public int down(ArrayList<MenuOption> menu, int selected) {
        selected++;
        selected %= menu.size();
        update(menu, selected);

        return selected;
    }

    private void update(ArrayList<MenuOption> menu, int selected) {
        for(int i=0; i<menu.size(); i++) {
            if(selected == i)
                menu.get(i).setSelected(true);
            else
                menu.get(i).setSelected(false);
        }
    }

    public void setInstructionsVisible(boolean x) {
        instructions.setVisible(x);
    }

    public void setMenuVisible(boolean x) {
        isMenuVisible = x;
        for(MenuOption option: selectionMenu)
            option.setVisible(x);
    }

    public void setSkinsMenuVisible(boolean x) {
        isSkinsMenuVisible = x;
        for(MenuOption option: skinsMenu)
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


    private boolean isKeyActive(KeyCode code) {
        String codeString = code.toString();
        if(parentContext.getCurrentlyActiveKeys().containsKey(codeString) && parentContext.getCurrentlyActiveKeys().get(codeString))
            return true;
        return false;
    }

    public void handleKey() {
        if(isKeyActive(KeyCode.UP)) {
            if(isMenuVisible)
                selection = up(selectionMenu, selection);
            else if(isSkinsMenuVisible)
                skinsSelection = up(skinsMenu, skinsSelection);

            parentContext.getCurrentlyActiveKeys().remove(KeyCode.UP.toString());
        } else if(isKeyActive(KeyCode.DOWN)) {
            if(isMenuVisible)
                selection = down(selectionMenu, selection);
            else if(isSkinsMenuVisible)
                skinsSelection = down(skinsMenu, skinsSelection);

            parentContext.getCurrentlyActiveKeys().remove(KeyCode.DOWN.toString());
        } else if(isKeyActive(KeyCode.ENTER) || isKeyActive(KeyCode.SPACE)) {
            if(isMenuVisible) {
                switch (selection) {
                    case 0:
                        parentContext.loadLevel(0);
                        clear();
                        break;
                    case 1:

                        setInstructionsVisible(true);
                        setMenuVisible(false);

                        break;
                    case 2:
                        setMenuVisible(false);
                        setSkinsMenuVisible(true);
                        break;
                }
            } else if(isSkinsMenuVisible) {
                if( parentContext.getScore() >= 5000 || purchasedSkinsIndex.contains(skinsSelection))  {
                    parentContext.loadTheme(themes[skinsSelection]);
                    setMenuVisible(true);
                    setSkinsMenuVisible(false);

                    if(!purchasedSkinsIndex.contains(skinsSelection)) {
                        String text = skinsMenu.get(skinsSelection).getText();
                        skinsMenu.get(skinsSelection).setText(text.substring(2, text.length()-7));

                        parentContext.setScore(parentContext.getScore() - 5000);
                        purchasedSkinsIndex.add(skinsSelection);
                    }
                } else {
                    parentContext.getMyHUD().fadeNewLabel("NOT ENOUGH CREDITS!");
                }

            } else {
                setInstructionsVisible(false);
                setMenuVisible(true);
            }

            parentContext.getCurrentlyActiveKeys().remove(KeyCode.ENTER.toString());
            parentContext.getCurrentlyActiveKeys().remove(KeyCode.SPACE.toString());
        }
    }

    public void updateColor(Color textColor) {
        title.setFill(textColor);
        author.setFill(textColor);
        instructions.setFill(textColor);

        for(MenuOption option: selectionMenu)
            option.updateColor(textColor);

        for(MenuOption option: skinsMenu)
            option.updateColor(textColor);

    }



}
