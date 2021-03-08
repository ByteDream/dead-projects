package org.blueshard.theosUI.theosFX.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.theosFX.TFXPasswordField;
import org.blueshard.theosUI.theosFX.utils.SVGToGenericImage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class TFXPasswordFieldController implements Initializable {

    private String promptText = "";
    private String text = "";
    private ImageView hideImage;
    private ImageView showImage;
    private boolean hide = true;

    public AnchorPane root;
    public JFXPasswordField passwordField;
    public JFXTextField clearPasswordField;
    public Button passwordShowHideButton;

    public TFXPasswordFieldController(String promptText, String text) {
        if (promptText != null) {
            this.promptText = promptText;
        }
        if (text != null) {
            this.text = text;
        }
    }

    public boolean isHidden() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;

        if (hide) {
            passwordField.setText(clearPasswordField.getText());
            passwordField.setVisible(true);
            clearPasswordField.setVisible(false);
            passwordShowHideButton.setGraphic(hideImage);
        } else {
            clearPasswordField.setText(passwordField.getText());
            clearPasswordField.setVisible(true);
            passwordField.setVisible(false);
            passwordShowHideButton.setGraphic(showImage);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            hideImage = new ImageView(new Image(new SVGToGenericImage(TFXPasswordField.class.getResourceAsStream("resources/images/password_hide.svg"),
                    SVGToGenericImage.Transcoder.PNG, (float) passwordShowHideButton.getPrefHeight(), (float) 28).asByteArrayInputStream()));
            showImage = new ImageView(new Image(new SVGToGenericImage(TFXPasswordField.class.getResourceAsStream("resources/images/password_show.svg"),
                    SVGToGenericImage.Transcoder.PNG, (float) passwordShowHideButton.getPrefHeight(), (float) 28).asByteArrayInputStream()));
            passwordField.setPromptText(promptText);
            passwordField.setText(text);
            clearPasswordField.setPromptText(promptText);
            clearPasswordField.setText(text);
            if (hide) {
                passwordField.setVisible(true);
                clearPasswordField.setVisible(false);
                passwordShowHideButton.setGraphic(hideImage);
            } else {
                clearPasswordField.setVisible(true);
                passwordField.setVisible(false);
                passwordShowHideButton.setGraphic(showImage);
            }

            passwordShowHideButton.setOnAction(event -> setHide(!hide));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
    }
}
