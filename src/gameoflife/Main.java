package gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Main extends Application {
    Tile[][] tiles;
    int gridHeight;
    int gridWidth;
    boolean startRandomizer;
    String fileContents;
    String[] strings;
    Slider slider;
    int needsCheckCounter;
    GraphicsContext gc;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        needsCheckCounter = 0;
        //int gridSize = 5000;
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button run = new Button("Run");
        Button pause = new Button("Pause");
        Button randomizer = new Button("Randomizer");
        Button browseBtn = new Button("Browse");
        Button clear = new Button("Clear");
        gridHeight = 350;
        gridWidth = 350;
        double tileSize = 2;
        slider = new Slider();
        slider.setMin(.1);
        slider.setMax(100);
        slider.setValue(1);
        slider.setMajorTickUnit(.1);
        Label sliderLabel = new Label(Double.toString(slider.getValue()));

        hBox.getChildren().addAll(run, pause, randomizer, browseBtn, clear, slider, sliderLabel);
        Canvas canvas = new Canvas(gridWidth*tileSize, gridHeight *tileSize);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,gridWidth*tileSize, gridHeight *tileSize);
        vBox.getChildren().addAll(canvas, hBox);
        startRandomizer = false;
//        gridWidth = gridSize;
//        gridLength = gridSize;
        tiles = new Tile[gridHeight][gridWidth];
        Scene scene = new Scene(vBox);

        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                Tile t = new Tile(tileSize, tileSize, Color.WHITE, (tileSize * i), (tileSize * j));
                tiles[i][j] = t;
            }
        }


        AnimationTimer timer = new AnimationTimer() {
            private double nextTime = 1000000000;
            int counter = 0;
            int x = 0 ;
            @Override
            public void handle(long now) {
                if (now > nextTime) {
                    x++;
                    System.out.println("needs CHeck" + needsCheckCounter);
                    System.out.println(x);
                    double temp = 10000000 * slider.getValue();
                    nextTime = now + temp;
                    checkRules();
                    counter++;
                    if(counter > 20){
                        needsCheckCounter = 0;
                        for (int i = 0; i < tiles.length; i++) {
                            for (int j = 0; j < tiles[0].length; j++) {
                                tiles[i][j].needsCheck = false;
                            }
                        }
                        for (int i = 0; i < tiles.length; i++) {
                            for (int j = 0; j < tiles[0].length; j++) {
                                if(tiles[i][j].isAlive){
                                    checkAdjacent(i,j);
                                }
                            }
                        }
                        sliderLabel.setText(Double.toString(Math.round(slider.getValue())));
                    }
                    if (startRandomizer) {
                        randomizer(5);
                    }
                }
            }
        };
        run.setOnAction((event) -> {
            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[0].length; j++) {
                    if(tiles[i][j].isAlive){
                        checkAdjacent(i,j);
                    }
                }
            }
            timer.start();
        });
        pause.setOnAction((event) -> {
            timer.stop();
        });
        randomizer.setOnAction((event) -> {
            startRandomizer = !startRandomizer;
        });
        clear.setOnAction((event) -> {
            for (int i =0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[0].length; j++) {
                    if(tiles[i][j].isAlive){
                        tiles[i][j].setDead(Main.this);

                    }
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game of Life");
        primaryStage.show();
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                for (int i = 0; i < tiles.length; i++) {
                    for (int j = 0; j < tiles[0].length; j++) {
                        tiles[i][j].checkClicked(x, y,Main.this);
                    }
                }
            }
        });
        browseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fc = new FileChooser();
                File loadFile = fc.showOpenDialog(primaryStage);
                try {
                    Scanner scanner = new Scanner(loadFile);
                    scanner.useDelimiter("$");
                    fileContents = "";

                    while (scanner.hasNextLine()) {
                        fileContents += scanner.next();
                    }
//                    System.out.println("fileContents:" + fileContents);
                    fileContents = fileContents.replaceAll("[$]", Matcher.quoteReplacement("1b$"));

                    String regex = "[$]";
                    strings = fileContents.split(regex);
//                    for(int i = 0 ; i < strings.length; i++){
//                        System.out.println(strings[i]);
//                    }
                    //takes all single b's or o's and inserts a 1 before them b -> 1b
                    for (int i = 0; i < strings.length; i++) {
                        String regex2 = "(?<![0-9])[b]";
                        String regex3 = "(?<![0-9])[o]";
                        strings[i] = strings[i].replaceAll(regex2, "1b");
                        strings[i] = strings[i].replaceAll(regex3, "1o");
//                        System.out.println("2");
//                        for(int j = 0 ; j < strings.length; j++){
//                            System.out.println(strings[j]);
//                        }
                    }


                    for (int i = 0; i < strings.length; i++) {
                        Scanner scanner3 = new Scanner(strings[i]);
                        String regex3 = "[0-9]+[a-z]{1}";
                        Pattern pat = Pattern.compile(regex3);
                        List<String> list = scanner3.findAll(pat).map(MatchResult::group)
                                .collect(Collectors.toList());
//                        System.out.println(list);
                        for (int j = 0; j < list.size(); j++) {
                            Scanner scanner4 = new Scanner(list.get(j));
                            String regex4 = "[0-9]+";
                            double num = Double.valueOf(scanner4.findInLine(regex4));
                            //System.out.println(num);
                            String character = scanner4.findInLine("[a-z]");
                            String replacement = character;
                            for (int k = 1; k < num; k++) {
                                replacement += character;
                            }
                            list.set(j, replacement);
                        }
                        strings[i] = "";
                        for (int j = 0; j < list.size(); j++) {
                            strings[i] += list.get(j);
                        }
                    }
//                    System.out.println("3");
//                    for(int i = 0 ; i < strings.length; i++){
//                        System.out.println(strings[i]);
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                scene.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        x = (int) (x / tileSize);
                        y = (int) (y / tileSize);
                        System.out.println(x);
                        System.out.println(y);

                        try {
                            for (int i = 0; i < strings.length; i++) {
                                for (int j = 0; j < strings[i].length(); j++) {
                                    if (strings[i].charAt(j) == 'b') {
                                        tiles[x + i][y + j].setDead(Main.this);
                                        tiles[x + i][y + j].needsCheck();

                                    } else if (strings[i].charAt(j) == 'o') {
                                        tiles[x + i][y + j].setAlive(Main.this);
                                        tiles[x + i][y + j].needsCheck();
                                    }
                                }
                            }
                        } catch (Exception exc) {
                            System.out.println(exc);
                        }
                        for (int i = 0; i < tiles.length; i++) {
                            for (int j = 0; j < tiles[0].length; j++) {
                                if(tiles[i][j].isAlive){
                                    checkAdjacent(i,j);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void checkRules() {
        int numLiveCells = 0;
        ArrayList<Integer> xAlives = new ArrayList();
        ArrayList<Integer> yAlives = new ArrayList();
        ArrayList<Integer> xDeads = new ArrayList();
        ArrayList<Integer> yDeads = new ArrayList();
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                if(tiles[i][j].needsCheck) {
                    if (i > 0 && j > 0) {
                        if (tiles[i - 1][j - 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (i > 0) {
                        if (tiles[i - 1][j].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (i > 0 && j < gridWidth - 1) {
                        if (tiles[i - 1][j + 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (j > 0) {
                        if (tiles[i][j - 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (j < gridWidth - 1) {
                        if (tiles[i][j + 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (i < gridHeight - 1 && j > 0) {
                        if (tiles[i + 1][j - 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (i < gridHeight - 1) {
                        if (tiles[i + 1][j].getAlive()) {
                            numLiveCells++;
                        }
                    }
                    if (i < gridHeight - 1 && j < gridWidth - 1) {
                        if (tiles[i + 1][j + 1].getAlive()) {
                            numLiveCells++;
                        }
                    }
                }
                if (tiles[i][j].getAlive()) {
                    if (numLiveCells < 2) {
                        xDeads.add(i);
                        yDeads.add(j);
                    }
                    if (numLiveCells > 3) {
                        xDeads.add(i);
                        yDeads.add(j);
                    }
                }
                if (!tiles[i][j].getAlive()) {
                    if (numLiveCells == 3) {
                        xAlives.add(i);
                        yAlives.add(j);
                        checkAdjacent(i,j);
                    }
                }
                numLiveCells = 0;
            }
        }
        for (int i = 0; i < xAlives.size(); i++) {
            tiles[xAlives.get(i)][yAlives.get(i)].setAlive(Main.this);
        }
        for (int i = 0; i < xDeads.size(); i++) {
            tiles[xDeads.get(i)][yDeads.get(i)].setDead(Main.this);
        }
    }

    public void randomizer(int num) {
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            int randomNum = rand.nextInt((gridHeight - 1));
            int randomNum2 = rand.nextInt((gridHeight - 1));
            tiles[randomNum][randomNum2].flipAlive(Main.this);
        }

    }
    public void checkAdjacent(int i, int j){
            tiles[i][j].needsCheck();
            if (i > 0 && j > 0) {
                tiles[i - 1][j - 1].needsCheck();
                needsCheckCounter++;
            }
            if (i > 0) {
                tiles[i - 1][j].needsCheck();
                needsCheckCounter++;

            }
            if (i > 0 && j < gridWidth - 1) {
                tiles[i - 1][j + 1].needsCheck();
                needsCheckCounter++;

            }
            if (j > 0) {
                tiles[i][j - 1].needsCheck();
                needsCheckCounter++;

            }
            if (j < gridWidth - 1) {
                tiles[i][j + 1].needsCheck();
                needsCheckCounter++;

            }
            if (i < gridHeight - 1 && j > 0) {
                tiles[i + 1][j - 1].needsCheck();
                needsCheckCounter++;

            }
            if (i < gridHeight - 1) {
                tiles[i + 1][j].needsCheck();
                needsCheckCounter++;


            }
            if (i < gridHeight - 1 && j < gridWidth - 1) {
                tiles[i + 1][j + 1].needsCheck();
                needsCheckCounter++;

            }
    }
}
