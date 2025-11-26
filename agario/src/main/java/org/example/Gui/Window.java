package org.example.Gui;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos.*;
import javafx.util.Duration;
import org.example.DataBase.ManageDataBase;
import org.example.DataBase.Players;
import org.example.Food.Food;
import org.example.Player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import static javafx.geometry.Pos.TOP_CENTER;


public class Window extends Application {
    private Stage primaryStage;
    private GridPane root = new GridPane();
    private List<Food> foods = new ArrayList<>();
    private Random random = new Random();

    private double mouseX, mouseY;


    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        root.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(root, 800, 600);
        scene.setCursor(Cursor.DEFAULT);
        stage.setTitle("Agarjos");

        VBox buttonContainer = new VBox(10);

        TextField playerNameField = new TextField();

        playerNameField.setPromptText("Wpisz swoj nick");

        Button startGame = new Button("Start Game");
        startGame.setOnAction(e -> startGame(playerNameField.getText()));

        Button rankingButton = new Button("Ranking");

        rankingButton.setOnAction(e -> {
            new RankingWindow(primaryStage).show();
        });

        startGame.setMaxWidth(Double.MAX_VALUE);
        rankingButton.setMaxWidth(Double.MAX_VALUE);

        buttonContainer.getChildren().addAll(playerNameField,startGame,  rankingButton);

        root.add(buttonContainer, 0, 0);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void startGame(String playerName) {
        Pane gameRoot = new Pane();
        Scene gameScene = new Scene(gameRoot, 1200, 900);
        Player player = new Player(400.00, 300.00, Color.BLUE, playerName);

        gameRoot.getChildren().add(player.getCircle());


        gameScene.setOnMouseMoved(event ->{
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });


        for (int i = 0; i < 30; i++) {
            spawnFood(gameRoot);
        }


        Timeline foodSpawner = new Timeline(new KeyFrame(Duration.seconds(5), e -> spawnFood(gameRoot)));
        foodSpawner.setCycleCount(Animation.INDEFINITE);
        foodSpawner.play();


        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {


                double dx = mouseX - player.getCircle().getCenterX();
                double dy = mouseY - player.getCircle().getCenterY();

                double distance = Math.sqrt(dx*dx + dy*dy);
                double speed = 1;

                if (distance > speed) {
                    player.move(dx / distance * speed, dy / distance * speed);
                } else {
                    player.move(dx, dy);
                }


                var iterator = foods.iterator();
                while (iterator.hasNext()) {
                    Food food = iterator.next();
                    if (player.getCircle().getBoundsInParent().intersects(food.getCircle().getBoundsInParent())) {
                        player.eatFood(food);
                        gameRoot.getChildren().remove(food.getCircle());
                        iterator.remove();
                        spawnFood(gameRoot);
                    }
                }
            }
        };
        gameLoop.start();

        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    private Food spawnFood(Pane gameRoot) {
        double x = random.nextDouble() * 1200;
        double y = random.nextDouble() * 900;
        Color color = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
        Food food = new Food(x, y,color);
        foods.add(food);
        gameRoot.getChildren().add(food.getCircle());

        return food;
    }
}
