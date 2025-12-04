package org.example.Actors;

import org.example.DataBase.ManageDataBase;
import org.example.DataBase.Players;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 5555;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, PlayerData> players = new HashMap<>();
    private static ManageDataBase databaseManager;

    public static void main(String[] args) {
        System.out.println("Server starting...");

        try {
            databaseManager = new ManageDataBase();
            System.out.println("Database connection established");
        } catch (Exception e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        String playerName = client.getPlayerName();
        PlayerData playerData = players.get(playerName);
        int score = (int) playerData.size;
        clients.remove(client);
        saveScoreToDatabase(playerName,score);
        if (client.getPlayerName() != null) {
            players.remove(client.getPlayerName());
        }
        System.out.println("Client disconnected");
    }

    public static void updatePlayerPosition(String playerName, double x, double y, double size) {
        players.put(playerName, new PlayerData(x, y, size));

        // Broadcast pozycji do wszystkich klientów
        String positionMessage = "POSITION:" + playerName + ":" + x + ":" + y + ":" + size;
        for (ClientHandler client : clients) {
            client.sendMessage(positionMessage);
        }
    }

    public static void handlePlayerCollision(String eater, String eaten) {
        if (players.containsKey(eater) && players.containsKey(eaten)) {
            PlayerData eaterData = players.get(eater);
            PlayerData eatenData = players.get(eaten);

            eaterData.size += eatenData.size * 0.5;
            players.remove(eaten);

            // Powiadomienie klientów
            String collisionMessage = "COLLISION:" + eater + ":" + eaten + ":" + eaterData.size;
            for (ClientHandler client : clients) {
                client.sendMessage(collisionMessage);
            }
        }
    }

    private static void saveScoreToDatabase(String playerName, int score) {
        try {
            Players player = new Players(playerName, score);
            databaseManager.addScore(player);
            System.out.println("Zapisano wynik do bazy: " + playerName + " - " + score);
        } catch (Exception e) {
            System.err.println("Błąd przy zapisie do bazy: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Odbieranie nazwy gracza
                playerName = in.readLine();
                System.out.println("Player trying to connect: " + playerName);

                // Sprawdź czy nazwa jest zajęta (wśród aktualnych graczy)
                synchronized(players) {
                    if (players.containsKey(playerName)) {
                        // Nazwa już jest używana przez innego gracza
                        out.println("NAME_TAKEN");
                        socket.close();
                        System.out.println("Nazwa " + playerName + " odrzucona - już w użyciu");
                        return;
                    }

                    // Sprawdź w bazie danych czy nazwa jest zajęta
                    if (databaseManager.ifPlayerNameIsFree(playerName)) {
                        // Nazwa jest już w bazie danych
                        out.println("NAME_TAKEN");
                        socket.close();
                        System.out.println("Nazwa " + playerName + " odrzucona - już w bazie");
                        return;
                    }

                    // Nazwa dostępna
                    out.println("NAME_ACCEPTED");
                    System.out.println("Player connected: " + playerName);

                    // Wysyłanie istniejących graczy do nowego klienta
                    for (Map.Entry<String, PlayerData> entry : players.entrySet()) {
                        if (!entry.getKey().equals(playerName)) {
                            PlayerData data = entry.getValue();
                            sendMessage("NEW_PLAYER:" + entry.getKey() + ":" +
                                    data.x + ":" + data.y + ":" + data.size);
                        }
                    }

                    // Dodanie nowego gracza
                    players.put(playerName, new PlayerData(400, 300, 25));

                    // Powiadomienie innych o nowym graczu
                    broadcast("NEW_PLAYER:" + playerName + ":400:300:25", this);
                }

                // Obsługa wiadomości
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    if (message.startsWith("MOVE:")) {
                        // Format: MOVE:name:x:y:size
                        String[] parts = message.split(":");
                        if (parts.length == 5) {
                            updatePlayerPosition(parts[1],
                                    Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3]),
                                    Double.parseDouble(parts[4]));
                        }
                    } else if (message.startsWith("EAT:")) {
                        // Format: EAT:eater:eaten
                        String[] parts = message.split(":");
                        if (parts.length == 3) {
                            handlePlayerCollision(parts[1], parts[2]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public String getPlayerName() {
            return playerName;
        }
    }

    static class PlayerData {
        double x, y, size;

        PlayerData(double x, double y, double size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
}