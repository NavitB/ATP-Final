//package View;
//
//import ViewModel.MyViewModel;
//import algorithms.mazeGenerators.AMazeGenerator;
//import algorithms.mazeGenerators.IMazeGenerator;
//import algorithms.mazeGenerators.Maze;
//import javafx.event.ActionEvent;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Alert;
//import javafx.scene.control.TextField;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.stage.FileChooser;
//
//import java.io.File;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class MyViewController implements IView, Initializable {
//
//    private MyViewModel viewModel;
//    public TextField textField_rows;
//    public TextField textField_cols;
//    public MazeDisplayer mazeDisplayer;
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//    }
//
//    public void mouseClicked(MouseEvent mouseEvent) {
//        mazeDisplayer.requestFocus();
//    }
//
//    public void solveMaze(ActionEvent actionEvent) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setContentText("Solving maze...");
//        alert.show();
//    }
//
//    public void generateMaze(ActionEvent actionEvent) {
////        if(viewModel == null)
////        {
////            viewModel = new MyViewModel();
////        }
//        int rows = Integer.valueOf(textField_rows.getText());
//        int cols = Integer.valueOf(textField_cols.getText());
//        Maze maze = viewModel.generateMaze(rows,cols);
////        if (mazeDisplayer == null)
////        {
////            mazeDisplayer = new MazeDisplayer();
////        }
//        mazeDisplayer.drawMaze(maze);
//    }
//    public void keyPressed(KeyEvent keyEvent) {
//        int row = mazeDisplayer.getPlayerRow();
//        int col = mazeDisplayer.getPlayerCol();
//
//        switch (keyEvent.getCode()) {
//            case UP -> row -= 1;
//            case DOWN -> row += 1;
//            case RIGHT -> col += 1;
//            case LEFT -> col -= 1;
//        }
//        //setPlayerPosition(row, col);
//
//        keyEvent.consume();
//    }
//    public void openFile(ActionEvent actionEvent) {
//        FileChooser fc = new FileChooser();
//        fc.setTitle("Open maze");
//        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
//        fc.setInitialDirectory(new File("./resources"));
//        File chosen = fc.showOpenDialog(null);
//        //...
//    }
//}

package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MyViewController implements Initializable {
   //public MazeGenerator generator;
    private MyViewModel viewModel;
    public TextField textField_rows;
    public TextField textField_cols;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        playerRow.textProperty().bind(updatePlayerRow);
//        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) {

        int rows = 0;
        int cols = 0;

        rows = Integer.valueOf(textField_rows.getText());
        cols = Integer.valueOf(textField_cols.getText());

        if(viewModel == null)
        {
            viewModel = new MyViewModel();
        }
        if (mazeDisplayer == null)
        {
            mazeDisplayer = new MazeDisplayer();
        }
        Maze maze = viewModel.generateMaze(rows,cols);
        mazeDisplayer.drawMaze(maze);
        setPlayerPosition(0, 0);
    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        //...
    }

    public void keyPressed(KeyEvent keyEvent) {
        int row = mazeDisplayer.getPlayerRow();
        int col = mazeDisplayer.getPlayerCol();

        switch (keyEvent.getCode()) {
            case UP -> row -= 1;
            case DOWN -> row += 1;
            case RIGHT -> col += 1;
            case LEFT -> col -= 1;
        }
        setPlayerPosition(row, col);

        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }
}

