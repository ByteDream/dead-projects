package org.blueshard.theosUI.theosFX.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.theosFX.TFXFileItem;
import org.blueshard.theosUI.theosFX.utils.SVGToGenericImage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TFXFileItemController implements Initializable {

    private MenuItem[] items;

    @FXML public BorderPane root;
    @FXML public Button filename;
    @FXML public Label date;
    @FXML public Label size;
    @FXML public Button optionsButton;
    @FXML public ContextMenu options;

    public TFXFileItemController(MenuItem... items) {
        this.items = new MenuItem[items.length];
        System.arraycopy(items, 0, this.items, 0, items.length);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        options.getItems().addAll(items);
        try {
            optionsButton.setGraphic(new ImageView(new Image(new SVGToGenericImage(TFXFileItem.class.getResourceAsStream("resources/images/option_dots.svg"),
                    SVGToGenericImage.Transcoder.PNG, (float) optionsButton.getPrefHeight(), (float) optionsButton.getPrefWidth()).asByteArrayInputStream())));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        optionsButton.setContextMenu(options);
    }
}