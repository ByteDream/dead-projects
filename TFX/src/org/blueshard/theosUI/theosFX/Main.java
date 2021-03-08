package org.blueshard.theosUI.theosFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        TFXFileItem a = new TFXFileItem("a", "a", "a", event -> event.toString());

        root.getChildren().add(a);

        primaryStage.setTitle("TFX Test");
        Scene scene = new Scene(root);
        //Scene scene = new Scene(root, 900, 470);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
