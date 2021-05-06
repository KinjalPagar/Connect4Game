package com.MyGame.Connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Game.fxml"));
        GridPane gridPane = fxmlLoader.load();

        controller = fxmlLoader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) gridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ConnectFour");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu()
    {
        //File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutConnect4 = new MenuItem("About Connect4");
        aboutConnect4.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();

        MenuItem aboutme = new MenuItem("About Me");
        aboutme.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutConnect4, separatorMenuItem1, aboutme);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Kinjal Pagar");
        alert.setContentText("I love to play with code and to create amazing applications.");
        alert.show();
    }

    private void aboutConnect4()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four (also known as Four Up, Plot Four, Find Four, Captain's Mistress, " +
                "Four in a Row, Drop Four, and Gravitrips in the Soviet Union) is a two-player connection board game, " +
                "in which the players choose a color and then take turns dropping colored discs into a seven-column, " +
                "six-row vertically suspended grid. The pieces fall straight down, occupying the lowest available space " +
                "within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal " +
                "line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing " +
                "the right moves.");
        alert.show();
    }

    private void exitGame()
    {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame()
    {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
