package org.blueshard.theosUI.utils;

import com.sun.javafx.css.SizeUnits;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.Main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UIUtils {

    public static final String HEADING = "TheosUI | Version 0.1.0";

    public static void advancedTooltipInstall(Node node, Tooltip tooltip, int delay) {
        node.setOnMouseEntered(event -> Platform.runLater(() -> {
            node.onMouseEnteredProperty();
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> tooltip.show(node.getScene().getWindow(), event.getScreenX(), event.getScreenY() + 15));
            });
            thread.start();
        }));
        node.setOnMouseExited(event -> Platform.runLater(() -> {
            node.onMouseExitedProperty();
            tooltip.hide();
        }));
    }

    public static void addAutocompleteToTextField(TextField textField, Set<String> entries) {
        ContextMenu autocompletePopup = new ContextMenu();

        for (String s: entries) {
            MenuItem menuItem = new MenuItem(s);

            menuItem.setOnAction(event -> textField.setText(menuItem.getText()));

            autocompletePopup.getItems().add(menuItem);
        }

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                autocompletePopup.hide();
            } else {
                autocompletePopup.show(textField, Side.BOTTOM, 0, 0);
            }
        });
    }

    public static BorderPane createFileItem(TheosFile theosFile, boolean download) throws IOException, TranscoderException {
        BorderPane fileItem = FXMLLoader.load(Main.class.getResource("resources/myFiles.fxml"));

        ImageView fileImage = (ImageView) fileItem.lookup("#fileImage");
        fileImage.setImage(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.question_mark).asStream(),
                SVGToGenericImage.Transcoder.PNG, (float) fileImage.getFitHeight(), (float) fileImage.getFitWidth()).asByteArrayInputStream()));

        Button fileName = (Button) fileItem.lookup("#fileName");
        fileName.setText(theosFile.getFilename());

        Label date = (Label) fileItem.lookup("#date");
        date.setText(theosFile.getDateOfUpload());

        Label size = (Label) fileItem.lookup("#size");
        size.setText(theosFile.getSize().toMegabyte() + "MB");

        return fileItem;
    }

    public static void errorAlert(String title, String message, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);

        if (title == null) {
            alert.setTitle("Error");
        } else {
            alert.setTitle(title);
        }

        if (message == null) {
            message = "Error";
        }
        if (throwable == null) {
            alert.getDialogPane().setContent(new Label("Error: " + message));
        } else {
            alert.getDialogPane().setContent(new Label("Error: " + message + "\n\n" + throwable));
        }

        alert.show();
    }

    public static void onClose() {
        Platform.exit();
        Main.getAction().closeConnection();
    }

    public static void warningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);

        if (title == null) {
            alert.setTitle("Warning");
        } else {
            alert.setTitle(title);
        }
        alert.getDialogPane().setContent(new Label(message));
        alert.show();
    }

}
