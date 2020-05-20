package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.*;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import static javafx.geometry.Pos.CENTER;

public class MyViewController implements IView, Observer {

    //controls
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_loadGame;
    public javafx.scene.control.ComboBox <String> cmb_loadGameOptions;
    public javafx.scene.image.ImageView img_character;
    public javafx.scene.image.Image img;
    public static int characterIndex=999;
    public javafx.scene.control.MenuItem mni_save;
    //fxml
    private MyViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;
    boolean solving;

    public MazeDisplay mazeDisplayer;

    //Properties - For Binding
    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");

    public void initialize(MyViewModel viewModel, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        this.solving=false;
//        bindProperties();                                                                                     /// move to mazeDisPlay !!!!!!!
        //setResizeEvent();
    }



    @Override
    public void update(Observable o, Object arg) {
    }

    public boolean isNumeric(String str){
        try{
            Double.parseDouble(str);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    public void generateMaze() {
        //playSong();
        int height=0;
        int width=0;
        boolean numeric=true;
        mainStage.setResizable(true);
        if(!isNumeric(txtfld_columnsNum.getText())||!isNumeric(txtfld_rowsNum.getText())) {
            showAlert("Please choose valid numbers!");
            numeric = false;
        }
        if(numeric) {
            height = Integer.valueOf(txtfld_rowsNum.getText());
            width = Integer.valueOf(txtfld_columnsNum.getText());
            if (height <= 0 || width <= 0) {
                showAlert("Please choose a number greater than 0");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                try {
                    Parent root = fxmlLoader.load(getClass().getResource("/View/maze.fxml").openStream());
                    Scene scene = new Scene(root, 960, 540);
                    scene.getStylesheets().add(getClass().getResource("/View/Maze.css").toExternalForm());
                    mainStage.setScene(scene);
                    MazeController mazeController = fxmlLoader.getController();
                    mazeController.setViewModel(viewModel);
                    mazeController.setMazeMainScene(scene);
                    mainStage.setHeight(700);
                    mainStage.setWidth(720);
                    mazeController.setMazeStage(mainStage);
                    viewModel.addObserver(mazeController);

                    viewModel.pauseMusic();
                    //viewModel.playMusic((new Media(getClass().getResource("/Sounds/letsGo.mp3").toURI().toString())),200.0);
                    mazeController.generateMaze(height, width, characterIndex);

                    mainStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }


    public void LoadGameClicked(ActionEvent actionEvent) { //playSong();
        mainStage.setResizable(true);
        mazeDisplayer.firstRedraw=true;
        cmb_loadGameOptions = setMazeNames();
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
                    //Maze loadedMaze=viewModel.loadGame(cmb_loadGameOptions.getValue());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    try {
                        Parent root = fxmlLoader.load(getClass().getResource("/View/maze.fxml").openStream());
                        Scene scene = new Scene(root, 960,540);
                        scene.getStylesheets().add(getClass().getResource("/View/Maze.css").toExternalForm());
                        mainStage.setScene(scene);
                        MazeController mazeController=fxmlLoader.getController();
                        mazeController.setViewModel(viewModel);
                        mazeController.setMazeMainScene(scene);
                        mainStage.setHeight(700);
                        mainStage.setWidth(720);
                        mazeController.setMazeStage(mainStage);
                        viewModel.addObserver(mazeController);
                        viewModel.pauseMusic();
                        viewModel.playMusic((new Media(getClass().getResource("/Sounds/letsGo.mp3").toURI().toString())),200.0);

                        mazeController.loadGameFromMain(cmb_loadGameOptions.getValue());

                        mainStage.show();
                        stage.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

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
        ObservableList <String> options = FXCollections.observableArrayList();

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

    public void changePhotoLeft(ActionEvent actionEvent) {

        characterIndex--;
        int currentIndex= characterIndex%3;
        String currChar = currentIndex +"";
        img = new Image("/Images/"+currChar+".png");
        img_character.setImage(img);
    }

    public void changePhotoRight(ActionEvent actionEvent) {

        characterIndex++;
        int currentIndex= characterIndex%3;
        String currChar = currentIndex +"";
        img = new Image("/Images/"+currChar+".png");
        img_character.setImage(img);
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



    public void closeEvent(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            // Close the program properly
            viewModel.closeModel();
            mainStage.close();
        } else {
            // ... user chose CANCEL or closed the dialog
            alert.close();
        }

    }

    public void propConfig(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties Configuration");
            VBox layout = new VBox();
            HBox H = new HBox(3);

            Label genLabel = new Label("Choose Generator:");
            ComboBox<String> genCmb = new ComboBox<>();
            genCmb.setPromptText("Generating Algorithm");
            genCmb.getItems().addAll("simpleMazeGenerator", "myMazeGenerator");
            H.getChildren().addAll(genLabel,genCmb);
            layout.getChildren().add(H);
            H.setAlignment(CENTER);

            HBox H1 = new HBox(3);
            Label solLabel = new Label("Choose Solver:");
            ComboBox<String> solCmb = new ComboBox<>();
            solCmb.setPromptText("Solving Algorithm");
            solCmb.getItems().addAll("DFS", "BFS","Best");
            H1.getChildren().addAll(solLabel,solCmb);
            layout.getChildren().add(H1);
            H1.setAlignment(CENTER);

            layout.setAlignment(CENTER);




            Button close = new Button();
            close.setText("OK");
            layout.spacingProperty().setValue(30);





            layout.getChildren().addAll(close);
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream("./resources/config.properties");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String str = "AlgorithmSelect = "+solCmb.getValue() +"\nGeneratorSelect = "+genCmb.getValue() ;

                    byte[] bytesss = str.getBytes();
                    try {
                        out.write(bytesss);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

}
