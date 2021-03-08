package org.blueshard.theosUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.blueshard.theosUI.theosFX.TFXFileItem;
import org.blueshard.theosUI.utils.UIUtils;

public class Test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane main = new TFXFileItem("null", "null", "null");

        Scene scene = new Scene(main);

        primaryStage.setTitle(UIUtils.HEADING);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
