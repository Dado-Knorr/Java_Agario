package org.example;

import javafx.application.Application;
import org.example.Gui.Window;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {

//        Players db = new Players("Tworzyk",510);
//        Players player = new Players("Test",320);
//        ManageDataBase mg = new ManageDataBase();
//        mg.addScore(db);
//        mg.addScore(player);
          Application.launch(Window.class,args);
    }
}