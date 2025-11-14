package org.example;

import javafx.application.Application;
import org.example.DataBase.ManageDataBase;
import org.example.DataBase.Players;
import org.example.Gui.Window;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {

//        Players db = new Players("test586",999);
//        Players db1 = new Players("test66",1);
//        Players db2 = new Players("test54",2);
//        Players db3 = new Players("test45",3);
//        Players db4 = new Players("test11",4);
//        Players db5 = new Players("test90",5);
//        Players player = new Players("Test4875",320);
//        ManageDataBase mg = new ManageDataBase();
//        mg.addScore(db);
//        mg.addScore(player);
//        mg.addScore(db2);
//        mg.addScore(db1);
//        mg.addScore(db3);
//        mg.addScore(db4);
//        mg.addScore(db5);

        Application.launch(Window.class,args);
    }
}