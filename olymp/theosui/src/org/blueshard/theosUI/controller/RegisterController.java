package org.blueshard.theosUI.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.blueshard.theosUI.UIStarter.Login;
import org.blueshard.theosUI.Main;
import org.blueshard.theosUI.client.Action;
import org.blueshard.theosUI.data.DataCodes;
import org.blueshard.theosUI.exception.ErrorCodes;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.theosFX.TFXPasswordField;
import org.blueshard.theosUI.utils.UIUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView theosLogo;
    @FXML private JFXTextField registerCode;
    @FXML private JFXTextField usernameInput;
    @FXML private JFXTextField mailInput;
    @FXML private TFXPasswordField passwordInput;
    @FXML private Text passwordSafety;
    @FXML private TFXPasswordField rePasswordInput;
    @FXML private Text passwordMatch;
    @FXML private JFXCheckBox keepLoggedIn;
    @FXML private Text result;

    private Action action = Main.getAction();
    private Logger logger = Main.getLogger();
    private boolean doPasswordsMatch = false;

    public void toLogin() throws IOException {
        Thread t = new Thread(() -> Platform.runLater(() -> ((Stage) root.getScene().getWindow()).close()));
        t.start();
        new Login(((Stage) root.getScene().getWindow()).getOwner()).getStage().show();
    }
    public void register() throws IOException, InvalidKeySpecException, UnexpectedException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
        if (!doPasswordsMatch) {
            result.setFill(Color.RED);
            result.setText("Die Passwörter stimmen nicht überein");
        } else if (!action.registerCode(registerCode.getText().trim())) {
            result.setFill(Color.RED);
            result.setText("Der Registrierungscode '" + registerCode.getText() + "' existiert nicht");
        } else {
            int resultCode;
            resultCode = action.register(usernameInput.getText(), passwordInput.getText(), mailInput.getText());

            switch (resultCode) {
                case DataCodes.Server.REGISTERFAIL:
                    result.setFill(Color.RED);
                    result.setText("Registrierung fehlgeschlagen");
                    logger.warn("Register failed");
                    break;
                case DataCodes.Server.REGISTERFAIL_USER_EXIST:
                    result.setFill(Color.RED);
                    result.setText("Der Nutzer existiert bereits");
                    logger.warn("Register failed, the user already exist");
                    break;
                case DataCodes.Server.REGISTERSUCCESS:
                    result.setFill(Color.GREEN);
                    result.setText("Erfolgreich registriert");
                    logger.info("Registered successfully");
                    break;
                case DataCodes.Server.REGISTERCODE_NOT_EXIST:
                    result.setFill(Color.RED);
                    result.setText("Der Registrierungscode existiert nicht");
                    logger.warn("Register failed, the register code doesn't exist");
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rePasswordInput.addTextListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(passwordInput.getText())) {
                passwordMatch.setVisible(true);
                doPasswordsMatch = false;
            } else {
                passwordMatch.setVisible(false);
                doPasswordsMatch = true;
            }
        });
    }
}
