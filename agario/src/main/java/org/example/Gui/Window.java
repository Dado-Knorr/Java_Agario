package org.example.Gui;

import java.io.IOException;

import org.example.Actors.Client;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Window extends Application {
    private Stage primaryStage;
    private GridPane root = new GridPane();
    private Client currentGame;

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
        startGame.setOnAction(e -> {
            String name = playerNameField.getText().trim();
            if (!name.isEmpty()) {
                startGame(name);
            }
        });

        Button rankingButton = new Button("Ranking");
        rankingButton.setOnAction(e -> {
            new RankingWindow(primaryStage).show();
        });

        startGame.setMaxWidth(Double.MAX_VALUE);
        rankingButton.setMaxWidth(Double.MAX_VALUE);

        buttonContainer.getChildren().addAll(playerNameField, startGame, rankingButton);

        root.add(buttonContainer, 0, 0);

        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(e -> {
            if (currentGame != null) {
                currentGame.disconnect();
            }
        });
        stage.show();
    }

    private void startGame(String playerName) {
        Pane gameRoot = new Pane();
        Scene gameScene = new Scene(gameRoot, 1200, 900);


        try {
            // Tworzenie klienta (gracza) zamiast GameLoop
            currentGame = new Client("localhost", 5557, playerName, gameRoot);
        } catch (IOException e) {

            return;
        }
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}