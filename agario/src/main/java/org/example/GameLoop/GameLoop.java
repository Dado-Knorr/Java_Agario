package org.example.GameLoop;

import javafx.scene.paint.Color;
import org.example.Player.Player;
import org.example.Food.Food;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class GameLoop {
    private List<Player> players;
    private List<Food> foodList;
    private Pane gameRoot;

    public GameLoop(Pane gameRoot, String playerName, javafx.stage.Stage primaryStage) {
        this.gameRoot = gameRoot;
        this.players = new ArrayList<>();
        this.foodList = new ArrayList<>();

        // Inicjalizacja gracza
        Player player = new Player(400.0, 300.0, Color.ORANGE, playerName);
        players.add(player);
        gameRoot.getChildren().add(player.getCircle());

        startGameLoop();
    }

    private void startGameLoop() {
        // Główna pętla gry - sprawdzanie kolizji
        javafx.animation.AnimationTimer gameLoop = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollisions();
            }
        };
        gameLoop.start();
    }

    private void checkCollisions() {
        // Kolizje między graczami
        for (int i = 0; i < players.size(); i++) {
            Player player1 = players.get(i);

            for (int j = i + 1; j < players.size(); j++) {
                Player player2 = players.get(j);

                if (isColliding(player1, player2)) {
                    handlePlayerCollision(player1, player2);
                }
            }

            // Kolizje gracza z jedzeniem (tylko po stronie klienta)
            for (Food food : foodList) {
                if (isColliding(player1, food)) {
                    player1.eatFood(food);
                    gameRoot.getChildren().remove(food.getCircle());
                    foodList.remove(food);
                    break;
                }
            }
        }
    }

    private boolean isColliding(Player player, Food food) {
        double dx = player.getXx() - food.getCircle().getCenterX();
        double dy = player.getYy() - food.getCircle().getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < player.getSize();
    }

    private boolean isColliding(Player player1, Player player2) {
        double dx = player1.getXx() - player2.getXx();
        double dy = player1.getYy() - player2.getYy();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < (player1.getSize() + player2.getSize());
    }

    private void handlePlayerCollision(Player player1, Player player2) {
        // Gracz zjada innego gracza jeśli jest większy
        if (player1.getSize() > player2.getSize()) {
            player1.setSize(player1.getSize() + player2.getSize() * 0.5);
            // Usunięcie gracza z gry
            gameRoot.getChildren().remove(player2.getCircle());
            players.remove(player2);
        } else if (player2.getSize() > player1.getSize()) {
            player2.setSize(player2.getSize() + player1.getSize() * 0.5);
            gameRoot.getChildren().remove(player1.getCircle());
            players.remove(player1);
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
        gameRoot.getChildren().add(player.getCircle());
    }

    public void addFood(Food food) {
        foodList.add(food);
        gameRoot.getChildren().add(food.getCircle());
    }

    public void cleanup() {
        // Czyszczenie zasobów
        players.clear();
        foodList.clear();
    }
}
