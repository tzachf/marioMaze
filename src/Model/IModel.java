package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

public interface IModel {
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void generateMaze(int width, int height);
    boolean moveCharacter(KeyCode movement);
    Position getGoalPos();
    void solveMaze();
    Solution getSolution();
    void saveGame(String gameName);
    void close();

    Maze loadGame(String chosenGame);
}
