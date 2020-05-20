package Model;
import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyModel extends Observable implements IModel{

    private Maze myMaze;
    private Position currentPos;
    private Solution mySol;
    private Server mazeGeneratorServer;
    private Server solveServer;
    //private int currRow;
    //private int currCol;


    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public MyModel() {

        }


    class ClientServerGenerationStrategy implements IClientStrategy{
        private int changingRows;
        private int changingCols;

        public ClientServerGenerationStrategy(int changingRows, int changingCols) {
            this.changingRows = changingRows;
            this.changingCols = changingCols;
        }


        @Override
        public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
            try {
                ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                toServer.flush();
                int[] mazeDimensions = new int[]{changingRows,changingCols};
                toServer.writeObject(mazeDimensions); //send maze dimensions to server
                toServer.flush();
                byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                byte[] decompressedMaze = new byte[mazeDimensions[0]*mazeDimensions[1] + 12 ]; //allocating byte[] for the decompressed maze -
                is.read(decompressedMaze); //Fill decompressedMaze with bytes
                //Maze mazeComing = new Maze(decompressedMaze);
                myMaze = new Maze(decompressedMaze);
                currentPos=myMaze.getStartPosition();
                myMaze.setCurrentPosition(currentPos);
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class ClientServerSolvingStrategy implements IClientStrategy{
        public Maze mazeToSolve;

        public ClientServerSolvingStrategy(Maze mazeToSolve) {
            this.mazeToSolve = mazeToSolve;
            mazeToSolve.setCurrentPosition(currentPos);
        }


        public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
            try {
                ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                toServer.flush();
                mazeToSolve.setCurrentPosition(currentPos);
                toServer.writeObject(mazeToSolve); //send maze dimensions to server
                toServer.flush();
                mySol = (Solution) fromServer.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }





    public Position getGoalPos(){
//        if(myMaze!=null) {
           return myMaze.getGoalPosition();

    }


    public void startServers() {
        //solveServer.start();
        //mazeGeneratorServer.start();
    }

    public void stopServers() {
        mazeGeneratorServer.stop();
        solveServer.stop();
    }

    public void close() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public int[][] getMaze() {
        if(myMaze==null){
            return null;
        }
        return myMaze.getMaze();
    }

    @Override
    public int getCharacterPositionRow() {
        return currentPos.getRowIndex();
    }

    @Override
    public int getCharacterPositionColumn() {
        return currentPos.getColumnIndex();
    }


    public void generateMaze(int width, int height) {
         mazeGeneratorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeGeneratorServer.start();
        System.out.println("Generation Started (MODEL) ");
        try{

            Client client = new Client(InetAddress.getLocalHost(),5400,new ClientServerGenerationStrategy(width,height));
            client.communicateWithServer();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mazeGeneratorServer.stop();
        System.out.println("Stop generating !! ");


    }
    public void solveMaze() {
        solveServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveServer.start();
        System.out.println("Generation Started (MODEL) ");
        try{

            Client client = new Client(InetAddress.getLocalHost(),5401,new ClientServerSolvingStrategy(myMaze));
            client.communicateWithServer();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        solveServer.stop();
        System.out.println("Stop solving !! ");


    }

    public boolean canMove(int row,int col){
        if(myMaze.getCell(row,col)==0){
            return true;
        }
        return false;
    }
    @Override
    public boolean moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                if(canMove(currentPos.getRowIndex()-1,currentPos.getColumnIndex())) {
                    currentPos = new Position(currentPos.getRowIndex() - 1, currentPos.getColumnIndex());
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case DOWN:
                if(canMove(currentPos.getRowIndex()+1,currentPos.getColumnIndex())) {
                    currentPos = new Position(currentPos.getRowIndex() + 1, currentPos.getColumnIndex());
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case RIGHT:
                if(canMove(currentPos.getRowIndex(),currentPos.getColumnIndex()+1)) {
                    currentPos = new Position(currentPos.getRowIndex(), currentPos.getColumnIndex() + 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case LEFT:
                if(canMove(currentPos.getRowIndex(),currentPos.getColumnIndex()-1)) {
                    currentPos = new Position(currentPos.getRowIndex(), currentPos.getColumnIndex() - 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD8:
                if(canMove(currentPos.getRowIndex()-1,currentPos.getColumnIndex())) {
                    currentPos = new Position(currentPos.getRowIndex() - 1, currentPos.getColumnIndex());
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD2:
                if(canMove(currentPos.getRowIndex()+1,currentPos.getColumnIndex())) {
                    currentPos = new Position(currentPos.getRowIndex() + 1, currentPos.getColumnIndex());
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD6:
                if(canMove(currentPos.getRowIndex(),currentPos.getColumnIndex()+1)) {
                    currentPos = new Position(currentPos.getRowIndex(), currentPos.getColumnIndex() + 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD4:
                if(canMove(currentPos.getRowIndex(),currentPos.getColumnIndex()-1)) {
                    currentPos = new Position(currentPos.getRowIndex(), currentPos.getColumnIndex() - 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD7:
                if(canMove(currentPos.getRowIndex()-1,currentPos.getColumnIndex()-1)) {
                    currentPos = new Position(currentPos.getRowIndex() - 1, currentPos.getColumnIndex() - 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD9:
                if(canMove(currentPos.getRowIndex()-1,currentPos.getColumnIndex()+1)) {
                    currentPos = new Position(currentPos.getRowIndex() - 1, currentPos.getColumnIndex() + 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD1:
                if(canMove(currentPos.getRowIndex()+1,currentPos.getColumnIndex()-1)) {
                    currentPos = new Position(currentPos.getRowIndex() + 1, currentPos.getColumnIndex() - 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;
            case NUMPAD3:
                if(canMove(currentPos.getRowIndex()+1,currentPos.getColumnIndex()+1)){
                    currentPos = new Position(currentPos.getRowIndex() + 1, currentPos.getColumnIndex() + 1);
                    myMaze.setCurrentPosition(currentPos);
                }
                break;

        }
        if(currentPos.equals(myMaze.getGoalPosition())){
            setChanged();
            notifyObservers();
            return true;
        }
        setChanged();
        notifyObservers();
        return false;
    }


    public Solution getSolution(){
        return mySol;
    }

    @Override
    public void saveGame(String gameName) {

        myMaze.setCurrentPosition(currentPos);
        System.out.println(gameName);
        String tempDir = System.getProperty("java.io.tmpdir");
        ObjectOutputStream fileToSave=null;
        FileOutputStream mazeToSave = null;
        try {
            mazeToSave = new FileOutputStream(tempDir+gameName+".bin");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fileToSave=new ObjectOutputStream(mazeToSave);
            fileToSave.writeObject(myMaze);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Maze loadGame(String chosenGame) {
        Maze loadedMaze=null;
        String tempDic = System.getProperty("java.io.tmpdir");
        try {
            String sss = tempDic+chosenGame+".bin";
            ObjectInputStream mazeToRead = new ObjectInputStream(new FileInputStream(tempDic+chosenGame+".bin"));
            loadedMaze = ((Maze) mazeToRead.readObject());
            myMaze = loadedMaze;
            currentPos= myMaze.getCurrentPosition();
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedMaze;
    }
}
