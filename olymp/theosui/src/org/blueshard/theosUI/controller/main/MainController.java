package org.blueshard.theosUI.controller.main;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.Main;
import org.blueshard.theosUI.utils.UIUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private MainControllerUtils mainControllerUtils = new MainControllerUtils(this);
    private MainControllerUtils.ServerStatusController serverStatusController = mainControllerUtils.getServerStatusController();

    //main

    @FXML protected AnchorPane root;
    @FXML protected Pane mainApplication;
    @FXML protected TextField searchBar;

    //choose items

    @FXML protected JFXButton myFiles;

    //server status

    @FXML protected Label serverStatusText;
    @FXML protected Label serverStatus;
    @FXML protected ImageView serverStatusImage;
    @FXML protected Label clientStatus;
    @FXML protected ImageView clientStatusImage;

    public void onMyFiles() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("resources/myFiles.fxml"));

        MyFilesController myFilesController = new MyFilesController();

        fxmlLoader.setController(myFilesController);

        searchBar.setPromptText("In '" + myFilesController.getWindowName() + "' suchen");

        UIUtils.addAutocompleteToTextField(searchBar, myFilesController.getSearchItems().keySet());

        mainApplication.getChildren().add(fxmlLoader.load());
    }

    public boolean isClientConnected() {
        return serverStatusController.clientConnected;
    }

    public void setClientConnected(boolean clientConnected) throws IOException, TranscoderException {
        serverStatusController.setClientConnected(clientConnected);
    }

    public boolean isServerOnline() {
        return serverStatusController.serverOnline;
    }

    public void setServerOnline(boolean serverOnline) throws IOException, TranscoderException {
        serverStatusController.setServerOnline(serverOnline);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setServerOnline(true);
            setClientConnected(true);
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }

    }

}
