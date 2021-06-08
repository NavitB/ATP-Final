package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction;
        switch (keyEvent.getCode()){
            case UP -> direction = MovementDirection.UP;
            case DOWN -> direction = MovementDirection.DOWN;
            case LEFT -> direction = MovementDirection.LEFT;
            case RIGHT -> direction = MovementDirection.RIGHT;
            case DIGIT2 -> direction = MovementDirection.DIGIT2;
            case DIGIT8 -> direction = MovementDirection.DIGIT8;
            case DIGIT6 -> direction = MovementDirection.DIGIT6;
            case DIGIT4 -> direction = MovementDirection.DIGIT4;
            case DIGIT1 -> direction = MovementDirection.DIGIT1;
            case DIGIT3 -> direction = MovementDirection.DIGIT3;
            case DIGIT7 -> direction = MovementDirection.DIGIT7;
            case DIGIT9 -> direction = MovementDirection.DIGIT9;
            default -> {
                // no need to move the player...
                return;
            }

        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public boolean gameOver(){
        return model.gameOver();
    }
}


