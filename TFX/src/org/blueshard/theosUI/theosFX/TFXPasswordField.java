package org.blueshard.theosUI.theosFX;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.theosFX.controller.TFXPasswordFieldController;

import java.io.IOException;

public class TFXPasswordField extends AnchorPane {

    private BooleanProperty hide = new SimpleBooleanProperty(true);
    private StringProperty promptText = new SimpleStringProperty("Password");
    private StringProperty text = new SimpleStringProperty();

    private ObjectProperty<Paint> focusColor = new SimpleObjectProperty<>(new JFXTextField().getFocusColor());
    private BooleanProperty labelFloat = new SimpleBooleanProperty(false);
    private ObjectProperty<Paint> unFocusColor = new SimpleObjectProperty<>(new JFXTextField().getUnFocusColor());

    private TFXPasswordFieldController controller;

    public TFXPasswordField() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/TFXPasswordField.fxml"));

            controller = new TFXPasswordFieldController(promptText.get(), text.get());
            loader.setController(controller);

            AnchorPane anchorPane = loader.load();

            this.getChildren().add(anchorPane);

            this.styleProperty().addListener((observable, oldValue, newValue) -> {
                controller.root.setStyle(newValue);
                controller.passwordField.setStyle(newValue);
                controller.clearPasswordField.setStyle(newValue);
            });

            this.getStylesheets().addListener((ListChangeListener<String>) c -> {
                controller.root.getStylesheets().setAll(c.getList());
                controller.passwordField.getStylesheets().setAll(c.getList());
                controller.clearPasswordField.getStylesheets().setAll(c.getList());
            });

            double newWidth;
            if (this.getPrefWidth() == -1) {
                newWidth = 197;
            } else {
                newWidth = this.getPrefWidth() - 42;
            }

            this.controller.root.setPrefWidth(this.getPrefWidth());
            this.controller.passwordField.setPrefWidth(newWidth);
            this.controller.clearPasswordField.setPrefWidth(newWidth);
            this.controller.passwordShowHideButton.setLayoutX(newWidth);
            this.controller.passwordField.textProperty().addListener((observable, oldValue, newValue) -> this.text.set(newValue));
            this.controller.clearPasswordField.textProperty().addListener((observable, oldValue, newValue) -> this.text.set(newValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTextListener(ChangeListener<? super String> listener) {
        controller.passwordField.textProperty().addListener(listener);
        controller.clearPasswordField.textProperty().addListener(listener);
    }

    public JFXPasswordField getPasswordField() {
        return controller.passwordField;
    }

    public JFXTextField getClearPasswordField() {
        return controller.clearPasswordField;
    }

    public final boolean getHide() {
        return hide.get();
    }

    public final void setHide(boolean hide) throws TranscoderException {
        this.hide.setValue(hide);
        controller.setHide(hide);
    }

    public final String getPromptText() {
        return promptText.get();
    }

    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    public final String getText() {
        return text.get();
    }

    public final void setText(String text) {
        this.text.set(text);
    }

    public final Paint getFocusColor() {
        return focusColor.get();
    }

    public final void setFocusColor(Paint color) {
        focusColor.set(color);
        controller.passwordField.setFocusColor(color);
        controller.clearPasswordField.setFocusColor(color);
    }

    public final boolean getLabelFloat() {
        return labelFloat.get();
    }

    public final void setLabelFloat(boolean labelFloat) {
        this.labelFloat.set(labelFloat);
        controller.passwordField.setLabelFloat(labelFloat);
        controller.clearPasswordField.setLabelFloat(labelFloat);
    }

    public final Paint getUnFocusColor() {
        return unFocusColor.get();
    }

    public final void setUnFocusColor(Paint color) {
        unFocusColor.set(color);
        controller.passwordField.setUnFocusColor(color);
        controller.clearPasswordField.setUnFocusColor(color);
    }
}
