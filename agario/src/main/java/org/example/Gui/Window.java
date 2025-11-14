package org.example.Gui;

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
import javafx.stage.Stage;
import javafx.geometry.Pos.*;
import org.example.DataBase.ManageDataBase;
import org.example.DataBase.Players;

import java.util.List;




import static javafx.geometry.Pos.TOP_CENTER;


public class Window extends Application {
    private Stage primaryStage;
    private GridPane root = new GridPane();
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
        startGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println(playerNameField.getText());
            }
        });
        Button rankingButton = new Button("Ranking");

        rankingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showRanking();
            }
        });
        startGame.setMaxWidth(Double.MAX_VALUE);
        rankingButton.setMaxWidth(Double.MAX_VALUE);

        buttonContainer.getChildren().addAll(playerNameField,startGame,  rankingButton);

        root.add(buttonContainer, 0, 0);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void startGame()
    {

    }
    private void showRanking() {
        ManageDataBase mg = new ManageDataBase();
        List<Players> scores = mg.getAllScores();

        // Tworzymy nowe okno (Stage) dla rankingu
        Stage rankingStage = new Stage();
        rankingStage.setTitle("Ranking Graczy");

        VBox rankingLayout = new VBox(10);
        rankingLayout.setPadding(new javafx.geometry.Insets(15));
        rankingLayout.setAlignment(TOP_CENTER);

        // Nagłówek
        Label title = new Label("RANKING GRACZY");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //table do rankingu, z Spacing 5
        VBox rankingBox = new VBox(5);
        rankingBox.setAlignment(TOP_CENTER);

        //GridPane is a layout component which lays out its child components in a grid.
        // The size of the cells in the grid depends on the components displayed in the GridPane, <-- taka instrukcja
        // but there are some rules
        GridPane rankingGrid = new GridPane();
        //rankingGrid.setHgap(20);
        //rankingGrid.setVgap(5);
        rankingGrid.setPadding(new javafx.geometry.Insets(10, 10, 10, 0));

        //Kolumna o szerokosci 80
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col1.setPrefWidth(80);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.LEFT);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHalignment(HPos.RIGHT);
        col3.setPrefWidth(60);

        //dodajemy do naszego głownego GridPanelu
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

            //position do określania pozycji w rankingu graczy
            int position = 1;
            for (Players player : scores) {
                // tutaj pobieramy sobie dane z obiektu Player.
                String nickname = player.getPlayerName();
                Integer score = player.getScore();

                Label posLabel = new Label(position + ".");
                Label nickLabel = new Label(nickname);
                Label scoreLabel = new Label(score.toString());

                //dodajemy kolumny
                rankingGrid.add(posLabel, 0, position);
                rankingGrid.add(nickLabel, 1, position);
                rankingGrid.add(scoreLabel, 2, position);

                position++;
            }
            //dodajemy nasz rankinggrid do glownego "gridu"
            rankingBox.getChildren().add(rankingGrid);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(rankingBox);
        scrollPane.setFitToWidth(true); // Dopasowuje szerokość do zawartości
        scrollPane.setPrefViewportHeight(250); // Ustawia preferowaną wysokość obszaru widocznego
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Pionowy scrollbar pojawia się gdy potrzeba


        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> rankingStage.close());
        //scalamy wszystko do glownego okna
        rankingLayout.getChildren().addAll(title, scrollPane, closeButton);


        Scene rankingScene = new Scene(rankingLayout, 300, 300);
        rankingStage.setScene(rankingScene);
        rankingStage.initOwner(primaryStage);
        rankingStage.setResizable(false);
        rankingStage.show();
    }
}
