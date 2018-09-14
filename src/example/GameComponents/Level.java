package example.GameComponents;

import example.GameComponents.Block;
import example.MainGame;
import example.UI.Theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {
    private int levelNumber;
    private int livesStart;
    private String fileName;

    private int defaultBouncerSpeed;


    private Theme theme;

    private MainGame parentContext;

    public Level(Theme theme, int number, int lives, String fileName, int defaultBouncerSpeed, MainGame context) {
        this.theme = theme;
        this.levelNumber = number;
        this.livesStart = lives;
        this.fileName = fileName;
        this.defaultBouncerSpeed = defaultBouncerSpeed;
        this.parentContext = context;
    }


    private ArrayList<String> readFile(String fileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        ArrayList<String> output = new ArrayList<>();

        try {
            Scanner in = new Scanner(file);

            while(in.hasNext()) {
                output.add(in.next());
            }
            in.close();
        }
        catch (FileNotFoundException e){
            System.out.print("File Not Found");
        }

        return output;
    }

    public ArrayList<Block> populateBlocks(int w, int h) {
        ArrayList<Block> myBlocks = new ArrayList<Block>();

        ArrayList<String> level = readFile(fileName);

        int height = level.size();
        int width = level.get(0).length();

        for(int i=0; i<level.size(); i++) {
            String row = level.get(i);

            int column = 0;
            for(int j=0; j<row.length(); j++) {
                char block = row.charAt(j);

                int health;
                Block.Type type;

                if(block=='?' || block=='+' || block=='-') {
                    j++;
                    health = Character.getNumericValue(row.charAt(j));
                }
                else {
                    health = Character.getNumericValue(block);
                }

                if(block!='o') {
                    if(block=='?')
                        type = Block.Type.RANDOM_DROP;
                    else if(block=='+')
                        type = Block.Type.BALL_SPEED_UP;
                    else if(block=='-')
                        type = Block.Type.BALL_SPEED_DOWN;
                    else
                        type = Block.Type.NORMAL;


                    myBlocks.add(new Block(column*w/width, i*h/height, w/width, h/height, health, type, parentContext));

                }

                column++;
            }
        }

        return myBlocks;
    }

    public int getLivesStart() {
        return livesStart;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getDefaultBouncerSpeed() {
        return defaultBouncerSpeed;
    }
}
