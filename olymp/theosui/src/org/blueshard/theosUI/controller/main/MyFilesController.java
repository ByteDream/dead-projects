package org.blueshard.theosUI.controller.main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.batik.transcoder.TranscoderException;
import org.blueshard.theosUI.Main;
import org.blueshard.theosUI.client.Action;
import org.blueshard.theosUI.exception.IllegalCodeException;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.theosFX.TFXFileItem;
import org.blueshard.theosUI.utils.SVGToGenericImage;
import org.blueshard.theosUI.utils.TheosUIImage;
import org.blueshard.theosUIWindow.Window;
import org.blueshard.theosUIWindow.WindowPreferences;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;

public class MyFilesController extends Window implements Initializable {

    private Action action = Main.getAction();

    @FXML private BorderPane root;
    @FXML private JFXButton upload;
    @FXML private VBox filesBox;

    public MyFilesController() {
        super("Meine Dateien");
    }

    @Override
    public Node searchContainer() {
        ScrollPane searchContainer = new ScrollPane();
        searchContainer.setPrefSize(WindowPreferences.WIDTH, WindowPreferences.HEIGHT);
        searchContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return searchContainer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            upload.setGraphic(new ImageView(new Image(new SVGToGenericImage(new TheosUIImage(TheosUIImage.ImageName.option_dots).asStream(),
                    SVGToGenericImage.Transcoder.PNG, (float) upload.getPrefHeight() - 10, (float) upload.getPrefWidth() - 10).asByteArrayInputStream())));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        try {
            Object[] filesData = action.getFilesData("/");
            HashSet<String> directories = (HashSet<String>) filesData[0];
            HashMap<String, String> files = (HashMap<String, String>) filesData[1];

            String[] realFiles = new String[directories.size() + files.size()];

            Iterator<String> iterator = directories.iterator();
            int index = 0;

            while (iterator.hasNext()) {
                realFiles[index] = iterator.next();
                index++;
            }

            iterator = files.keySet().iterator();

            while (iterator.hasNext()) {
                realFiles[index] = iterator.next();
            }

            Arrays.sort(realFiles, String::compareToIgnoreCase);

            for (String realFile : realFiles) {
                System.out.println(realFile);
                TFXFileItem fileItem = new TFXFileItem(realFile, "Coming soon...", files.get(realFile));

                fileItem.setPrefSize(filesBox.getPrefWidth(), filesBox.getPrefHeight());

                filesBox.getChildren().add(fileItem);
                addSearchItem(realFile, fileItem);
            }
        } catch (UnexpectedException | IOException | GeneralSecurityException | IllegalCodeException e) {
            e.printStackTrace();
        }
    }
}
