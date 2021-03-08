package org.blueshard.theosUI.theosFX;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import org.blueshard.theosUI.theosFX.controller.TFXScrollButtonController;

import java.io.IOException;

public class TFXScrollButton extends JFXButton {

    private ObjectProperty<Paint> backgroundColor = new SimpleObjectProperty<>();

    public TFXScrollButtonController controller;

    public TFXScrollButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/TFXScrollButton.fxml"));

            controller = new TFXScrollButtonController();
            loader.setController(controller);

            JFXButton jfxButton = loader.load();

            jfxButton.setPrefSize(this.getPrefWidth(), this.getHeight());

            this.getChildren().add(jfxButton);
        } catch (IOException e) {

        }
    }

    public void setImage(Image image) {
        this.setGraphic(new ImageView(image));
    }

    public void setImage(String image) {
        this.setGraphic(new ImageView(image));
    }

    public void setImage(ImageView imageView) {
        this.setGraphic(imageView);
    }

    public Paint getBackgroundColor() {
        return backgroundColor.get();
    }

    public void setBackgroundColor(Paint backgroundColor) {
        this.backgroundColor.set(backgroundColor);
        this.setStyle(this.getStyle() + "; -fx-background-color: " + backgroundColor.toString());
    }

}
