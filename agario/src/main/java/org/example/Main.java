package org.example;

import org.example.DataBase.Players;
import org.example.DataBase.ManageDataBase;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Players db = new Players("Tworzyk",510);
        Players player = new Players("Test",320);
        ManageDataBase mg = new ManageDataBase();
        mg.addScore(db);
        mg.addScore(player);
    }
}