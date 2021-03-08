package org.blueshard.theosUI.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Logger;
import org.blueshard.theosUI.Main;
import org.blueshard.theosUI.UIStarter.Register;
import org.blueshard.theosUI.client.Action;
import org.blueshard.theosUI.data.DataCodes;
import org.blueshard.theosUI.exception.ErrorCodes;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.theosFX.TFXPasswordField;
import org.blueshard.theosUI.utils.SVGToGenericImage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private AnchorPane root;

    @FXML private ImageView theosLogo;
    @FXML private ImageView usernameImage;
    @FXML private JFXTextField usernameInput;
    @FXML private ImageView passwordImage;
    @FXML private TFXPasswordField passwordInput;
    @FXML private Button login;
    @FXML private JFXButton passwordForget;
    @FXML private Text result;

    private Action action;
    private Logger logger = Main.getLogger();

    public void toRegister() throws IOException {
        Thread t = new Thread(() -> Platform.runLater(() -> ((Stage) root.getScene().getWindow()).close()));
        t.start();
        new Register(((Stage) root.getScene().getWindow()).getOwner()).getStage().show();
    }

    public void login() throws IOException, InvalidKeySpecException, UnexpectedException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
        int resultCode = action.login(usernameInput.getText(), passwordInput.getText());

        switch (resultCode) {
            case DataCodes.Server.LOGINFAIL:
                result.setFill(Color.RED);
                result.setText("Login fehlgeschlagen");
                logger.warn("Login failed");
                break;
            case DataCodes.Server.LOGINSUCCESS:
                result.setFill(Color.GREEN);
                result.setText("Erfolgreich eingeloggt");
                logger.info("Logged in successfully");

                Thread t = new Thread(() -> Platform.runLater(() -> {
                    ((Stage) root.getScene().getWindow()).close();
                }));
                t.start();

                break;
            default:
                result.setFill(Color.RED);
                result.setText("Der Server hat eine unerwartete antwort gesendet, bitte erneut versuchen");
                String name = "";
                int code;
                DataCodes.Server server = new DataCodes.Server();
                Field[] fields = ErrorCodes.class.getFields();

                for (Field field : fields) {
                    try {
                        name = field.getName();
                        code = (int) field.get(server);
                        if (code == resultCode) {
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                logger.warn("The server sent an unexpected result (" + name + ")");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            theosLogo.setImage(new Image(new SVGToGenericImage(this.getClass().getResource("../resources/images/theos_logo.svg").getFile(),
                    SVGToGenericImage.Transcoder.PNG, (float) theosLogo.getFitHeight(), (float) theosLogo.getFitWidth()).asByteArrayInputStream()));
            usernameImage.setImage(new Image(new SVGToGenericImage(this.getClass().getResource("../resources/images/user.svg").getFile(),
                    SVGToGenericImage.Transcoder.PNG, (float) usernameImage.getFitHeight(), (float) usernameImage.getFitWidth()).asByteArrayInputStream()));
            passwordImage.setImage(new Image(new SVGToGenericImage(this.getClass().getResource("../resources/images/lock.svg").getFile(),
                    SVGToGenericImage.Transcoder.PNG, (float) usernameImage.getFitHeight(), (float) usernameImage.getFitWidth()).asByteArrayInputStream()));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
            return;
        }

        action = Main.getAction();
    }
}
