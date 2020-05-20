package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import static javafx.geometry.Pos.*;

public class MazeController implements IView,Observer {

    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Button btn_newMaze;
    public javafx.scene.control.TextField txtfld_gameName;
    public javafx.scene.control.Button btn_saveGame;
    public javafx.scene.control.Button btn_loadGame;
    public javafx.scene.control.ComboBox<String> cmb_loadGameOptions;
    public javafx.scene.control.MenuItem mni_save;
    public javafx.scene.control.TextField txt;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_mute;

    private MyViewModel mazeViewModel;
    private Scene mazeMainScene;
    private Stage mazeStage;

    public MazeDisplay mazeDis;
    private boolean music;

    //maze fields
    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");
    public int height;
    public int width;
    public int characterIndex;

    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
    }

    public void setViewModel(MyViewModel myViewModel){
        mazeViewModel=myViewModel;
    }


    public void KeyPressed(KeyEvent keyEvent) throws URISyntaxException {
        boolean isGoal=mazeViewModel.moveCharacter(keyEvent.getCode());
        if(!music) {
            switch (keyEvent.getCode()) {
                case UP:
                case DOWN:
                case RIGHT:
                case LEFT:
                case NUMPAD8:
                case NUMPAD2:
                case NUMPAD6:
                case NUMPAD4:
                case NUMPAD7:
                case NUMPAD9:
                case NUMPAD1:
                    mazeViewModel.playMusic(new Media(getClass().getResource("/Sounds/Jump.mp3").toURI().toString()), 60.0);
                    break;
                default:
            }
        }

        if(isGoal){
            try {
                mazeDis.setGoalImage(-1,-1);
            try{
                if(!music) {
                    mazeViewModel.playMusic(new Media(getClass().getResource("/Sounds/Win.mp3").toURI().toString()), 60.0);
                }
                Stage stage = new Stage();
                stage.setTitle("YOU WIN!!!");
                VBox layout = new VBox();
                HBox H = new HBox(5);
                H.setAlignment(CENTER);
                layout.setAlignment(CENTER);
                Button close = new Button();
                close.setText("Got it !");
                H.getChildren().add(close);
                layout.spacingProperty().setValue(30);
                Image im = new Image("/Images/GIF"+characterIndex%3+".gif");
                ImageView image = new ImageView(im);
                layout.getChildren().add(image);
                layout.getChildren().add(H);
                close.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        stage.close();
                    }
                });
                Scene scene = new Scene(layout, 400, 350);
                scene.getStylesheets().add(getClass().getResource("/View/LoadScene.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
                stage.show();


            } catch (Exception e) {
            }

        } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        keyEvent.consume();
    }

    public void saveGameClicked(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Save Game");


            FXMLLoader fxmlLoader = new FXMLLoader();
            //Parent root = fxmlLoader.load(getClass().getResource("SaveGame.fxml").openStream());

            VBox centerLayout = new VBox();
            centerLayout.setAlignment(CENTER);
            centerLayout.setSpacing(50);
            HBox H = new HBox(5);
            H.setAlignment(CENTER);
            txt = new TextField();
            txt.setPrefWidth(200);
            txt.setPrefHeight(10);
            Label name = new Label("Name:");

            name.setAlignment(CENTER_RIGHT);
            H.getChildren().add(name);
            H.getChildren().add(txt);



            Button button = new Button("Save");
            button.setPrefHeight(20);
            button.setPrefWidth(80);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String gameName = String.valueOf(txt.getText());
//                    btn_saveGame.setDisable(true);
                    mazeViewModel.saveGame(gameName);
                    stage.close();
                }
            });

            centerLayout.getChildren().addAll(H,button);

            Scene scene = new Scene(centerLayout, 300, 175);
            scene.getStylesheets().add(getClass().getResource("/View/LoadScene.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();

        } catch (Exception e) {
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDis.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        setResizeEvent();
        if (o == mazeViewModel) {
            displayMaze(mazeViewModel.getMaze());
           // btn_generateMaze.setDisable(false);
            try {
                mazeDis.setGoalImage(mazeViewModel.getGoalPos().getRowIndex(),mazeViewModel.getGoalPos().getColumnIndex());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            this.zoom(mazeDis);
        }
    }
    public void zoom(MazeDisplay pane) {
        pane.setOnScroll(
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        double zoomFactor = 1.05;
                        double deltaY = event.getDeltaY();

                        if (deltaY < 0) {
                            zoomFactor = 0.95;
                        }
                        pane.setScaleX(pane.getScaleX() * zoomFactor);
                        pane.setScaleY(pane.getScaleY() * zoomFactor);
                        event.consume();
                    }
                });


    }


    public void setResizeEvent() {
        this.mazeMainScene.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(mazeDis!=null) {
                mazeDis.setWidth((double) newValue * 0.75);

                mazeDis.redraw();

                if (mazeViewModel.getMaze() != null) {
                    try {
                        mazeDis.setGoalImage(mazeViewModel.getGoalPos().getRowIndex(), mazeViewModel.getGoalPos().getColumnIndex());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Width: " + newValue);
        });

        this.mazeMainScene.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(mazeDis!=null) {
                mazeDis.setHeight((double) newValue * 0.85);
                mazeDis.redraw();
                if (mazeViewModel.getMaze() != null) {
                    try {
                        mazeDis.setGoalImage(mazeViewModel.getGoalPos().getRowIndex(), mazeViewModel.getGoalPos().getColumnIndex());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Height: " + newValue);
        });
    }

    public void displayMaze(int[][] maze) {

        mazeDis.setCharacterIndex(characterIndex);
        mazeDis.setMaze(maze);
        int characterPositionRow = mazeViewModel.getCharacterPositionRow();
        int characterPositionColumn = mazeViewModel.getCharacterPositionColumn();
        mazeDis.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionColumn.set(characterPositionColumn + "");
        this.characterPositionRow.set(characterPositionRow + "");
        // btn_solveMaze.setDisable(false);
    }


    public void loadGameFromMain(String chosenGame){

        try {
            mazeViewModel.playMusic((new Media(getClass().getResource("/Sounds/letsGo.mp3").toURI().toString())),200.0);
            mazeViewModel.playMusic((new Media(getClass().getResource("/Sounds/Dance.mp3").toURI().toString())),10.0);
            music=true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mazeViewModel.loadGame(chosenGame);

    }
    public void generateMaze(int height, int width,int index){
        this.height = height;
        this.width = width;
        characterIndex=index;
        bindProperties();
        try {
            mazeViewModel.pauseMusic();
            if(!music) {
                mazeViewModel.playMusic((new Media(getClass().getResource("/Sounds/letsGo.mp3").toURI().toString())), 200.0);
                mazeViewModel.playSound(new Media(getClass().getResource("/Sounds/Dance.mp3").toURI().toString()), 10.0);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mazeViewModel.generateMaze(height,width);
    }



    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About us");
            VBox layout = new VBox();
            layout.setAlignment(CENTER);
            Button close = new Button();
            close.setText("OK");
            Label label = new Label("This game was designed by ISE students\nnamed: Zahi Kapri & Tzach Finkelstein.\nThe Maze generator engine was based\non prim's algorithm.\nSolving way is by BFS algorithm.\nFind Peach and HAVE FUN! ©");
            label.setAlignment(CENTER);
            label.setLineSpacing(2);
            layout.spacingProperty().setValue(30);


            layout.getChildren().addAll(label,close);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                }
            });
            Scene scene = new Scene(layout, 600, 270);
            scene.getStylesheets().add(getClass().getResource("/View/LoadScene.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();


        } catch (Exception e) {
        }
    }


    public void LoadGameClicked(ActionEvent actionEvent) {
        cmb_loadGameOptions = setMazeNames();
        mazeDis.firstRedraw = true;
        try {
            Stage stage = new Stage();
            stage.setTitle("Load Game");
            VBox layout = new VBox();
            layout.setAlignment(CENTER);
            Label lbl_load = new Label();
            lbl_load.setText("Please choose\na game to load");
            layout.getChildren().add(lbl_load);
            layout.getChildren().add(cmb_loadGameOptions );
            Button btn_load = new Button();
            btn_load.setText("Load");
            layout.spacingProperty().setValue(30);
            layout.getChildren().add(btn_load);
            //layout.getChildren().add(btn_loadGame );
            cmb_loadGameOptions.setPromptText("Maze Name");
            cmb_loadGameOptions.setId("cmbLoad");
            btn_load.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    mazeViewModel.loadGame(cmb_loadGameOptions.getValue());
                    stage.close();
                }
            });
            //FXMLLoader fxmlLoader = new FXMLLoader();
            //Parent root = fxmlLoader.load(getClass().getResource("LoadGame.fxml").openStream());
            Scene scene = new Scene(layout, 350, 175);
            scene.getStylesheets().add(getClass().getResource("/View/LoadScene.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            //setMazeNames();
            //MyViewController view = fxmlLoader.getController();
            //view.initialize(viewModel,stage,scene);



        } catch (Exception e) {
        }
    }

    public final ComboBox<String> setMazeNames() {

        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        File folder = new File(tempDirectoryPath);
        File[] listOfFiles = folder.listFiles();
        ObservableList<String> options = FXCollections.observableArrayList();

        for (int i = 0; i < listOfFiles.length; i++) {
            String tempEnd = listOfFiles[i].getName();
            if(tempEnd.length()>4) {
                tempEnd = tempEnd.substring(tempEnd.length() - 3, tempEnd.length());
            }
            if(tempEnd.equals("bin")){
                options.add(listOfFiles[i].getName().substring(0,listOfFiles[i].getName().length()-4));
                //cmb_loadGameOptions.getItems().add(listOfFiles[i].getName().substring(0,listOfFiles[i].getName().length()-4));
            }
        }
        ComboBox cmb= new ComboBox<String>(options);
        return cmb;
    }

    public void setMazeStage(Stage mazeStage) {
        this.mazeStage = mazeStage;
    }

    public void setMazeMainScene(Scene mazeMainScene) {
        this.mazeMainScene = mazeMainScene;
    }

    public void generateNewMaze(ActionEvent actionEvent) {
        mazeDis.firstRedraw = true;
        generateMaze(height,width,characterIndex);
    }
    public void solveMaze(ActionEvent actionEvent) {
        //showAlert("Solving maze..");
        Solution sol=mazeViewModel.solveMaze();
        mazeDis.redrawSolution(sol);

    }
    //</editor-fold>






    public void howToPlayScene(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("HOW TO PLAY");
            VBox layout = new VBox();
            HBox H = new HBox(5);
            H.setAlignment(CENTER);
            layout.setAlignment(CENTER);
            Button close = new Button();
            close.setText("Got it !");
            H.getChildren().add(close);
            layout.spacingProperty().setValue(30);
            Image im = new Image("/Images/keyboard.jpeg");
            ImageView image = new ImageView(im);
            layout.getChildren().add(image);
            layout.getChildren().add(H);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                }
            });
            Scene scene = new Scene(layout, 950, 350);
            scene.getStylesheets().add(getClass().getResource("/View/LoadScene.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();


        } catch (Exception e) {
        }

    }


    public void closeEvent(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            // Close the program properly
            mazeViewModel.closeModel();
            mazeStage.close();
        } else {
            // ... user chose CANCEL or closed the dialog
            alert.close();
        }

    }

    public void mute(ActionEvent actionEvent) {
        btn_mute=new Button();
        if(!music){
            mazeViewModel.mute(true);
            Image muteImg = new Image("/Images/mute.png");
            btn_mute=(Button)actionEvent.getSource();
            btn_mute.setGraphic(new ImageView(muteImg));

//            BackgroundImage backImg= new BackgroundImage(muteImg,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
//            Background muteBackground = new Background(backImg);
//            btn_mute.setBackground(muteBackground);
            music=true;
        }else{
            mazeViewModel.mute(false);
            Image muteImg = new Image("/Images/speaker.png");
            btn_mute=(Button)actionEvent.getSource();
            btn_mute.setGraphic(new ImageView(muteImg));
//            Image muteImg = new Image("/Images/speaker.png");
//            BackgroundImage backImg= new BackgroundImage(muteImg,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
//            Background muteBackground = new Background(backImg);
//            btn_mute.setBackground(muteBackground);
            music=false;
        }

    }
}