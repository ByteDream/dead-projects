package org.blueshard.theosUI.theosFX;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import org.blueshard.theosUI.theosFX.controller.TFXFileItemController;

import java.io.IOException;

public class TFXFileItem extends BorderPane {

    private final TFXFileItemController controller;

    public TFXFileItem(String filename, String date, String size, EventHandler<ActionEvent> onDownload) {
        MenuItem download = new MenuItem("Download");
        download.setOnAction(onDownload);

        controller = new TFXFileItemController(download);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/TFXFileItem.fxml"));
            loader.setController(controller);

            BorderPane borderPane = loader.load();

            controller.filename.setText(filename);
            controller.date.setText(date);
            controller.size.setText(size);

            this.getChildren().add(borderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return controller.filename.getText();
    }

    public String getDate() {
        return controller.date.getText();
    }

    public String getSize() {
        return controller.size.getText();
    }

}
