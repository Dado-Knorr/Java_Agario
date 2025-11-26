package org.example.Gui;

import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.DataBase.ManageDataBase;
import org.example.DataBase.Players;

import java.util.List;

import static javafx.geometry.Pos.TOP_CENTER;

public class RankingWindow {

    private Stage owner;

    public RankingWindow(Stage owner) {
        this.owner = owner;
    }

    public void show() {
        ManageDataBase mg = new ManageDataBase();
        List<Players> scores = mg.getAllScores();

        Stage rankingStage = new Stage();
        rankingStage.setTitle("Ranking Graczy");

        VBox rankingLayout = new VBox(10);
        rankingLayout.setPadding(new javafx.geometry.Insets(15));
        rankingLayout.setAlignment(TOP_CENTER);

        Label title = new Label("RANKING GRACZY");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox rankingBox = new VBox(5);
        rankingBox.setAlignment(TOP_CENTER);

        GridPane rankingGrid = new GridPane();
        rankingGrid.setPadding(new javafx.geometry.Insets(10, 10, 10, 0));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col1.setPrefWidth(80);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.LEFT);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHalignment(HPos.RIGHT);
        col3.setPrefWidth(60);

        rankingGrid.getColumnConstraints().addAll(col1, col2, col3);

        if (scores.isEmpty()) {
            rankingBox.getChildren().add(new Label("Brak danych rankingu"));
        } else {
            Label positionHeader = new Label("Position");
            positionHeader.setStyle("-fx-font-weight: bold;");
            rankingGrid.add(positionHeader, 0, 0);

            Label nickHeader = new Label("NICK");
            nickHeader.setStyle("-fx-font-weight: bold;");
            rankingGrid.add(nickHeader, 1, 0);

            Label scoreHeader = new Label("WYNIK");
            scoreHeader.setStyle("-fx-font-weight: bold;");
            rankingGrid.add(scoreHeader, 2, 0);

            int position = 1;
            for (Players player : scores) {
                Label posLabel = new Label(position + ".");
                Label nickLabel = new Label(player.getPlayerName());
                Label scoreLabel = new Label(player.getScore().toString());

                rankingGrid.add(posLabel, 0, position);
                rankingGrid.add(nickLabel, 1, position);
                rankingGrid.add(scoreLabel, 2, position);

                position++;
            }

            rankingBox.getChildren().add(rankingGrid);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(rankingBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(250);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> rankingStage.close());

        rankingLayout.getChildren().addAll(title, scrollPane, closeButton);

        Scene rankingScene = new Scene(rankingLayout, 300, 300);
        rankingStage.setScene(rankingScene);
        rankingStage.initOwner(owner);
        rankingStage.setResizable(false);
        rankingStage.show();
    }
}