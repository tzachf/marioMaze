package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer  {
    private IModel model;
    private MediaPlayer mdp_media;
    private MediaPlayer mdp_music;

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if(o == model){

            setChanged();
            notifyObservers();
        }
    }

    public boolean moveCharacter(KeyCode movement){

        return model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public boolean isNull(){
        if(mdp_music==null){
            return  true;
        }
        return false;
    }
    public int getCharacterPositionRow() {
        //return characterPositionRowIndex;
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        //return characterPositionColumnIndex;
        return model.getCharacterPositionColumn();
    }

    public void generateMaze(int width, int height) {
        model.generateMaze(width, height);
    }

    public Solution solveMaze(){
        model.solveMaze();
        return model.getSolution();

    }

    public void playSound(Media media,double vol){
        mdp_music=new MediaPlayer(media);
        mdp_music.setVolume(vol);
        mdp_music.play();
        //mdp_media.setAutoPlay(true);
    }
    public void playMusic(Media media,double vol){
        mdp_media=new MediaPlayer(media);
        mdp_media.setVolume(vol);
        mdp_media.play();
        //mdp_media.setAutoPlay(true);
    }
    public void pauseMusic(){
        if(mdp_media!=null) {
            mdp_media.stop();
        }
        if(mdp_music!=null) {
            mdp_music.stop();
        }
    }
    public void mute(boolean mute){
        mdp_music.setMute(mute);
        mdp_media.setMute(mute);
        mdp_media.setAutoPlay(!mute);
        if(!mute){
            mdp_music.play();
        }
    }

    public void closeModel(){
        model.close();
    }

    public Position getGoalPos(){
        return model.getGoalPos();
    }

    public void saveGame(String gameName) {
        model.saveGame(gameName);
    }

    public Maze loadGame(String chosenGame) {
        return model.loadGame(chosenGame);
    }

}
