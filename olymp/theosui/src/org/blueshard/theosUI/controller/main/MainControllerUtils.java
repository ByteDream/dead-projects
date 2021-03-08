package org.blueshard.theosUI.controller.main;

import javafx.scene.image.Image;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.theosFX.utils.SVGToGenericImage;
import org.blueshard.theosUI.utils.TheosUIImage;

import java.io.IOException;

public class MainControllerUtils {

    private MainController mainController;

    public MainControllerUtils(MainController mainController) {
        this.mainController = mainController;
    }

    public ServerStatusController getServerStatusController() {
        return new ServerStatusController();
    }

    public class ServerStatusController {

        protected boolean clientConnected;
        protected boolean serverOnline;

        public void setClientConnected(boolean clientConnected) throws IOException, TranscoderException {
            this.clientConnected = clientConnected;
            if (clientConnected) {
                mainController.clientStatus.setText("Verbunden");
                mainController.clientStatusImage.setImage(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.green_dot).asStream(),
                        SVGToGenericImage.Transcoder.PNG, (float) mainController.clientStatusImage.getFitHeight(), (float) mainController.clientStatusImage.getFitWidth()).asByteArrayInputStream()));
            } else {
                mainController.clientStatus.setText("Nicht verbunden");
                mainController.clientStatusImage.setImage(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.red_dot).asStream(),
                        SVGToGenericImage.Transcoder.PNG, (float) mainController.clientStatusImage.getFitHeight(), (float) mainController.clientStatusImage.getFitWidth()).asByteArrayInputStream()));
            }
        }

        public void setServerOnline(boolean serverOnline) throws IOException, TranscoderException {
            this.serverOnline = serverOnline;
            if (serverOnline) {
                mainController.serverStatus.setText("Online");
                mainController.serverStatusImage.setImage(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.green_dot).asStream(),
                        SVGToGenericImage.Transcoder.PNG, (float) mainController.serverStatusImage.getFitHeight(), (float) mainController.serverStatusImage.getFitWidth()).asByteArrayInputStream()));
            } else {
                mainController.serverStatus.setText("Offline");
                mainController.serverStatusImage.setImage(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.red_dot).asStream(),
                        SVGToGenericImage.Transcoder.PNG, (float) mainController.serverStatusImage.getFitHeight(), (float) mainController.serverStatusImage.getFitWidth()).asByteArrayInputStream()));
            }
        }

    }

}
