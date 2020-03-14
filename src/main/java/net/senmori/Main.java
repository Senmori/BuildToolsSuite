package net.senmori;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.senmori.project.ApplicationDetails;
import net.senmori.project.spigot.SpigotProject;
import net.senmori.project.spigot.config.SpigotConfig;
import net.senmori.storage.Directory;
import net.senmori.util.FileUtil;

public class Main extends Application {
    public static final Directory WORKING_DIR = new Directory(System.getProperty("user.dir"), "BTSuite");
    private static Scene scene;

    private static AnchorPane rootPane;
    private static SpigotProject spigotProject;
    private static ApplicationDetails applicationDetails;

    @Override
    public void init() throws Exception {
        WORKING_DIR.getFile().mkdirs();
        applicationDetails = new ApplicationDetails(this);
        spigotProject = new SpigotProject();
        spigotProject.initSettings(applicationDetails);
    }

    @Override
    public void start(Stage stage) throws IOException {
        rootPane = new AnchorPane();
        rootPane.setPrefSize(600.0D, 565.0D);
        rootPane.setMinWidth(Region.USE_PREF_SIZE);
        rootPane.setMinHeight(Region.USE_PREF_SIZE);
        //VBox children
        TextArea textArea = new TextArea();
        textArea.setPrefSize(200.0D, 100.0D);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox vBox = new VBox(textArea);
        vBox.setPrefSize(100.0D, 200.0D);
        AnchorPane.setBottomAnchor(vBox, 0.0D);
        AnchorPane.setLeftAnchor(vBox, 0.0D);
        AnchorPane.setRightAnchor(vBox, 0.0D);
        AnchorPane.setTopAnchor(vBox, 0.0D);

        rootPane.getChildren().add(vBox);

        scene = new Scene(loadFXML("main"));
        scene.setRoot(rootPane);
        stage.sizeToScene();
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(scene.getWidth());
        stage.setMinHeight(scene.getHeight());


        File workingDirectory = applicationDetails.getWorkingDirectory().getFile();
        printToConsole(textArea, workingDirectory.getAbsolutePath());
        Desktop.getDesktop().open(workingDirectory);
    }

    private static void printToConsole(TextArea console, String text) {
        Platform.runLater(() -> console.appendText(text + System.lineSeparator()));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}