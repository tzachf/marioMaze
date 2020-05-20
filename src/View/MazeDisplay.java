package View;

import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplay extends Canvas {

    private int[][] maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int goalRowPos;
    private int goalColPos;
    private int characterIndex;
    static public boolean firstRedraw=true;
    static private boolean secondRun=true;
    private int wallindex =100000;
    GraphicsContext gc;
    private int [][] index;
    public void setMaze(int[][] maze) {
        this.maze = maze;
        if(firstRedraw) {
            index = new int[maze.length][maze[0].length];
        }
        redraw();
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;

        redraw();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    public void redrawSolution(Solution solution) {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;


            Image solPos = new Image("/Images/Coins.png");


            ArrayList<AState> arrayList = solution.getSolutionPath();
//                for (int i = 0; i < arrayList.size(); i++) {
//                }

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    MazeState temp = new MazeState(j, i);
                    if (arrayList.contains(temp)) {
                        if ((temp.getRow() == characterPositionColumn && temp.getCol() == characterPositionRow) || ((temp.getRow() == goalRowPos && temp.getCol() == goalColPos)) ){

                        }
                        else
                        gc.drawImage(solPos, i * cellWidth, j * cellHeight, cellWidth, cellHeight);

                    }
                }
            }

        }


    }


    public void redraw() {

        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

//            try {

                //   Image goalPos = new Image("/Images/wall1.jpg");
                String strCharacterIndex = characterIndex%3+"";
                Image characterImage = new Image("Images/maze"+strCharacterIndex+".png");
                Image wallImage0 = new Image("Images/marioWall0.png");
                Image wallImage1 = new Image("Images/marioWall1.png");
                Image wallImage2 = new Image("Images/marioWall2.png");
                Image wallImage3 = new Image("Images/marioWall3.png");
                Image wallImage4 = new Image("Images/marioWall4.png");
                Image wallImage5 = new Image("Images/marioWall5.png");
                gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze

                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            if(firstRedraw) {
                                switch (wallindex % 6) {
                                    case 0:
                                        gc.drawImage(wallImage0, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                    case 1:
                                        gc.drawImage(wallImage1, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                    case 2:
                                        gc.drawImage(wallImage2, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                    case 3:
                                        gc.drawImage(wallImage3, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                    case 4:
                                        gc.drawImage(wallImage4, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                    case 5:
                                        gc.drawImage(wallImage5, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                                        index[i][j] = wallindex;
                                        break;
                                }
                                wallindex++;
                                secondRun = true;
                            }
                            else
                            {

                                switch (index[i][j] % 6) {
                                    case 0:
                                        gc.drawImage(wallImage0, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;
                                    case 1:
                                        gc.drawImage(wallImage1, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;
                                    case 2:
                                        gc.drawImage(wallImage2, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;
                                    case 3:
                                        gc.drawImage(wallImage3, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;
                                    case 4:
                                        gc.drawImage(wallImage4, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;
                                    case 5:
                                        gc.drawImage(wallImage5, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                                        break;


                                }
                            }
                        }
                    }
                }

            firstRedraw=false;
                //Draw Character

                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);

//            }
        }
        //System.out.println(cu);
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();


    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public void setGoalImage(int x, int y) throws FileNotFoundException {

        Image goalPos = new Image("/Images/degree.jpg");
        String strCharacterIndex = characterIndex%3+"";
        Image characterImage = new Image("Images/win"+strCharacterIndex+".png");
        if(x>0||y>0) {
            this.goalColPos = x;
            this.goalRowPos = y;
        }
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / maze.length;
        double cellWidth = canvasWidth / maze[0].length;
        //    gc.setFill(Color.RED);
        //  gc.fillOval(x * cellHeight, y * cellWidth, cellHeight, cellWidth);
        if(x>0||y>0) {
            gc.drawImage(goalPos, y * cellWidth, x * cellHeight, cellWidth, cellHeight);
        }
        else{
            redraw();
            gc.clearRect(goalRowPos*cellWidth,goalColPos*cellHeight,cellWidth,cellHeight);
            gc.drawImage(characterImage, goalRowPos * cellWidth, goalColPos * cellHeight, cellWidth, cellHeight);

        }

    }

    public void setCharacterIndex(int characterIndex) {
        this.characterIndex = characterIndex;
    }



}
