package com.MyGame.Connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable
{
    private static final int COLUMNS = 7;
    private static final int Rows = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String player_One = "Player One";
    private static String player_Two = "Player Two";

    private boolean isPlayerOneTurn = true;

    private Disc[][] insertedDiskArray = new Disc[Rows][COLUMNS];

    @FXML
    public GridPane RootGradePane;

    @FXML
    public Pane InsertedDiskPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField txtfldPlayerOne, txtfldPlayerTwo;

    @FXML
    public Button btnSetNames;

    private boolean isAllowedToInsert = true;

    public void createPlayground()
    {
        btnSetNames.setOnAction(event -> {
            player_One = txtfldPlayerOne.getText();
            player_Two = txtfldPlayerTwo.getText();
        });

            Shape rectanglewithHoles = CreateGameStructuralGrid();
            RootGradePane.add(rectanglewithHoles, 0, 1);

            List<Rectangle> rectangleList = CreateClickableColumns();

            for (Rectangle rectangle : rectangleList
            ) {
                RootGradePane.add(rectangle, 0, 1);
            }

    }

    private Shape CreateGameStructuralGrid()
    {
        Shape rectanglewithHoles= new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (Rows + 1) * CIRCLE_DIAMETER);

        for(int row = 0; row < Rows; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) +  CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectanglewithHoles = Shape.subtract(rectanglewithHoles, circle);
            }
        }

        rectanglewithHoles.setFill(Color.WHITE);

        return rectanglewithHoles;
    }

    private List<Rectangle> CreateClickableColumns()
    {
        List<Rectangle> rectangleList = new ArrayList<>();

            for(int col = 0; col < COLUMNS; col++) {

                Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (Rows + 1) * CIRCLE_DIAMETER);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
                rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
                final int Column = col;
                rectangle.setOnMouseClicked(event -> {
                    if(isAllowedToInsert) {
                        isAllowedToInsert = false;
                        insertDisc(new Disc(isPlayerOneTurn), Column);
                    }
                });

                rectangleList.add(rectangle);
            }
       return rectangleList;
    }

    private void insertDisc(Disc disc, int column)
    {
        int row = Rows - 1;
        while (row >= 0)
        {
            if(getDiscIfPresent(row, column) == null)
                break;
            row--;
        }

        if (row < 0) //if it is full we can not insert disk
            return;

        insertedDiskArray[row][column] =  disc;
        InsertedDiskPane.getChildren().add(disc);
        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;
            if(gameEnded(currentRow, column)) {

                gameOver();
                return;

            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? player_One : player_Two);
        });
        translateTransition.play();
    }

    private void gameOver() {
        String winner = isPlayerOneTurn ? player_One : player_Two;
        System.out.println("Winner is: " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is : "+winner);
        alert.setContentText("Want to play again ? ");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {

            Optional<ButtonType> btnClicked = alert.showAndWait();

            if ( btnClicked.isPresent() && btnClicked.get() == yesBtn)
            {
                resetGame();
            }
            else {
                Platform.exit();
                System.exit(0);
            }

        });


    }

    public void resetGame() {

        InsertedDiskPane.getChildren().clear();

        for (int row = 0; row < insertedDiskArray.length; row++) {

            for (int col = 0; col < insertedDiskArray.length; col++) {

                insertedDiskArray[row][col] = null;
            }
        }

        isPlayerOneTurn = true;
        playerNameLabel.setText(player_One);

        txtfldPlayerOne.clear();
        txtfldPlayerTwo.clear();

        createPlayground();
    }

    private Disc getDiscIfPresent(int row, int column)
    {
        if(row >= Rows || row < 0 || column >= COLUMNS || column < 0)
            return null;

        return insertedDiskArray[row][column];
    }

    private boolean gameEnded(int Row, int column) {

        List<Point2D> verticalPoints =  IntStream.rangeClosed(Row - 3, Row + 3)    //range of rows values 0,1,2,3,4,5
                .mapToObj(r -> new Point2D(r, column))          //0,3 1,3 2,3 3,3 4,3 5,3
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints =  IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(Row, col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(Row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(Row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> Points) {

        int chain = 0;

        for (Point2D point2D: Points) {
            int rowIndexforArray = (int) point2D.getX();
            int columnIndexforArray = (int) point2D.getY();

            Disc disc = getDiscIfPresent(rowIndexforArray, columnIndexforArray);

            if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn)
            {
                chain++;
                if(chain == 4) {
                    return true;
                }
            }
            else
            {
                chain = 0;
            }
        }
        return  false;
    }

    private static class Disc extends Circle
    {
        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove)
        {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
