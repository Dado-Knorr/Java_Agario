package org.example.Gui;

import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.awt.*;


public class Window extends Application {
    @Override
    public void start(Stage stage) throws Exception {


        GridPane root = new GridPane();
        root.setAlignment(javafx.geometry.Pos.CENTER);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Agarjos");

        VBox buttonContainer = new VBox(10);

        TextField tx = new TextField();

        tx.setPromptText("Wpisz swoj nick");

        Button startGame = new Button("Start Game");
        Button ranking = new Button("Ranking");

        startGame.setMaxWidth(Double.MAX_VALUE);
        ranking.setMaxWidth(Double.MAX_VALUE);

        buttonContainer.getChildren().addAll(tx,startGame, ranking);

        root.add(buttonContainer, 0, 0);

        stage.setScene(scene);
        stage.show();
    }
}
