package org.example.Actors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.example.Food.Food;
import org.example.Player.Player;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Integer MAXFOOD = 30;
    private Pane gameRoot;
    private String playerName;
    private Player localPlayer;
    private Map<String, Player> remotePlayers = new HashMap<>();
    private List<Food> foodList = new ArrayList<>();
    private Random random = new Random();
    private boolean running = true;
    private javafx.animation.AnimationTimer gameLoop;
    private double mouseX, mouseY;

    public Client(String host, int port, String playerName, Pane gameRoot) throws IOException {
        this.playerName = playerName;
        this.gameRoot = gameRoot;

        // Ustawienie początkowej pozycji myszy na środek ekranu
        this.mouseX = 600;
        this.mouseY = 450;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Wysyłanie nazwy gracza do serwera
            out.println(playerName);

            // Oczekiwanie na odpowiedź serwera
            String response = in.readLine();
            if (response != null && response.equals("NAME_TAKEN")) {
                socket.close();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Nazwa zajęta");
                    alert.setContentText("Nazwa gracza '" + playerName + "' jest już zajęta. Wybierz inną nazwę.");
                    alert.showAndWait();
                });
                throw new IOException("Nazwa zajęta");
            }

            if (response == null || !response.equals("NAME_ACCEPTED")) {
                socket.close();
                throw new IOException("Nieprawidłowa odpowiedź serwera");
            }
            // Tworzenie lokalnego gracza
            Color color = Color.rgb(
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
            );
            localPlayer = new Player(400.0, 300.0, color, playerName);
            gameRoot.getChildren().add(localPlayer.getCircle());

            // Setup kontrolek myszy
            setupMouseControls();

            // Rozpoczęcie wątku do odbierania wiadomości
            new Thread(this::receiveMessages).start();

            // Rozpoczęcie respienia jedzenia
            new Thread(this::spawnFood).start();

            // Uruchomienie pętli gry
            startGameLoop();

        } catch (IOException e) {
            throw e;
        }
    }
    public void sendScoreToServer() {
        if (out != null) {
            String message = String.format("SCORE:%s:%d", playerName, localPlayer.getEatenFood());
            out.println(message);
            System.out.println("Wysłano wynik do serwera: " + playerName + " - " + localPlayer.getEatenFood());
        }
    }
    private void setupMouseControls() {
        // Obsługa ruchu myszy - śledzenie pozycji kursora
        gameRoot.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        gameRoot.setOnMouseDragged(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Aktualizacja pozycji gracza w kierunku myszy
                updatePlayerPosition();

                // Sprawdzanie kolizji z jedzeniem
                checkFoodCollisions();

                // Wysyłanie aktualizacji do serwera
                sendMoveUpdate();
            }
        };
        gameLoop.start();
    }

    private void updatePlayerPosition() {
        double dx = mouseX - localPlayer.getXx();
        double dy = mouseY - localPlayer.getYy();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Prędkość zależy od rozmiaru - im większy gracz, tym wolniej się porusza
        double speed = Math.max(1, 10 - localPlayer.getSize() / 10);

        if (distance > 0) {
            if (distance < speed) {
                localPlayer.setXx(mouseX);
                localPlayer.setYy(mouseY);
            } else {
                localPlayer.setXx(localPlayer.getXx() + dx / distance * speed);
                localPlayer.setYy(localPlayer.getYy() + dy / distance * speed);
            }

            // Aktualizacja pozycji kółka
            localPlayer.getCircle().setCenterX(localPlayer.getXx());
            localPlayer.getCircle().setCenterY(localPlayer.getYy());
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                handleServerMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split(":");

        switch (parts[0]) {
            case "NEW_PLAYER":
                // Format: NEW_PLAYER:name:x:y:size
                if (parts.length == 5 && !parts[1].equals(playerName)) {
                    javafx.application.Platform.runLater(() -> {
                        Player remotePlayer = new Player(
                                Double.parseDouble(parts[2]),
                                Double.parseDouble(parts[3]),
                                Color.RED,
                                parts[1]
                        );
                        remotePlayer.setSize(Double.parseDouble(parts[4]));
                        remotePlayers.put(parts[1], remotePlayer);
                        gameRoot.getChildren().add(remotePlayer.getCircle());
                    });
                }
                break;

            case "POSITION":
                // Format: POSITION:name:x:y:size
                if (parts.length == 5 && !parts[1].equals(playerName)) {
                    javafx.application.Platform.runLater(() -> {
                        Player remotePlayer = remotePlayers.get(parts[1]);
                        if (remotePlayer != null) {
                            remotePlayer.setXx(Double.parseDouble(parts[2]));
                            remotePlayer.setYy(Double.parseDouble(parts[3]));
                            remotePlayer.setSize(Double.parseDouble(parts[4]));
                            remotePlayer.getCircle().setCenterX(remotePlayer.getXx());
                            remotePlayer.getCircle().setCenterY(remotePlayer.getYy());
                            remotePlayer.getCircle().setRadius(remotePlayer.getSize());
                        }
                    });
                }
                break;

            case "COLLISION":
                // Format: COLLISION:eater:eaten:newSize
                if (parts.length == 4) {
                    String eater = parts[1];
                    String eaten = parts[2];
                    double newSize = Double.parseDouble(parts[3]);

                    Platform.runLater(() -> {
                        // Jeśli zjedzony gracz to my (lokalny gracz), to przegrywamy
                        if (eaten.equals(playerName)) {
                            // Gracz został zjedzony - koniec gry
                            System.out.println("Zostałeś zjedzony przez: " + eater);
                            // Możemy wyświetlić komunikat i wrócić do menu
                            disconnect();
                            // ... kod do obsługi powrotu do menu
                        }

                        // Usuń zjedzonego gracza
                        Player eatenPlayer = remotePlayers.get(eaten);
                        if (eatenPlayer != null) {
                            gameRoot.getChildren().remove(eatenPlayer.getCircle());
                            remotePlayers.remove(eaten);
                        }

                        // Aktualizuj rozmiar gracza, który zjadł
                        if (eater.equals(playerName)) {
                            // To my zjedliśmy kogoś - aktualizuj lokalny rozmiar
                            localPlayer.setSize(newSize);
                        } else {
                            // Ktoś inny zjadł - aktualizuj zdalny gracz
                            Player eaterPlayer = remotePlayers.get(eater);
                            if (eaterPlayer != null) {
                                eaterPlayer.setSize(newSize);
                                eaterPlayer.getCircle().setRadius(newSize);
                            }
                        }
                    });
                }
                break;
        }
    }

    private void spawnFood() {
        while (running) {
            try {
                Thread.sleep(500); // Respawn co 0.5 sekundy

                Platform.runLater(() -> {
                   while(foodList.size() < MAXFOOD) {
                       double x = random.nextDouble() * 1200;
                       double y = random.nextDouble() * 900;
                       Color color = Color.rgb(
                               random.nextInt(256),
                               random.nextInt(256),
                               random.nextInt(256)
                       );

                    Food food = new Food(x, y, color);
                    foodList.add(food);
                    gameRoot.getChildren().add(food.getCircle());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void sendMoveUpdate() {
        if (out != null) {
            String message = String.format("MOVE:%s:%.2f:%.2f:%.2f",
                    playerName,
                    localPlayer.getXx(),
                    localPlayer.getYy(),
                    localPlayer.getSize());
            out.println(message.replace(",","."));
        }
    }

    public void sendEatEvent(String eatenPlayer) {
        if (out != null) {
            String message = String.format("EAT:%s:%s", playerName, eatenPlayer);
            out.println(message);
        }
    }

    public void checkFoodCollisions() {
        // Sprawdzanie kolizji z jedzeniem (tylko po stronie klienta)
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            double dx = localPlayer.getXx() - food.getCircle().getCenterX();
            double dy = localPlayer.getYy() - food.getCircle().getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < localPlayer.getSize() && localPlayer.getSize() < localPlayer.getMaxSize()) {
                localPlayer.eatFood(food);
                gameRoot.getChildren().remove(food.getCircle());
                foodList.remove(i);
                i--;
                sendMoveUpdate(); // Aktualizacja rozmiaru
            }
        }

        // Sprawdzanie kolizji z innymi graczami (lokalnie)
        for (Map.Entry<String, Player> entry : remotePlayers.entrySet()) {
            Player otherPlayer = entry.getValue();
            double dx = localPlayer.getXx() - otherPlayer.getXx();
            double dy = localPlayer.getYy() - otherPlayer.getYy();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < (localPlayer.getSize() + otherPlayer.getSize())) {
                if (localPlayer.getSize() > otherPlayer.getSize() * 1.1) {
                    // Wysyłamy do serwera informację o kolizji, ale nie usuwamy gracza lokalnie
                    sendEatEvent(entry.getKey());
                    // Możemy przerwać pętlę, bo jednocześnie możemy zjeść tylko jednego gracza
                    break;
                }
            }
        }
    }

    public void disconnect() {
        running = false;
        sendScoreToServer();
        if (gameLoop != null) {
            gameLoop.stop();
        }
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}