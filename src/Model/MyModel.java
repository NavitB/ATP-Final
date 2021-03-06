package Model;

import Client.Client;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import Server.*;
import Client.IClientStrategy;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{

    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;
    private Server mazeGeneratorServer;
    private Server solveMazeServer;
    private boolean gameOver = false;
    private boolean showSolution = false;
    private static final Logger logger = LogManager.getLogger(MyModel.class);




    public void setGameOver(boolean gameOver) {

        this.gameOver = gameOver;
    }

    @Override
    public void stopServers() {
        mazeGeneratorServer.stop();
        solveMazeServer.stop();
    }

    public MyModel(){
        mazeGeneratorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeGeneratorServer.start();
        solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveMazeServer.start();

    }


    public void generateMaze(int rows,int cols)
    {
        removeSolution();
        CommunicateWithServer_MazeGenerating(rows,cols);
        //mazeGeneratorServer.stop();
        setChanged();
        notifyObservers("maze generated");
        // start position:
        movePlayer(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());

    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    private void movePlayer(int row, int col){
        if (row == maze.getGoalPosition().getRowIndex() && col == maze.getGoalPosition().getColumnIndex())
        {
            gameOver=true;
        }
        this.playerRow = row;
        this.playerCol = col;
        if(solution !=  null && showSolution)
        {
            solveMazeToEat();
        }
        setChanged();
        notifyObservers("player moved");
    }

    public boolean gameOver(){
        return gameOver;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch (direction) {
            case UP:
            case DIGIT8:
            {
                if (playerRow > 0 && maze.getMaze()[playerRow-1][playerCol]!=1)
                    movePlayer(playerRow - 1, playerCol);
            }
            break;
            case DOWN:
            case DIGIT2:
            {
                if (playerRow < maze.getMaze().length - 1 && maze.getMaze()[playerRow+1][playerCol]!=1)
                    movePlayer(playerRow + 1, playerCol);
            }
            break;
            case LEFT:
            case DIGIT4:
            {
                if (playerCol > 0 && maze.getMaze()[playerRow][playerCol-1]!=1)
                    movePlayer(playerRow, playerCol - 1);
            }
            break;
            case RIGHT:
            case DIGIT6:
            {
                if (playerCol < maze.getMaze()[0].length - 1 && maze.getMaze()[playerRow][playerCol+1]!=1)
                    movePlayer(playerRow, playerCol + 1);
            }
            break;

            case DIGIT9:
                if (playerRow - 1 >= 0 && playerCol + 1 < maze.getMaze()[0].length && maze.getMaze()[playerRow - 1][playerCol + 1] == 0 && (maze.getMaze()[playerRow - 1][playerCol] == 0 || maze.getMaze()[playerRow][playerCol + 1] == 0)) {
                    movePlayer(playerRow-1,playerCol+1);
                }
                break;
            case DIGIT3:
                if (playerRow + 1 < maze.getMaze().length && playerCol + 1 < maze.getMaze()[0].length && maze.getMaze()[playerRow + 1][playerCol + 1] == 0 && (maze.getMaze()[playerRow][playerCol + 1] == 0 || maze.getMaze()[playerRow + 1][playerCol] == 0)) {
                    movePlayer(playerRow+1,playerCol+1);

                }
                break;
            case DIGIT7:
                if (playerRow - 1 >= 0 && playerCol - 1 >= 0 && maze.getMaze()[playerRow - 1][playerCol - 1] == 0 && (maze.getMaze()[playerRow - 1][playerCol] == 0 || maze.getMaze()[playerRow][playerCol - 1] == 0)) {
                    movePlayer(playerRow-1,playerCol-1);

                }
                break;
            case DIGIT1:
                if (playerRow + 1 < maze.getMaze().length && playerCol - 1 >= 0 && maze.getMaze()[playerRow + 1][playerCol - 1] == 0 && (maze.getMaze()[playerRow + 1][playerCol] == 0 || maze.getMaze()[playerRow][playerCol - 1] == 0)) {
                    movePlayer(playerRow+1 , playerCol-1);

                }
                break;
        }

    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {
        //solve the maze
        Position playerPos = new Position(playerRow,playerCol);
        maze.setStartPosition(playerPos);
        CommunicateWithServer_SolveSearchProblem(this.maze);
        setChanged();
        notifyObservers("maze solved");

    }
    public void solveMazeToEat() {
        //solve the maze
        Position playerPos = new Position(playerRow,playerCol);
        maze.setStartPosition(playerPos);
        CommunicateWithServer_SolveSearchProblem(this.maze);

    }

    @Override
    public Solution getSolution() {
        return this.solution;
    }

    private void CommunicateWithServer_MazeGenerating(int rows,int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[100000];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        logger.info("generated maze in size " + maze.getMaze().length +"*" + maze.getMaze()[0].length);
                    }
                    catch (Exception var10)
                    {
                        logger.fatal("failed to generate maze");
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1)
        {
            logger.fatal("failed to connect with server to generate maze");
        }
    }

    private void CommunicateWithServer_SolveSearchProblem(Maze maze) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();
                        logger.info("maze solved with algorithem :" + Configurations.getConf().getSearchingAlgo());

                    } catch (Exception var10) {
                        logger.error("failed to solve maze");
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            logger.fatal("failed to connect server");
        }
    }
    @Override
    public boolean saveFile()
    {
        if(maze != null) {

            Path path = Paths.get("SavedMazes");
            if (!Files.exists(path)) {
                new File("SavedMazes").mkdir();
            }
            String savedMazeName = new Date().getTime() + ".maze";
            String mazeFileName = "SavedMazes" + '\\' + savedMazeName;
            try {
                OutputStream out = new MyCompressorOutputStream(new FileOutputStream(mazeFileName));
                out.write(maze.toByteArray());
                out.flush();
                out.close();
            } catch (IOException var8) {
                logger.error("couldn't save maze");
                return false;
            }
            logger.info("maze saved");
            return true;
        }
        return false;
    }
    @Override
    public boolean loadFile(String name) {

        byte[] savedMazeBytes = new byte[0];
        try {
            InputStream in = new MyDecompressorInputStream(new FileInputStream(name));
            savedMazeBytes = new byte[100000];
            in.read(savedMazeBytes);
            in.close();
        } catch (IOException var7) {
            logger.error("couldn't load maze");
            return false;
        }

        maze = new Maze(savedMazeBytes);
        movePlayer(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
        setChanged();
        notifyObservers("maze loaded");
        logger.info("maze loaded from file");
        return true;


    }

    @Override
    public void removeSolution() {
        this.solution = null;
        setChanged();
        notifyObservers("hide solution");

    }

    @Override
    public void setShowSolution(boolean b) {
        showSolution = b;
    }

}
