package org.blueshard.theosUI.UIStarter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.utils.SVGToGenericImage;
import org.blueshard.theosUI.utils.TheosUIImage;
import org.blueshard.theosUI.utils.UIUtils;

import java.io.IOException;

public class Login {

    private final Stage rootStage = new Stage();

    public Login(Window rootWindow) throws IOException {
        rootStage.initOwner(rootWindow);
        rootStage.initModality(Modality.WINDOW_MODAL);

        AnchorPane login = FXMLLoader.load(Login.class.getResource("../resources/login.fxml"));
        Scene scene = new Scene(login);

        rootStage.setOnCloseRequest(event -> {
            this.onClose();
        });

        rootStage.setTitle(UIUtils.HEADING);
        //rootStage.setResizable(false);
        rootStage.setScene(scene);
    }

    public void onClose() {
        UIUtils.onClose();
    }

    public Stage getStage() {
        return rootStage;
    }

}
