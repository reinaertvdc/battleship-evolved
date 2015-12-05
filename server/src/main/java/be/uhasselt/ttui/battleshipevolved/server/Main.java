package be.uhasselt.ttui.battleshipevolved.server;

import java.util.ArrayList;

import be.uhasselt.ttui.battleshipevolved.Game;

/**
 * Main is the entry point for the Battleship Evolved server.
 */
public class Main {
    private static ArrayList<String> mStringlijst;
    public static void main(String[] args) {
        Game game = new Game();
        System.out.println("The server works!");
    }
}
